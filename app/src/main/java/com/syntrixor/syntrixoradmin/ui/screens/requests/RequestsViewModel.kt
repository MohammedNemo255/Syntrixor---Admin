package com.syntrixor.syntrixoradmin.ui.screens.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syntrixor.syntrixoradmin.data.AppModule
import com.syntrixor.syntrixoradmin.data.model.MaintenanceRequest
import com.syntrixor.syntrixoradmin.data.model.RequestStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RequestsViewModel : ViewModel() {

    private val repo = AppModule.repository
    private var allRequests: List<MaintenanceRequest> = emptyList()

    private val _state = MutableStateFlow(RequestsState())
    val state: StateFlow<RequestsState> = _state.asStateFlow()

    init { load() }

    fun setFilter(status: RequestStatus?) {
        _state.update { it.copy(activeFilter = status) }
        applyFilters()
    }

    fun setSearch(query: String) {
        _state.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repo.getRequests(null)
                .onSuccess { list ->
                    allRequests = list
                    _state.update { it.copy(isLoading = false) }
                    applyFilters()
                }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    private fun applyFilters() {
        val query  = _state.value.searchQuery.trim().lowercase()
        val status = _state.value.activeFilter
        val filtered = allRequests
            .filter { status == null || it.status == status }
            .filter {
                query.isEmpty() ||
                it.title.lowercase().contains(query) ||
                it.residentName.lowercase().contains(query) ||
                it.unit.lowercase().contains(query)
            }
        _state.update { it.copy(requests = filtered) }
    }
}
