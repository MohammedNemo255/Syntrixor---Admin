package com.syntrixor.syntrixoradmin.data.model

import androidx.annotation.StringRes
import com.syntrixor.syntrixoradmin.R

enum class AnnouncementPriority(@StringRes val labelRes: Int) {
    LOW(R.string.priority_low),
    MEDIUM(R.string.priority_medium),
    HIGH(R.string.priority_high)
}
