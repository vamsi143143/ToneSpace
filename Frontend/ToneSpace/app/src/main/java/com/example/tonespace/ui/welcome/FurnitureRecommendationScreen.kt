package com.example.tonespace.ui.welcome

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FurnitureRecommendationScreen(
    vibe: String,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    designViewModel: DesignViewModel = viewModel(factory = DesignViewModelFactory())
) {
    // Get the list of furniture names from the ViewModel
    val furnitureItems = designViewModel.getFurnitureForVibe(vibe)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$vibe Furniture") },
                navigationIcon = { IconButton(onClick = onBackClick) { Text("<") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Here are some furniture recommendations that match the $vibe vibe.",
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Display the furniture names in a simple list
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(furnitureItems) { itemName ->
                    Text(
                        text = "• $itemName",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Divider()
                }
            }

            Button(
                onClick = onNextClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Next")
            }
        }
    }
}
