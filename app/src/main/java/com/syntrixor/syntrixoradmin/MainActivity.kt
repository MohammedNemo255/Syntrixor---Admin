package com.syntrixor.syntrixoradmin

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.rememberNavController
import com.syntrixor.syntrixoradmin.navigation.NavGraph
import com.syntrixor.syntrixoradmin.ui.theme.SyntrixorAdminTheme
import com.syntrixor.syntrixoradmin.utils.LocaleHelper
import com.syntrixor.syntrixoradmin.utils.LocalIsTablet
import com.syntrixor.syntrixoradmin.utils.ThemeManager
import com.syntrixor.syntrixoradmin.utils.rememberIsTablet

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeManager.init(this)
        enableEdgeToEdge()
        setContent {
            SyntrixorAdminTheme {
                val navController = rememberNavController()
                val isTablet = rememberIsTablet()
                CompositionLocalProvider(LocalIsTablet provides isTablet) {
                    NavGraph(navController = navController)
                }
            }
        }
    }
}
