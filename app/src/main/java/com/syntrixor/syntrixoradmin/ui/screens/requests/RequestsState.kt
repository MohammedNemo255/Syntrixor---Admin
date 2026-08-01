package com.syntrixor.syntrixoradmin.ui.screens.requests

import com.syntrixor.syntrixoradmin.data.model.MaintenanceRequest
import com.syntrixor.syntrixoradmin.data.model.RequestStatus

data class RequestsState(
    val requests: List<MaintenanceRequest> = emptyList(),
    val activeFilter: RequestStatus? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)
