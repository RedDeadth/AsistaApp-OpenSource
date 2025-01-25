package com.example.login_app.utils

import android.content.Context

class SessionManager(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        sharedPreferences.edit()
            .putString("auth_token", token)
            .apply()
    }

    fun saveUsername(username: String) {
        sharedPreferences.edit()
            .putString("username", username)
            .apply()
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }

    fun getUsername(): String? {
        return sharedPreferences.getString("username", null)
    }

    fun clearSession() {
        android.util.Log.d("SessionManager", "Limpiando sesión...")
        sharedPreferences.edit()
            .remove("auth_token")
            .remove("username")
            .clear()
            .commit()
    }

    fun isLoggedIn(): Boolean {
        val token = getAuthToken()
        val username = getUsername()
        return !token.isNullOrEmpty() && !username.isNullOrEmpty()
    }
}