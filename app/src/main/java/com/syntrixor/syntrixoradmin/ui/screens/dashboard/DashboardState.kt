package com.syntrixor.syntrixoradmin.ui.screens.dashboard

import com.syntrixor.syntrixoradmin.data.model.DashboardStats

data class DashboardState(
    val stats: DashboardStats? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
