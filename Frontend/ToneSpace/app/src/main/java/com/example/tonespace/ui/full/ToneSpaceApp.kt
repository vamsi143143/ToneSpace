package com.example.tonespace.ui.full

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tonespace.ui.welcome.DesignViewModelFactory
import com.example.tonespace.ui.welcome.NewDesignScreen
import com.example.tonespace.ui.welcome.UploadSuccessScreen

/**
 * This file has been updated to use a shared ViewModel for navigation,
 * which is a more robust and modern approach that prevents crashes from long URIs.
 */
@Composable
fun ToneSpaceApp() {
    val navController = rememberNavController()
    // The shared ViewModel is the single source of truth for the design flow.
    val designViewModel: com.example.tonespace.ui.welcome.DesignViewModel = viewModel(factory = DesignViewModelFactory())

    NavHost(
        navController = navController,
        startDestination = "new_design"
    ) {
        composable("new_design") {
            NewDesignScreen(
                onCloseClick = { /* No-op for this test */ },
                // CORRECTED: The ViewModel now handles the data.
                // We just need to navigate to the next screen.
                onImagePicked = { navController.navigate("upload_success") },
                designViewModel = designViewModel
            )
        }

        // CORRECTED: The route is now simple, as the ViewModel holds the URI.
        composable("upload_success") { 
            UploadSuccessScreen(
                onCloseClick = { navController.popBackStack("new_design", inclusive = true) },
                onGenerateIdeasClick = { /* No-op for this test */ },
                onUploadDifferentPhotoClick = { navController.popBackStack() },
                designViewModel = designViewModel
            )
        }
    }
}
