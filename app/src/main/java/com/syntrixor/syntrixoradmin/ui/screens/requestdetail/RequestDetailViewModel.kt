package com.syntrixor.syntrixoradmin.ui.screens.requestdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syntrixor.syntrixoradmin.data.AppModule
import com.syntrixor.syntrixoradmin.data.model.RequestStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RequestDetailViewModel(private val requestId: String) : ViewModel() {

    private val repo = AppModule.repository

    private val _state = MutableStateFlow(RequestDetailState())
    val state: StateFlow<RequestDetailState> = _state.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val reqResult  = repo.getRequestById(requestId)
            val techResult = repo.getTechnicians()
            _state.update {
                it.copy(
                    isLoading    = false,
                    request      = reqResult.getOrNull(),
                    technicians  = techResult.getOrNull() ?: emptyList(),
                    error        = reqResult.exceptionOrNull()?.message
                )
            }
        }
    }

    fun onEvent(event: RequestDetailEvent) {
        when (event) {
            RequestDetailEvent.ShowAssignDialog  -> _state.update { it.copy(showAssignDialog = true) }
            RequestDetailEvent.HideAssignDialog  -> _state.update { it.copy(showAssignDialog = false) }
            RequestDetailEvent.ShowStatusDialog  -> _state.update { it.copy(showStatusDialog = true) }
            RequestDetailEvent.HideStatusDialog  -> _state.update { it.copy(showStatusDialog = false) }
            RequestDetailEvent.ClearMessage      -> _state.update { it.copy(successMessage = null, error = null) }
            is RequestDetailEvent.AssignTechnician -> assign(event.technicianId)
            is RequestDetailEvent.UpdateStatus     -> updateStatus(event.status)
        }
    }

    private fun assign(techId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isUpdating = true, showAssignDialog = false) }
            repo.assignTechnician(requestId, techId)
                .onSuccess { req -> _state.update { it.copy(isUpdating = false, request = req, successMessage = "Technician assigned successfully") } }
                .onFailure { e  -> _state.update { it.copy(isUpdating = false, error = e.message) } }
        }
    }

    private fun updateStatus(status: RequestStatus) {
        viewModelScope.launch {
            _state.update { it.copy(isUpdating = true, showStatusDialog = false) }
            repo.updateRequestStatus(requestId, status)
                .onSuccess { req -> _state.update { it.copy(isUpdating = false, request = req, successMessage = "Status updated successfully") } }
                .onFailure { e  -> _state.update { it.copy(isUpdating = false, error = e.message) } }
        }
    }
}
