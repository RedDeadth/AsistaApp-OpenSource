package com.example.asistaapp.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.asistaapp.core.session.SessionManager
import com.example.asistaapp.core.network.RetrofitProvider
import com.example.asistaapp.data.repository.AuthRepositoryImpl
import com.example.asistaapp.presentation.home.HomeScreen
import com.example.asistaapp.presentation.login.LoginScreen
import com.example.asistaapp.presentation.register.RegisterScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val FACE_RECOGNITION = "face_recognition"
    const val LOCATION = "location"
}

@Composable
fun AppNavigation(navController: NavHostController, sessionManager: SessionManager) {
    val repository = AuthRepositoryImpl(RetrofitProvider.authApiService, sessionManager)
    val startRoute = if (sessionManager.isLoggedIn()) Routes.HOME else Routes.LOGIN

    NavHost(navController = navController, startDestination = startRoute) {
        composable(Routes.LOGIN) {
            LoginScreen(
                repository = repository,
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                repository = repository,
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                sessionManager = sessionManager,
                onNavigateToFace = { navController.navigate(Routes.FACE_RECOGNITION) },
                onNavigateToLocation = { navController.navigate(Routes.LOCATION) },
                onLogout = {
                    sessionManager.clearSession()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
