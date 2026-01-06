package com.example.tonespace.ui.welcome

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignComparisonScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onRedesignClick: () -> Unit,
    designViewModel: DesignViewModel = viewModel(factory = DesignViewModelFactory()),
    sessionManager: SessionManager = SessionManager(LocalContext.current)
) {
    val context = LocalContext.current
    val imageUri by designViewModel.imageUri.collectAsState()
    val selectedVibe by designViewModel.selectedVibe.collectAsState()
    val uiState by designViewModel.uiState.collectAsState()

    val colorPalette = designViewModel.getColorPaletteForVibe(selectedVibe)
    val overlayColor = colorPalette.firstOrNull() ?: Color.Transparent

    LaunchedEffect(uiState) {
        val currentState = uiState
        if (currentState is UiState.Success) {
            Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT).show()
            designViewModel.resetUiState()
            onSaveClick()
        }
        if (currentState is UiState.Error) {
            Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
            designViewModel.resetUiState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Design Comparison") },
                navigationIcon = { IconButton(onClick = onBackClick) { Text("<") } })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            // --- Comparison Row ---
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ----- Before Image -----
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Original", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Original Design",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                // ----- After Image (with Color Filter) -----
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Redesigned", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Redesigned Design",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        // This applies a color tint to simulate the new vibe
                        colorFilter = ColorFilter.colorMatrix(
                            ColorMatrix(floatArrayOf(
                                1f, 0f, 0f, 0f, overlayColor.red * 0.15f,
                                0f, 1f, 0f, 0f, overlayColor.green * 0.15f,
                                0f, 0f, 1f, 0f, overlayColor.blue * 0.15f,
                                0f, 0f, 0f, 1f, 0f
                            ))
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // --- Action Buttons ---
            Button(
                onClick = { 
                    val userId = sessionManager.getUserId()
                    if(userId != -1) {
                        designViewModel.saveDesign(context, userId, "My $selectedVibe Design")
                    } else {
                        Toast.makeText(context, "Please log in to save designs.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is UiState.Loading
            ) {
                if(uiState is UiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Save")
                }
            }
            OutlinedButton(onClick = onRedesignClick, modifier = Modifier.fillMaxWidth()) {
                Text("Redesign")
            }
        }
    }
}
