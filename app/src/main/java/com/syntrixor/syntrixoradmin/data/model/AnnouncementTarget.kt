package com.syntrixor.syntrixoradmin.data.model

import androidx.annotation.StringRes
import com.syntrixor.syntrixoradmin.R

enum class AnnouncementTarget(@StringRes val labelRes: Int) {
    ALL(R.string.all_residents),
    BUILDING_A(R.string.target_building_a),
    BUILDING_B(R.string.target_building_b),
    BUILDING_C(R.string.target_building_c),
    BUILDING_D(R.string.target_building_d)
}
