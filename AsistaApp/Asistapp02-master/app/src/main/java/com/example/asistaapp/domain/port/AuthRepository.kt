package com.example.asistaapp.domain.port

import com.example.asistaapp.domain.model.AuthToken

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<AuthToken>
    suspend fun register(username: String, password: String, email: String): Result<AuthToken>
    fun logout()
    fun isLoggedIn(): Boolean
}
