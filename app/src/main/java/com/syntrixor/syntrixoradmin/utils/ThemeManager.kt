package com.syntrixor.syntrixoradmin.utils

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.mutableStateOf

object ThemeManager {
    private const val PREF_NAME = "syntrixor_prefs"
    private const val KEY_DARK_MODE = "dark_mode"

    val isDarkMode = mutableStateOf(false)

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        isDarkMode.value = if (prefs.contains(KEY_DARK_MODE)) {
            // User has explicitly set a preference before — honour it.
            prefs.getBoolean(KEY_DARK_MODE, false)
        } else {
            // First install: mirror the system Night Mode so the app doesn't look
            // jarring when the phone is already in dark mode.
            val nightMask = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            nightMask == Configuration.UI_MODE_NIGHT_YES
        }
    }

    fun toggle(context: Context) {
        isDarkMode.value = !isDarkMode.value
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK_MODE, isDarkMode.value)
            .apply()
    }
}
