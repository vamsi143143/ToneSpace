package com.example.tonespace.ui.welcome

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInformationScreen(onBackClick: () -> Unit) {

    val context = LocalContext.current
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(SessionManager(context)))

    val user by viewModel.userState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }

    LaunchedEffect(user) {
        user?.let {
            name = it.name ?: ""
            phone = it.phone ?: ""
            gender = it.gender ?: ""
            dob = it.dob ?: ""
        }
    }

    LaunchedEffect(uiState) {
        val currentState = uiState
        if (currentState is UiState.Success) {
            Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT).show()
            viewModel.resetUiState()
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personal Information") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetUiState()
                        onBackClick()
                    }) {
                        Text("<", fontSize = 22.sp)
                    }
                }
            )
        }
    ) { pad ->
        Column(
            Modifier.padding(pad).padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            InfoField("Full Name", name) { name = it }
            InfoField("Phone", phone) { phone = it }
            InfoField("Gender", gender) { gender = it }
            InfoField("DOB (YYYY-MM-DD)", dob) { dob = it }

            Spacer(Modifier.height(24.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is UiState.Loading,
                onClick = { viewModel.updateUserProfile(name, phone, gender, dob) }
            ) {
                if (uiState is UiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Save")
                }
            }

            // --- ADDED: Raw Debug Text ---
            // This will show the exact state of the UI from the ViewModel.
            Spacer(Modifier.height(20.dp))
            Text(
                text = "DEBUG: Current UI State is: $uiState",
                color = Color.Gray,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun InfoField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(Modifier.padding(bottom = 16.dp)) {
        Text(label, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

class ProfileViewModelFactory(private val sessionManager: SessionManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
