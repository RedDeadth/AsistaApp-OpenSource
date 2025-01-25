package com.example.login_app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.login_app.ui.face.FaceComparinsonScreen
import com.example.login_app.ui.face.FaceRegisterScreen
import com.example.login_app.ui.home.HomeScreen
import com.example.login_app.ui.login.LoginScreen
import com.example.login_app.ui.register.RegisterScreen
import com.example.login_app.ui.location.LocationCatchScreen
import com.example.login_app.ui.theme.LoginAppTheme
import com.example.login_app.utils.SessionManager

class MainActivity : ComponentActivity() {
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)

        setContent {
            LoginAppTheme {
                val navController = rememberNavController()
                val startDestination = if (sessionManager.isLoggedIn()) "home" else "login"

                NavHost(navController = navController, startDestination = startDestination) {
                    composable("login") {
                        LoginScreen(
                            navController = navController,
                            sessionManager = sessionManager
                        )
                    }
                    composable("register") {
                        RegisterScreen(
                            navController = navController,
                            sessionManager = sessionManager
                        )
                    }
                    composable("home") {
                        HomeScreen(
                            navController = navController,
                            username = sessionManager.getUsername()
                        )
                    }
                    composable("location_catch") {
                        LocationCatchScreen(
                            context = this@MainActivity,
                            onLocationVerified = { success ->
                                if (success) {
                                    navController.navigate("comparisonFace")
                                }
                            }
                        )
                    }

                    composable("comparisonFace") {
                        FaceComparinsonScreen(
                            username = sessionManager.getUsername(),  // Añadir el username aquí
                            onFaceVerified = { success ->
                                if (success) {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            }
                        )
                    }
                    composable("faceRegister") {
                        FaceRegisterScreen()
                    }
                }
            }
        }
    }
}