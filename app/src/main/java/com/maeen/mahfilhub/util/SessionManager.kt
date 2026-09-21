package com.maeen.mahfilhub.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit

/**
 * Simple session manager backed by SharedPreferences.
 * Stores login state, token, role and basic user info.
 */
object SessionManager {

    private const val TAG = "SessionManager"
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
     * Uses commit = true for synchronous write to ensure token
     * is available immediately after this call returns.
     */
    fun login(
        context: Context,
        token: String,
        role: String,
        email: String = ""
    ) {
        Log.d(TAG, "login: saving token (${token.length} chars), role=$role, email=$email")
        prefs(context).edit(commit = true) {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_AUTH_TOKEN, token)
            putString(KEY_USER_ROLE, role)
            putString(KEY_USER_EMAIL, email)
        }
        // Verify the save
        val savedToken = getToken(context)
        Log.d(TAG, "login: verified saved token=${if (savedToken == token) "MATCHES" else "MISMATCH! saved=${savedToken?.take(20)}"}")
    }

    /**
     * Save login session for guest mode (no token).
     */
    fun loginAsGuest(context: Context) {
        Log.d(TAG, "loginAsGuest: entering guest mode")
        prefs(context).edit(commit = true) {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_NAME, "Guest User")
            putString(KEY_USER_EMAIL, "guest@mahfilhub.com")
            putString(KEY_USER_ROLE, "GUEST")
            // Clear any stale token
            remove(KEY_AUTH_TOKEN)
        }
    }

    fun logout(context: Context) {
        Log.d(TAG, "logout: clearing session")
        prefs(context).edit(commit = true) { clear() }
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
