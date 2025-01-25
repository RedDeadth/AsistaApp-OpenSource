package com.example.login_app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.login_app.RetrofitInstance
import com.example.login_app.data.AuthRepository
import com.example.login_app.ui.theme.WindowsXpBlue
import com.example.login_app.ui.theme.WindowsXpGrass
import com.example.login_app.ui.theme.WindowsXpSky
import com.example.login_app.utils.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavHostController,
    username: String?,
    sessionManager: SessionManager = SessionManager(LocalContext.current)
) {
    val context = LocalContext.current
    val currentUsername = remember { mutableStateOf(username) }
    val repository = AuthRepository(RetrofitInstance.api, sessionManager)
    val scope = rememberCoroutineScope()

    val composition by rememberLottieComposition(
        // Puedes usar una URL o un archivo raw
        LottieCompositionSpec.Url("https://assets9.lottiefiles.com/packages/lf20_M9p23l.json")
        // O si prefieres usar un archivo local:
        // LottieCompositionSpec.RawRes(R.raw.waving_person)
    )

    // Estado de la animación
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(WindowsXpSky, WindowsXpGrass)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "¡Bienvenido ${currentUsername.value ?: ""}!",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = WindowsXpBlue,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Sesión iniciada: ${
                    java.time.LocalDateTime.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                }",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = WindowsXpBlue,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Button(
                    onClick = {
                        navController.navigate("faceRegister")
                    },
                    modifier = Modifier
                        .width(250.dp) // Ancho más estrecho
                        .height(60.dp), // Altura más grande
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WindowsXpBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(9.dp)
                ) {
                    Text("Registrar Rostro")
                }

                Spacer(modifier = Modifier.height(8.dp)) // Espaciado reducido entre botones

                Button(
                    onClick = {
                        navController.navigate("location_catch")
                    },
                    modifier = Modifier
                        .width(250.dp) // Ancho más estrecho
                        .height(60.dp), // Altura más grande
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WindowsXpBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(9.dp)
                ) {
                    Text("Registrar Asistencia")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                        scope.launch {
                            delay(100)
                            sessionManager.clearSession()
                        }
                    },
                    modifier = Modifier
                        .width(250.dp)
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(9.dp)
                ) {
                    Text("Cerrar sesión")
                }
            }
        }
    }
}