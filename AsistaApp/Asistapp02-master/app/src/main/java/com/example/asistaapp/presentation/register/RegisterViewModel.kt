package com.example.asistaapp.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.asistaapp.domain.model.AuthToken
import com.example.asistaapp.domain.port.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    data class Success(val token: AuthToken) : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun register(username: String, password: String, email: String) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            repository.register(username, password, email).fold(
                onSuccess = { token -> _uiState.value = RegisterUiState.Success(token) },
                onFailure = { e -> _uiState.value = RegisterUiState.Error(e.message ?: "Unknown error") }
            )
        }
    }

    fun resetState() { _uiState.value = RegisterUiState.Idle }
}

class RegisterViewModelFactory(
    private val repository: AuthRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) return RegisterViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
