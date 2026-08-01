package com.syntrixor.syntrixoradmin.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {
    private const val PREF_NAME  = "syntrixor_prefs"
    private const val KEY_LANG   = "language"
    private const val DEFAULT    = "en"

    fun applyLocale(context: Context): Context {
        val lang   = getLanguage(context)
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    fun setLanguage(context: Context, lang: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_LANG, lang).apply()
    }

    fun getLanguage(context: Context): String =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANG, DEFAULT) ?: DEFAULT
}
