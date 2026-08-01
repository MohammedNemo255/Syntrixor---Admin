package com.syntrixor.syntrixoradmin.ui.screens.requestdetail

import com.syntrixor.syntrixoradmin.data.model.RequestStatus

sealed class RequestDetailEvent {
    object ShowAssignDialog  : RequestDetailEvent()
    object HideAssignDialog  : RequestDetailEvent()
    object ShowStatusDialog  : RequestDetailEvent()
    object HideStatusDialog  : RequestDetailEvent()
    data class AssignTechnician(val technicianId: String) : RequestDetailEvent()
    data class UpdateStatus(val status: RequestStatus)    : RequestDetailEvent()
    object ClearMessage : RequestDetailEvent()
}
