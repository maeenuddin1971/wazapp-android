package com.maeen.mahfilhub.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Simple session manager backed by SharedPreferences.
 * Stores login state and basic user info.
 * Replace static values with real API data later.
 */
object SessionManager {

    private const val PREF_NAME = "mahfilhub_session"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // ── Login state ─────────────────────────────────────────────────────

    fun isLoggedIn(context: Context): Boolean =
        prefs(context).getBoolean(KEY_IS_LOGGED_IN, false)

    /**
     * Save login session. Uses static placeholder values for now.
     * Replace with real API response data later.
     */
    fun login(context: Context, name: String = "Guest User", email: String = "user@mahfilhub.com") {
        prefs(context).edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }

    fun logout(context: Context) {
        prefs(context).edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .remove(KEY_USER_NAME)
            .remove(KEY_USER_EMAIL)
            .apply()
    }

    // ── User info ───────────────────────────────────────────────────────

    fun getUserName(context: Context): String =
        prefs(context).getString(KEY_USER_NAME, "Guest") ?: "Guest"

    fun getUserEmail(context: Context): String =
        prefs(context).getString(KEY_USER_EMAIL, "") ?: ""

}
