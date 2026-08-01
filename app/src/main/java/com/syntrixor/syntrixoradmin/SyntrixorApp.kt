package com.syntrixor.syntrixoradmin

import android.app.Application
import android.content.Context
import com.syntrixor.syntrixoradmin.utils.LocaleHelper

class SyntrixorApp : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.applyLocale(base))
    }
}
