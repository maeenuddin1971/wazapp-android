package com.maeen.mahfilhub.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * Utility for managing the app's locale (English / Bangla).
 * Persists the user's language preference in SharedPreferences.
 */
object LocaleHelper {

    private const val PREFS_NAME = "mahfilhub_settings"
    private const val KEY_LANGUAGE = "selected_language"

    const val LANG_ENGLISH = "en"
    const val LANG_BANGLA = "bn"

    /**
     * Wraps the given context with the stored locale configuration.
     * Call this from [Activity.attachBaseContext].
     */
    fun applyLocale(context: Context): Context {
        val language = getLanguage(context)
        return setLocale(context, language)
    }

    /**
     * Creates a new context with the given locale applied.
     */
    fun setLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }

    /**
     * Saves the selected language code to SharedPreferences.
     */
    fun saveLanguage(context: Context, languageCode: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, languageCode)
            .apply()
    }

    /**
     * Retrieves the stored language code. Defaults to English.
     */
    fun getLanguage(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, LANG_ENGLISH) ?: LANG_ENGLISH
    }

    /**
     * Toggles between English and Bangla, saves the choice, and returns the new language code.
     */
    fun toggleLanguage(context: Context): String {
        val current = getLanguage(context)
        val newLang = if (current == LANG_ENGLISH) LANG_BANGLA else LANG_ENGLISH
        saveLanguage(context, newLang)
        return newLang
    }
}
