package com.syntrixor.syntrixoradmin.ui.screens.residents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syntrixor.syntrixoradmin.data.AppModule
import com.syntrixor.syntrixoradmin.data.model.Resident
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ResidentsState(
    val residents: List<Resident> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

class ResidentsViewModel : ViewModel() {
    private val repo = AppModule.repository
    private var allResidents: List<Resident> = emptyList()

    private val _state = MutableStateFlow(ResidentsState())
    val state: StateFlow<ResidentsState> = _state.asStateFlow()

    init { load() }

    fun setSearch(query: String) {
        _state.update { it.copy(searchQuery = query) }
        applyFilter()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repo.getResidents()
                .onSuccess { list ->
                    allResidents = list
                    _state.update { it.copy(isLoading = false) }
                    applyFilter()
                }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    private fun applyFilter() {
        val query = _state.value.searchQuery.trim().lowercase()
        val filtered = if (query.isEmpty()) allResidents
        else allResidents.filter {
            it.name.lowercase().contains(query) ||
            it.email.lowercase().contains(query) ||
            it.unit.lowercase().contains(query) ||
            it.building.lowercase().contains(query)
        }
        _state.update { it.copy(residents = filtered) }
    }
}
