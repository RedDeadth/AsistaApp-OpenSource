package com.example.asistaapp.presentation.login

import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.asistaapp.domain.port.AuthRepository

@Composable
fun LoginScreen(
    repository: AuthRepository,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Column {
        Text("Login Screen")
        Button(onClick = onLoginSuccess) { Text("Login") }
        Button(onClick = onNavigateToRegister) { Text("Go to Register") }
    }
}
