package com.maeen.mahfilhub.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Simple session manager backed by SharedPreferences.
 * Stores login state, token, role and basic user info.
 */
object SessionManager {

    private const val PREF_NAME = "mahfilhub_session"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val KEY_USER_ROLE = "user_role"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // ── Login state ─────────────────────────────────────────────────────

    fun isLoggedIn(context: Context): Boolean =
        prefs(context).getBoolean(KEY_IS_LOGGED_IN, false)

    /**
     * Save login session with token and role from API response.
     */
    fun login(
        context: Context,
        token: String,
        role: String,
        email: String = ""
    ) {
        prefs(context).edit {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_AUTH_TOKEN, token)
            putString(KEY_USER_ROLE, role)
            putString(KEY_USER_EMAIL, email)
        }
    }

    /**
     * Save login session for guest mode (no token).
     */
    fun loginAsGuest(context: Context) {
        prefs(context).edit {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_NAME, "Guest User")
            putString(KEY_USER_EMAIL, "guest@mahfilhub.com")
            putString(KEY_USER_ROLE, "GUEST")
        }
    }

    fun logout(context: Context) {
        prefs(context).edit { clear() }
    }

    // ── Token ───────────────────────────────────────────────────────────

    fun getToken(context: Context): String? =
        prefs(context).getString(KEY_AUTH_TOKEN, null)

    fun getRole(context: Context): String =
        prefs(context).getString(KEY_USER_ROLE, "") ?: ""

    // ── User info ───────────────────────────────────────────────────────

    fun getUserName(context: Context): String =
        prefs(context).getString(KEY_USER_NAME, "Guest") ?: "Guest"

    fun getUserEmail(context: Context): String =
        prefs(context).getString(KEY_USER_EMAIL, "") ?: ""

}
