package com.syntrixor.syntrixoradmin.utils

import android.content.Context
import androidx.compose.runtime.mutableStateOf

object ThemeManager {
    private const val PREF_NAME = "syntrixor_prefs"
    private const val KEY_DARK_MODE = "dark_mode"

    val isDarkMode = mutableStateOf(false)

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        isDarkMode.value = prefs.getBoolean(KEY_DARK_MODE, false)
    }

    fun toggle(context: Context) {
        isDarkMode.value = !isDarkMode.value
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK_MODE, isDarkMode.value)
            .apply()
    }
}
