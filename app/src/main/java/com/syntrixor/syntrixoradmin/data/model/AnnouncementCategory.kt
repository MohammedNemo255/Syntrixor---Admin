package com.syntrixor.syntrixoradmin.data.model

import androidx.annotation.StringRes
import com.syntrixor.syntrixoradmin.R

enum class AnnouncementCategory(@StringRes val labelRes: Int) {
    GENERAL(R.string.ann_cat_general),
    MAINTENANCE(R.string.ann_cat_maintenance),
    EMERGENCY(R.string.ann_cat_emergency),
    EVENT(R.string.ann_cat_event)
}
