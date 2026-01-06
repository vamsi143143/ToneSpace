package com.example.tonespace.ui.welcome

/**
 * A sealed class representing the shared UI state for asynchronous operations.
 */
sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val message: String) : UiState()
    data class Error(val message: String) : UiState()
}
