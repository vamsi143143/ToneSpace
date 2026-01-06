package com.example.tonespace.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tonespace.network.* // Import all network models
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val sessionManager: SessionManager) : ViewModel() {

    private val _userState = MutableStateFlow<User?>(null)
    val userState = _userState.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _notificationsState = MutableStateFlow<List<Notification>>(emptyList())
    val notificationsState = _notificationsState.asStateFlow()

    private val _helpArticlesState = MutableStateFlow<List<HelpArticle>>(emptyList())
    val helpArticlesState = _helpArticlesState.asStateFlow()

    init {
        _userState.value = sessionManager.getFullUser()
        fetchUserProfile()
    }

    fun fetchUserProfile() {
        val userId = sessionManager.getUserId()
        if (userId == -1) return
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getUserProfile(userId)
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.user?.let {
                        _userState.value = it
                        sessionManager.saveUser(it)
                    }
                }
            } catch (e: Exception) { /* Don't show error on initial fetch */ }
        }
    }

    fun updateUserProfile(name: String, phone: String, gender: String, dob: String) {
        val userId = sessionManager.getUserId()
        if (userId == -1) {
            _uiState.value = UiState.Error("User session expired. Please log in again.")
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val request = UpdateProfileRequest(userId = userId.toString(), name = name, phone = phone, gender = gender, dob = dob)
                val response = RetrofitClient.api.updateUserProfile(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    val updatedUser = _userState.value?.copy(name = name, phone = phone, gender = gender, dob = dob)
                    if(updatedUser != null) {
                        sessionManager.saveUser(updatedUser)
                        _userState.value = updatedUser
                    }
                    _uiState.value = UiState.Success("Profile updated successfully!")
                } else {
                    _uiState.value = UiState.Error(response.body()?.message ?: "Failed to update profile.")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Network error: ${e.message}")
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        val userId = sessionManager.getUserId()
        if (userId == -1) {
            _uiState.value = UiState.Error("User session expired. Please log in again.")
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val request = ChangePasswordRequest(userId = userId, currentPassword = currentPassword, newPassword = newPassword)
                val response = RetrofitClient.api.changePassword(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    _uiState.value = UiState.Success("Password changed successfully!")
                } else {
                    _uiState.value = UiState.Error(response.body()?.message ?: "Failed to change password.")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Network Error: ${e.message}")
            }
        }
    }

    fun fetchNotifications() {
        val userId = sessionManager.getUserId()
        if (userId == -1) return
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = RetrofitClient.api.getNotifications(userId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _notificationsState.value = response.body()?.notifications ?: emptyList()
                    _uiState.value = UiState.Idle
                } else {
                    _uiState.value = UiState.Error("Failed to load notifications.")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Network Error: ${e.message}")
            }
        }
    }

    fun fetchHelpArticles() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = RetrofitClient.api.getHelpArticles()
                if (response.isSuccessful && response.body()?.success == true) {
                    _helpArticlesState.value = response.body()?.articles ?: emptyList()
                    _uiState.value = UiState.Idle
                } else {
                    _uiState.value = UiState.Error("Failed to load help articles.")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Network Error: ${e.message}")
            }
        }
    }

    fun sendContactMessage(message: String) {
        val user = sessionManager.getFullUser()
        if (user == null) {
            _uiState.value = UiState.Error("User session expired. Please log in again.")
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val request = ContactUsRequest(
                    userId = user.id,
                    name = user.name ?: "",
                    email = user.email,
                    message = message
                )
                val response = RetrofitClient.api.contactUs(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    _uiState.value = UiState.Success("Message sent successfully!")
                } else {
                    _uiState.value = UiState.Error(response.body()?.message ?: "Failed to send message.")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Network Error: ${e.message}")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = UiState.Idle
    }
}
// REMOVED: Duplicate UiState class
