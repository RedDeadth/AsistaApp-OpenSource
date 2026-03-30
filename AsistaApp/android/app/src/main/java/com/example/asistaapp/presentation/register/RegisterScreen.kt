package com.example.asistaapp.presentation.register

import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.asistaapp.domain.port.AuthRepository

@Composable
fun RegisterScreen(
    repository: AuthRepository,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Column {
        Text("Register Screen")
        Button(onClick = onRegisterSuccess) { Text("Register") }
        Button(onClick = onNavigateToLogin) { Text("Go to Login") }
    }
}
