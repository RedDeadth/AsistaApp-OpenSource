package com.example.login_app.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.login_app.data.AuthRepository
import com.example.login_app.data.models.AuthResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val response: AuthResponse) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun register(username: String, password: String, email: String, onResult: (AuthResponse?, String?) -> Unit) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            repository.register(username, password, email).fold(
                onSuccess = { response ->
                    _registerState.value = RegisterState.Success(response)
                    onResult(response, null)
                },
                onFailure = { exception ->
                    _registerState.value = RegisterState.Error(exception.message ?: "Error desconocido")
                    onResult(null, exception.message)
                }
            )
        }
    }
}

class RegisterViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}