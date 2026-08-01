package com.syntrixor.syntrixoradmin.ui.screens.requestdetail

import com.syntrixor.syntrixoradmin.data.model.MaintenanceRequest
import com.syntrixor.syntrixoradmin.data.model.Technician

data class RequestDetailState(
    val request: MaintenanceRequest? = null,
    val technicians: List<Technician> = emptyList(),
    val isLoading: Boolean = true,
    val isUpdating: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val showAssignDialog: Boolean = false,
    val showStatusDialog: Boolean = false
)
