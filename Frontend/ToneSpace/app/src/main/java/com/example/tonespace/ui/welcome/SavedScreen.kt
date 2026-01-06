package com.example.tonespace.ui.welcome

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(
    designViewModel: DesignViewModel = viewModel(factory = DesignViewModelFactory()),
    sessionManager: SessionManager = SessionManager(LocalContext.current)
) {
    val context = LocalContext.current
    val savedItems by designViewModel.savedItems.collectAsState()
    val uiState by designViewModel.uiState.collectAsState()
    val userId = sessionManager.getUserId()

    LaunchedEffect(Unit) {
        if (userId != -1) {
            designViewModel.fetchSavedItems(userId)
        }
    }

    LaunchedEffect(uiState) {
        val currentState = uiState
        if (currentState is UiState.Success) {
            Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT).show()
            designViewModel.resetUiState()
        }
        if (currentState is UiState.Error) {
            Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
            designViewModel.resetUiState()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Saved Designs") }) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState is UiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            if (savedItems.isEmpty()) {
                Text("No saved designs found.", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp)) {
                    items(savedItems) { item ->
                        Card(modifier = Modifier.padding(bottom = 16.dp)) {
                            Column {
                                AsyncImage(
                                    model = item.designImageUrl,
                                    contentDescription = item.designTitle,
                                    modifier = Modifier.fillMaxWidth().height(200.dp)
                                )
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(item.designTitle ?: "Saved Design", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                                    IconButton(onClick = { 
                                        if(userId != -1) {
                                            designViewModel.deleteSavedItem(userId, item.id)
                                        }
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Design")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
