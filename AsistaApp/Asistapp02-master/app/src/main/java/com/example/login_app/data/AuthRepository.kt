package com.example.login_app.data

import com.example.login_app.data.models.AuthRequest
import com.example.login_app.data.models.AuthResponse
import com.google.gson.Gson
import retrofit2.HttpException
import com.example.login_app.data.models.ErrorResponse
import com.example.login_app.utils.SessionManager

class AuthRepository(
    private val api: AuthService,
    private val sessionManager: SessionManager
) {
    suspend fun login(username: String, password: String): Result<AuthResponse> {
        if (username.isEmpty() || password.isEmpty()) {
            return Result.failure(Exception("Username and password cannot be empty"))
        }

        return try {
            val response = api.login(AuthRequest(username, password))
            if (response.access != null) {
                sessionManager.saveAuthToken(response.access)
                sessionManager.saveUsername(username)
                Result.success(response)
            } else {
                Result.failure(Exception(response.error ?: "Error desconocido en la respuesta"))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = try {
                Gson().fromJson(errorBody, ErrorResponse::class.java)
            } catch (e: Exception) {
                null
            }
            Result.failure(Exception(errorResponse?.error ?: "Error del servidor: ${e.code()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun register(username: String, password: String, email: String): Result<AuthResponse> {
        return try {
            val response = api.register(AuthRequest(username, password, email))
            Result.success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = try {
                Gson().fromJson(errorBody, ErrorResponse::class.java)
            } catch (e: Exception) {
                null
            }
            Result.failure(Exception(errorResponse?.error ?: "Error en el registro: ${e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    fun logout() {
        sessionManager.clearSession()
    }

    fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()
}