package com.example.login_app.data.models

data class AuthRequest(
    val username: String,
    val password: String,
    val email: String? = null
)