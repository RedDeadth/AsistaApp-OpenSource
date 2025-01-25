package com.example.login_app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Esquema de color único ya que usamos los mismos colores para ambos temas
private val ColorScheme = lightColorScheme(
    primary = WindowsXpBlue,
    secondary = WindowsXpGrass,
    background = WindowsXpSky,
    surface = Color.White,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    primaryContainer = Color(0xFFFF4081),
    onPrimaryContainer = Color.White,
    surfaceVariant = Color.White.copy(alpha = 0.1f),
    onSurfaceVariant = Color.Black
)

@Composable
fun LoginAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}