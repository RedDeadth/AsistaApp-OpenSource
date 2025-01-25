package com.example.login_app.ui.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.login_app.R
import com.example.login_app.RetrofitInstance
import com.example.login_app.data.AuthRepository
import com.example.login_app.utils.SessionManager

@Composable
fun LoginScreen(
    navController: NavHostController,
    sessionManager: SessionManager
) {
    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(
            repository = AuthRepository(RetrofitInstance.api, sessionManager)
        )
    )
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            )
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .align(Alignment.Center),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .shadow(elevation = 4.dp, shape = CircleShape)
                        .scale(1.3f)
                )

                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Nombre de Usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                Button(
                    onClick = {
                        if (username.isBlank() || password.isBlank()) {
                            Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_LONG).show()
                            return@Button
                        }
                        viewModel.login(username, password)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = loginState !is LoginState.Loading
                ) {
                    if (loginState is LoginState.Loading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Iniciar Sesión")
                    }
                }

                TextButton(
                    onClick = { navController.navigate("register") },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("¿No tienes cuenta? Regístrate")
                }
            }
        }

        LaunchedEffect(loginState) {
            when (loginState) {
                is LoginState.Success -> {
                    val successState = loginState as LoginState.Success
                    sessionManager.saveAuthToken(successState.token)
                    sessionManager.saveUsername(username)
                    Toast.makeText(context, "Login exitoso", Toast.LENGTH_LONG).show()
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
                is LoginState.Error -> {
                    Toast.makeText(
                        context,
                        (loginState as LoginState.Error).message,
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {}
            }
        }
    }
}