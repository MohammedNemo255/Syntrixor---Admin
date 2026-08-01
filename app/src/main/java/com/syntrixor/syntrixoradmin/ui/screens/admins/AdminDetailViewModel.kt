package com.syntrixor.syntrixoradmin.ui.screens.admins

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.syntrixor.syntrixoradmin.R
import com.syntrixor.syntrixoradmin.data.AppModule
import com.syntrixor.syntrixoradmin.data.model.Admin
import com.syntrixor.syntrixoradmin.data.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminDetailState(
    val isCreateMode: Boolean = true,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val selectedCategories: List<Category> = emptyList(),
    val isActive: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false,
    val nameError: String? = null,
    val emailError: String? = null,
)

sealed class AdminDetailEvent {
    data class NameChanged(val name: String)   : AdminDetailEvent()
    data class EmailChanged(val email: String) : AdminDetailEvent()
    data class PhoneChanged(val phone: String) : AdminDetailEvent()
    data class ToggleCategory(val category: Category) : AdminDetailEvent()
    object ToggleActive     : AdminDetailEvent()
    object Save             : AdminDetailEvent()
    object ClearSaveSuccess : AdminDetailEvent()
    object ClearError       : AdminDetailEvent()
}

class AdminDetailViewModel(
    app: Application,
    private val adminId: String?
) : AndroidViewModel(app) {

    private val context = app.applicationContext
    private val isCreateMode = adminId == null || adminId == "new"
    private val repo = AppModule.repository

    private val _state = MutableStateFlow(AdminDetailState(isCreateMode = isCreateMode))
    val state: StateFlow<AdminDetailState> = _state.asStateFlow()

    init {
        if (!isCreateMode) loadAdmin()
    }

    fun onEvent(event: AdminDetailEvent) {
        when (event) {
            is AdminDetailEvent.NameChanged     -> _state.update { it.copy(name = event.name, nameError = null) }
            is AdminDetailEvent.EmailChanged    -> _state.update { it.copy(email = event.email, emailError = null) }
            is AdminDetailEvent.PhoneChanged    -> _state.update { it.copy(phone = event.phone) }
            is AdminDetailEvent.ToggleCategory  -> {
                val current = _state.value.selectedCategories
                val updated = if (event.category in current) current - event.category else current + event.category
                _state.update { it.copy(selectedCategories = updated) }
            }
            AdminDetailEvent.ToggleActive       -> _state.update { it.copy(isActive = !it.isActive) }
            AdminDetailEvent.Save               -> save()
            AdminDetailEvent.ClearSaveSuccess   -> _state.update { it.copy(saveSuccess = false) }
            AdminDetailEvent.ClearError         -> _state.update { it.copy(error = null) }
        }
    }

    private fun loadAdmin() {
        viewModelScope.launch {
            repo.getAdmins()
                .onSuccess { admins ->
                    val admin = admins.find { it.id == adminId }
                    if (admin != null) {
                        _state.update {
                            it.copy(
                                name               = admin.name,
                                email              = admin.email,
                                phone              = admin.phone,
                                selectedCategories = admin.assignedCategories,
                                isActive           = admin.isActive
                            )
                        }
                    } else {
                        _state.update { it.copy(error = context.getString(R.string.request_not_found)) }
                    }
                }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }

    private fun save() {
        val name  = _state.value.name.trim()
        val email = _state.value.email.trim()

        val nameError  = if (name.isBlank()) context.getString(R.string.error_name_empty) else null
        val emailError = when {
            email.isBlank()       -> context.getString(R.string.error_email_empty)
            !email.contains("@") -> context.getString(R.string.error_email_invalid)
            else                  -> null
        }
        if (nameError != null || emailError != null) {
            _state.update { it.copy(nameError = nameError, emailError = emailError) }
            return
        }

        val phone      = _state.value.phone.trim()
        val categories = _state.value.selectedCategories
        val isActive   = _state.value.isActive

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            val result = if (isCreateMode) {
                repo.createAdmin(name, email, phone, categories)
            } else {
                val role = if (categories.isEmpty()) "System Administrator" else "Category Administrator"
                repo.updateAdmin(Admin(adminId!!, name, email, phone, role, "Syntrixor Heights", categories, isActive))
            }
            result
                .onSuccess { _state.update { it.copy(isSaving = false, saveSuccess = true) } }
                .onFailure { e -> _state.update { it.copy(isSaving = false, error = e.message) } }
        }
    }

    companion object {
        fun factory(adminId: String?): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!
                return AdminDetailViewModel(app, adminId) as T
            }
        }
    }
}
