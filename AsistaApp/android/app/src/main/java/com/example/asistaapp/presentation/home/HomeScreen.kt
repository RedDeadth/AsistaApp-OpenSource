package com.example.asistaapp.presentation.home

import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.asistaapp.core.session.SessionManager

@Composable
fun HomeScreen(
    sessionManager: SessionManager,
    onNavigateToFace: () -> Unit,
    onNavigateToLocation: () -> Unit,
    onLogout: () -> Unit
) {
    Column {
        Text("Home Screen")
        Button(onClick = onNavigateToFace) { Text("Face Recognition") }
        Button(onClick = onNavigateToLocation) { Text("Location") }
        Button(onClick = onLogout) { Text("Logout") }
    }
}
