package com.syntrixor.syntrixoradmin.ui.screens.technicians

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syntrixor.syntrixoradmin.data.AppModule
import com.syntrixor.syntrixoradmin.data.model.Technician
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TechniciansState(
    val technicians: List<Technician> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

class TechniciansViewModel : ViewModel() {
    private val repo = AppModule.repository
    private var allTechnicians: List<Technician> = emptyList()

    private val _state = MutableStateFlow(TechniciansState())
    val state: StateFlow<TechniciansState> = _state.asStateFlow()

    init { load() }

    fun setSearch(query: String) {
        _state.update { it.copy(searchQuery = query) }
        applyFilter()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repo.getTechnicians()
                .onSuccess { list ->
                    allTechnicians = list
                    _state.update { it.copy(isLoading = false) }
                    applyFilter()
                }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    private fun applyFilter() {
        val query = _state.value.searchQuery.trim().lowercase()
        val filtered = if (query.isEmpty()) allTechnicians
        else allTechnicians.filter {
            it.name.lowercase().contains(query) ||
            it.specialization.lowercase().contains(query)
        }
        _state.update { it.copy(technicians = filtered) }
    }
}
