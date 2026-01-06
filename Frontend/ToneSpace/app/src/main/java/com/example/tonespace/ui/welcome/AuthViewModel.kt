package com.example.tonespace.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tonespace.network.ForgotPasswordRequest
import com.example.tonespace.network.ResetPasswordRequest
import com.example.tonespace.network.RetrofitClient
import com.example.tonespace.network.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    private fun isPasswordValid(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$".toRegex()
        return password.matches(passwordPattern)
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = RetrofitClient.api.login(email, password)
                val body = response.body()
                if (response.isSuccessful && body?.success == true && body.user != null) {
                    _authState.value = AuthState.Success(body.user)
                } else {
                    _authState.value = AuthState.Error(body?.message ?: "Login failed")
                }
            } catch (e: Exception) {
                val errorMessage = if (e is IllegalStateException) {
                    "Server Error: Received invalid data. Please check login.php script."
                } else {
                    "Network Error: ${e.message}"
                }
                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }

    fun register(email: String, password: String) {
        if (!isPasswordValid(password)) {
            _authState.value = AuthState.Error("Password must be 8+ chars and include an uppercase, lowercase, and number.")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = RetrofitClient.api.register(email, password)
                if (response.isSuccessful && response.body()?.success == true) {
                    _authState.value = AuthState.Message("Registration successful! Please log in.")
                } else {
                    _authState.value = AuthState.Error(response.body()?.message ?: "Registration failed")
                }
            } catch (e: Exception) {
                 val errorMessage = if (e is IllegalStateException) {
                    "Server Error: Received invalid data. Please check register.php script."
                } else {
                    "Network Error: ${e.message}"
                }
                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = RetrofitClient.api.forgotPassword(ForgotPasswordRequest(email))
                if (response.isSuccessful && response.body()?.success == true) {
                    _authState.value = AuthState.Message("Reset link sent. Check your email.")
                } else {
                    _authState.value = AuthState.Error(response.body()?.message ?: "Failed to send reset link")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Network error: ${e.message}")
            }
        }
    }

    // ADDED: Function to handle the in-app password reset
    fun resetPassword(email: String, newPassword: String) {
        if (!isPasswordValid(newPassword)) {
            _authState.value = AuthState.Error("New password must be 8+ chars and include an uppercase, lowercase, and number.")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val request = ResetPasswordRequest(email, newPassword)
                val response = RetrofitClient.api.resetPassword(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    _authState.value = AuthState.Success(User(-1, null, email, null, null, null, null)) // Send success to navigate
                } else {
                    _authState.value = AuthState.Error(response.body()?.message ?: "Failed to reset password.")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Network Error: ${e.message}")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
    data class Message(val text: String) : AuthState()
}
