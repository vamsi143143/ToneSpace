package com.example.tonespace.ui.welcome

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
// ADDED: Import for the .clip() modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tonespace.BuildConfig
import java.io.File

@Composable
fun NewDesignScreen(
    onCloseClick: () -> Unit,
    onImagePicked: () -> Unit, // Renamed to onContinueClick for clarity
    designViewModel: DesignViewModel = viewModel(factory = DesignViewModelFactory())
) {
    val context = LocalContext.current
    var tempImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val imageUri by designViewModel.imageUri.collectAsState()

    // --- Camera and Gallery Launchers ---

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                designViewModel.setImageUri(uri.toString())
            } else {
                Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success: Boolean ->
            if (success) {
                tempImageUri?.let { designViewModel.setImageUri(it.toString()) }
            } else {
                Toast.makeText(context, "Camera cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted: Boolean ->
            if (granted) {
                val newImageUri = createImageUri(context)
                if (newImageUri != null) {
                    tempImageUri = newImageUri
                    cameraLauncher.launch(newImageUri)
                } else {
                    Toast.makeText(context, "Error: Could not create image file", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // --- UI ---

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onCloseClick) {
                Icon(Icons.Outlined.Close, contentDescription = "Close")
            }
            Spacer(modifier = Modifier.weight(1f))
            Text("New Design", fontSize = 16.sp)
            Spacer(modifier = Modifier.weight(1f))
        }

        // MODIFIED: The UI now depends on whether an image has been selected
        if (imageUri == null) {
            // ----- INITIAL STATE: No image selected -----
            InitialPrompt( 
                onUploadClick = { galleryLauncher.launch("image/*") },
                onTakePhotoClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }
            )
        } else {
            // ----- CONFIRMATION STATE: Image has been selected -----
            ImageConfirmation( 
                imageUri = imageUri,
                onContinueClick = onImagePicked,
                onClearClick = { designViewModel.setImageUri(null) } // Clear selection
            )
        }
    }
}

@Composable
private fun InitialPrompt(onUploadClick: () -> Unit, onTakePhotoClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(40.dp))
        Text("Let's get started", fontSize = 24.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Upload a photo of your room or take a new one", color = Color.Gray, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onUploadClick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(26.dp)
        ) {
            Text("Upload Photo")
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = onTakePhotoClick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(26.dp)
        ) {
            Text("Take Photo")
        }
    }
}

@Composable
private fun ImageConfirmation(imageUri: String?, onContinueClick: () -> Unit, onClearClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(24.dp))
        Text("Your Photo", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        AsyncImage(
            model = imageUri,
            contentDescription = "Selected room photo",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onContinueClick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(26.dp)
        ) {
            Text("Continue")
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onClearClick) {
            Text("Choose a different photo")
        }
    }
}

private fun createImageUri(context: Context): Uri? {
    return try {
        val imageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile(
            "JPEG_${System.currentTimeMillis()}_",
            ".jpg",
            imageDir
        )
        FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.provider",
            imageFile
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
