package com.syntrixor.syntrixoradmin.utils

import android.content.Context

object AuthPreferences {
    private const val PREF_NAME      = "syntrixor_auth"
    private const val KEY_REMEMBER   = "remember_me"
    private const val KEY_EMAIL      = "saved_email"
    private const val KEY_PASSWORD   = "saved_password"

    fun saveCredentials(context: Context, email: String, password: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_REMEMBER, true)
            .putString(KEY_EMAIL, email)
            .putString(KEY_PASSWORD, password)
            .apply()
    }

    fun clearCredentials(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_REMEMBER, false)
            .remove(KEY_EMAIL)
            .remove(KEY_PASSWORD)
            .apply()
    }

    fun isRemembered(context: Context): Boolean =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_REMEMBER, false)

    fun getSavedEmail(context: Context): String =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_EMAIL, "") ?: ""

    fun getSavedPassword(context: Context): String =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_PASSWORD, "") ?: ""
}
