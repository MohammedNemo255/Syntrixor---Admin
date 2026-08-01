package com.syntrixor.syntrixoradmin.data.model

import androidx.annotation.StringRes
import com.syntrixor.syntrixoradmin.R

enum class RequestStatus(val displayName: String, @StringRes val labelRes: Int) {
    PENDING("Pending",       R.string.filter_pending),
    IN_PROGRESS("In Progress", R.string.filter_in_progress),
    COMPLETED("Completed",   R.string.filter_completed),
    CANCELLED("Cancelled",   R.string.filter_cancelled)
}
