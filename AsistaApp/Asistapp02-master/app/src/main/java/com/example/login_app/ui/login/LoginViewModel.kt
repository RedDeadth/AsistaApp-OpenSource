package com.example.login_app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.login_app.data.AuthRepository
import com.example.login_app.data.models.AuthResponse
import com.example.login_app.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(
        val token: String,
        val username: String
    ) : LoginState()
    data class Error(val message: String) : LoginState()
}
class LoginViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                repository.login(username, password).fold(
                    onSuccess = { response ->
                        _loginState.value = LoginState.Success(
                            token = response.access ?: "",
                            username = username
                        )
                    },
                    onFailure = { exception ->
                        _loginState.value = LoginState.Error(
                            exception.message ?: "Error desconocido"
                        )
                    }
                )
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}

class LoginViewModelFactory(
    private val repository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}