package com.example.tonespace.ui.welcome

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tonespace.R
import com.example.tonespace.network.DeleteSavedItemRequest
import com.example.tonespace.network.RetrofitClient
import com.example.tonespace.network.SavedItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

// MODIFIED: This data class is no longer needed for text-only recommendations
// data class FurnitureItem(val name: String, val imageRes: Int)

class DesignViewModel : ViewModel() {

    private val _imageUri = MutableStateFlow<String?>(null)
    val imageUri = _imageUri.asStateFlow()

    private val _selectedVibe = MutableStateFlow("Minimal")
    val selectedVibe = _selectedVibe.asStateFlow()

    private val _savedItems = MutableStateFlow<List<SavedItem>>(emptyList())
    val savedItems = _savedItems.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun setImageUri(uri: String?) {
        _imageUri.value = uri
    }

    fun setSelectedVibe(vibe: String) {
        _selectedVibe.value = vibe
    }

    fun saveDesign(context: Context, userId: Int, title: String) {
        val uriString = _imageUri.value ?: return
        val uri = Uri.parse(uriString)

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val tempFile = createTempFileFromUri(context, uri)

                if (tempFile == null) {
                    _uiState.value = UiState.Error("Failed to read image file.")
                    return@launch
                }

                val requestFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("design_image", tempFile.name, requestFile)

                val userIdBody = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())

                val response = RetrofitClient.api.saveDesign(userIdBody, titleBody, body)

                tempFile.delete()

                if (response.isSuccessful && response.body()?.success == true) {
                    _uiState.value = UiState.Success("Design saved successfully!")
                } else {
                    _uiState.value = UiState.Error(response.body()?.message ?: "Failed to save design.")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun fetchSavedItems(userId: Int) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = RetrofitClient.api.getSavedItems(userId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _savedItems.value = response.body()?.savedItems ?: emptyList()
                    _uiState.value = UiState.Idle
                } else {
                    _uiState.value = UiState.Error("Failed to load saved items.")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Network Error: ${e.message}")
            }
        }
    }

    fun deleteSavedItem(userId: Int, savedItemId: Int) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val request = DeleteSavedItemRequest(savedItemId, userId)
                val response = RetrofitClient.api.deleteSavedItem(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    fetchSavedItems(userId)
                    _uiState.value = UiState.Success("Item deleted successfully.")
                } else {
                    _uiState.value = UiState.Error(response.body()?.message ?: "Failed to delete item.")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Network Error: ${e.message}")
            }
        }
    }

    fun getColorPaletteForVibe(vibe: String): List<Color> {
        return when (vibe.lowercase()) {
            "calm" -> listOf(Color(0xFFADC8D3), Color(0xFFE4F0F4), Color(0xFF7B9EAB), Color(0xFFF7FAFB))
            "cozy" -> listOf(Color(0xFFB98B73), Color(0xFFD5B9B2), Color(0xFF8D6E63), Color(0xFFFFF8E1))
            "energetic" -> listOf(Color(0xFFFFB74D), Color(0xFFF48FB1), Color(0xFF4FC3F7), Color(0xFFAED581))
            "focus" -> listOf(Color(0xFF37474F), Color(0xFFB0BEC5), Color(0xFF607D8B), Color(0xFFECEFF1))
            "luxury" -> listOf(Color(0xFF4E342E), Color(0xFFD7CCC8), Color(0xFF8D6E63), Color(0xFF3E2723))
            "minimal" -> listOf(Color(0xFFF5F5F5), Color(0xFFE0E0E0), Color(0xFFBDBDBD), Color(0xFF424242))
            "playful" -> listOf(Color(0xFFF06292), Color(0xFF4FC3F7), Color(0xFFFFD54F), Color(0xFF81C784))
            "romantic" -> listOf(Color(0xFFF48FB1), Color(0xFFF8BBD0), Color(0xFFEC407A), Color(0xFFFCE4EC))
            else -> listOf(Color(0xFFF5F5F5), Color(0xFFE0E0E0), Color(0xFFBDBDBD), Color(0xFF424242))
        }
    }

    // CORRECTED: This function now returns a simple list of strings.
    fun getFurnitureForVibe(vibe: String): List<String> {
        return when (vibe.lowercase()) {
            "calm" -> listOf("Linen Sofa", "Light Wood Table", "Potted Plant")
            "cozy" -> listOf("Plush Armchair", "Knit Blanket", "Warm Area Rug")
            "energetic" -> listOf("Bold Accent Chair", "Geometric Bookshelf", "Colorful Wall Art")
            "focus" -> listOf("Ergonomic Desk Chair", "Minimalist Desk", "Task Lamp")
            "luxury" -> listOf("Velvet Couch", "Marble Coffee Table", "Gold Floor Lamp")
            "minimal" -> listOf("Simple Bookshelf", "Neutral Tone Sofa", "Slim Coffee Table")
            "playful" -> listOf("Bean Bag Chair", "Funky Patterned Rug", "Bright Throw Pillows")
            "romantic" -> listOf("Canopy Bed", "Sheer Curtains", "Mood Lighting")
            else -> listOf("Generic Chair", "Generic Table", "Generic Lamp")
        }
    }

    fun resetUiState() {
        _uiState.value = UiState.Idle
    }
}

private fun createTempFileFromUri(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
        val fileOutputStream = FileOutputStream(tempFile)
        inputStream.use { input ->
            fileOutputStream.use { output ->
                input.copyTo(output)
            }
        }
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

class DesignViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DesignViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DesignViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
