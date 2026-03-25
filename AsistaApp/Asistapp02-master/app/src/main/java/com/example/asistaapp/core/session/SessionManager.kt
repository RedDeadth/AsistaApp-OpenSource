package com.example.asistaapp.core.session

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) = prefs.edit().putString(KEY_TOKEN, token).apply()

    fun getAuthToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun saveUsername(username: String) = prefs.edit().putString(KEY_USERNAME, username).apply()

    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)

    fun isLoggedIn(): Boolean = getAuthToken() != null

    fun clearSession() = prefs.edit().clear().apply()

    companion object {
        private const val PREFS_NAME = "asista_prefs"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USERNAME = "username"
    }
}
