package com.example.login_app.data.models

data class AuthResponse(
    val access: String? = null,  // Token JWT
    val refresh: String? = null, // Token de refresco
    val username: String? = null,
    val error: String? = null
)