package com.example.asistaapp.data.repository

import com.example.asistaapp.core.session.SessionManager
import com.example.asistaapp.data.remote.dto.AuthRequestDto
import com.example.asistaapp.data.remote.dto.ErrorResponseDto
import com.example.asistaapp.data.remote.service.AuthApiService
import com.example.asistaapp.domain.model.AuthToken
import com.example.asistaapp.domain.port.AuthRepository
import com.google.gson.Gson
import retrofit2.HttpException

class AuthRepositoryImpl(
    private val api: AuthApiService,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<AuthToken> {
        if (username.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Username and password cannot be empty"))
        }
        return try {
            val response = api.login(AuthRequestDto(username, password))
            if (response.access != null) {
                sessionManager.saveAuthToken(response.access)
                sessionManager.saveUsername(username)
                Result.success(AuthToken(accessToken = response.access, username = username))
            } else {
                Result.failure(Exception(response.error ?: "Unknown error"))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val parsed = runCatching { Gson().fromJson(errorBody, ErrorResponseDto::class.java) }.getOrNull()
            Result.failure(Exception(parsed?.error ?: "Server error: ${e.code()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Connection error: ${e.message}"))
        }
    }

    override suspend fun register(username: String, password: String, email: String): Result<AuthToken> {
        return try {
            val response = api.register(AuthRequestDto(username, password, email))
            if (response.access != null) {
                Result.success(AuthToken(accessToken = response.access, username = username))
            } else {
                Result.failure(Exception(response.error ?: "Unknown error"))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val parsed = runCatching { Gson().fromJson(errorBody, ErrorResponseDto::class.java) }.getOrNull()
            Result.failure(Exception(parsed?.error ?: "Registration error: ${e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }

    override fun logout() = sessionManager.clearSession()

    override fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()
}
