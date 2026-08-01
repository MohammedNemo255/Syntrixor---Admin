package com.syntrixor.syntrixoradmin.ui.screens.admins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syntrixor.syntrixoradmin.data.AppModule
import com.syntrixor.syntrixoradmin.data.model.Admin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminsState(
    val admins: List<Admin> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val searchQuery: String = ""
)

sealed class AdminsEvent {
    object Load : AdminsEvent()
    data class SearchChanged(val query: String) : AdminsEvent()
    object ClearError : AdminsEvent()
}

class AdminsViewModel : ViewModel() {

    private val repo = AppModule.repository
    private val _state = MutableStateFlow(AdminsState())
    val state: StateFlow<AdminsState> = _state.asStateFlow()

    init { load() }

    fun onEvent(event: AdminsEvent) {
        when (event) {
            AdminsEvent.Load                    -> load()
            is AdminsEvent.SearchChanged        -> _state.update { it.copy(searchQuery = event.query) }
            AdminsEvent.ClearError              -> _state.update { it.copy(error = null) }
        }
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repo.getAdmins()
                .onSuccess { admins -> _state.update { it.copy(isLoading = false, admins = admins) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}
