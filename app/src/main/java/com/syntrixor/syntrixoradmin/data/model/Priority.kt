package com.syntrixor.syntrixoradmin.data.model

import androidx.annotation.StringRes
import com.syntrixor.syntrixoradmin.R

enum class Priority(val displayName: String, @StringRes val labelRes: Int) {
    LOW("Low",       R.string.priority_low),
    MEDIUM("Medium", R.string.priority_medium),
    HIGH("High",     R.string.priority_high),
    URGENT("Urgent", R.string.priority_urgent)
}
