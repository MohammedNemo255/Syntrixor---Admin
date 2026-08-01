package com.syntrixor.syntrixoradmin.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration

/** Below this width, the app behaves like a phone (bottom nav, single-pane screens). */
const val TABLET_BREAKPOINT_DP = 600

val LocalIsTablet = compositionLocalOf { false }

@Composable
fun rememberIsTablet(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp >= TABLET_BREAKPOINT_DP
}
