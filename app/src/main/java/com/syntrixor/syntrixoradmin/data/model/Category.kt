package com.syntrixor.syntrixoradmin.data.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.syntrixor.syntrixoradmin.R

enum class Category(val displayName: String, @StringRes val labelRes: Int, val color: Color) {
    PLUMBING   ("Plumbing",   R.string.cat_plumbing,    Color(0xFF0EA5E9)), // sky blue
    ELECTRICAL ("Electrical", R.string.cat_electrical,  Color(0xFFF59E0B)), // amber
    HVAC       ("HVAC",       R.string.cat_hvac,        Color(0xFF06B6D4)), // cyan
    CARPENTRY  ("Carpentry",  R.string.cat_carpentry,   Color(0xFFD97706)), // brown-orange
    PAINTING   ("Painting",   R.string.cat_painting,    Color(0xFF8B5CF6)), // violet
    CLEANING   ("Cleaning",   R.string.cat_cleaning,    Color(0xFF10B981)), // emerald
    ELEVATOR   ("Elevator",   R.string.cat_elevator,    Color(0xFF6366F1)), // indigo
    SECURITY   ("Security",   R.string.cat_security,    Color(0xFFEF4444)), // red
    OTHER      ("Other",      R.string.cat_other,       Color(0xFF94A3B8))  // slate
}
