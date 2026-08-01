package com.syntrixor.syntrixoradmin.utils

import android.content.Context
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

/**
 * Hardware tablet check, independent of current rotation — uses smallestScreenWidthDp
 * (the shorter of the two screen dimensions) rather than the current-orientation width.
 * Use this to decide the orientation lock; use [rememberIsTablet] for layout decisions
 * that should react to the actual current window width.
 */
fun isTabletDevice(context: Context): Boolean =
    context.resources.configuration.smallestScreenWidthDp >= TABLET_BREAKPOINT_DP
