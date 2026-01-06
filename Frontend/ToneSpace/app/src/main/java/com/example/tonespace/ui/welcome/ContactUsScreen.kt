package com.example.tonespace.ui.welcome

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactUsScreen(onBackClick: () -> Unit) {

    val context = LocalContext.current
    // Re-use the ProfileViewModel which already has access to the user's session
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(SessionManager(context)))
    val uiState by viewModel.uiState.collectAsState()

    var message by remember { mutableStateOf("") }

    // Listen for success or error states from the ViewModel
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is UiState.Success -> {
                // The backend now returns "Thanks for your feedback!"
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetUiState()
                onBackClick() // Go back to the previous screen
            }
            is UiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetUiState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contact Us") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("<", fontSize = 22.sp)
                    }
                }
            )
        }
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .padding(16.dp)) {
            Text(
                text = "We\'d love to hear from you",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Your Message") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is UiState.Loading, // Disable button while loading
                onClick = {
                    if (message.isBlank()) {
                        Toast.makeText(context, "Message cannot be empty.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    // Trigger the send message function in the ViewModel
                    viewModel.sendContactMessage(message)
                }
            ) {
                if (uiState is UiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Send Message")
                }
            }
        }
    }
}
