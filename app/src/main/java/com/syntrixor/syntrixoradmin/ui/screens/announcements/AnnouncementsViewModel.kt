package com.syntrixor.syntrixoradmin.ui.screens.announcements

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syntrixor.syntrixoradmin.data.AppModule
import com.syntrixor.syntrixoradmin.data.model.Announcement
import com.syntrixor.syntrixoradmin.utils.FeatureFlags
import com.syntrixor.syntrixoradmin.data.model.AnnouncementCategory
import com.syntrixor.syntrixoradmin.data.model.AnnouncementPriority
import com.syntrixor.syntrixoradmin.data.model.AnnouncementTarget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AnnouncementsState(
    val announcements: List<Announcement> = emptyList(),
    val isLoading: Boolean = true,
    val isPosting: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val postSuccess: Boolean = false,
    val newTitle: String = "",
    val newBody: String = "",
    val newCategory: AnnouncementCategory = AnnouncementCategory.GENERAL,
    val newPriority: AnnouncementPriority = AnnouncementPriority.MEDIUM,
    val attachedImageUri: Uri? = null,
    val target: AnnouncementTarget = AnnouncementTarget.ALL,
    val scheduledAt: String? = null,
    val editingId: String? = null,
    val navigateToCreate: Boolean = false,
    val categoryLocked: Boolean = false
)

sealed class AnnouncementsEvent {
    object ResetForm : AnnouncementsEvent()
    data class TitleChanged(val title: String) : AnnouncementsEvent()
    data class BodyChanged(val body: String) : AnnouncementsEvent()
    data class CategoryChanged(val category: AnnouncementCategory) : AnnouncementsEvent()
    data class PriorityChanged(val priority: AnnouncementPriority) : AnnouncementsEvent()
    object Post : AnnouncementsEvent()
    object ClearMessage : AnnouncementsEvent()
    object ClearPostSuccess : AnnouncementsEvent()
    data class ImageAttached(val uri: Uri) : AnnouncementsEvent()
    object RemoveImage : AnnouncementsEvent()
    data class TargetChanged(val target: AnnouncementTarget) : AnnouncementsEvent()
    data class ScheduledAtChanged(val scheduledAt: String?) : AnnouncementsEvent()
    data class StartEdit(val id: String) : AnnouncementsEvent()
    object ClearNavigateToCreate : AnnouncementsEvent()
    data class DeleteAnnouncement(val id: String) : AnnouncementsEvent()
    data class ToggleActive(val id: String) : AnnouncementsEvent()
}

class AnnouncementsViewModel : ViewModel() {

    private val repo = AppModule.repository
    private val isCategoryAdmin = AppModule.currentAdmin?.assignedCategories?.isNotEmpty() == true
    private val categoryLocked = isCategoryAdmin && !FeatureFlags.CATEGORY_ADMIN_SEE_ALL_ANNOUNCEMENTS

    private val _state = MutableStateFlow(AnnouncementsState())
    val state: StateFlow<AnnouncementsState> = _state.asStateFlow()

    init {
        _state.update { it.copy(categoryLocked = categoryLocked) }
        load()
    }

    fun onEvent(event: AnnouncementsEvent) {
        when (event) {
            AnnouncementsEvent.ResetForm -> _state.update {
                it.copy(
                    newTitle = "", newBody = "",
                    newCategory = if (categoryLocked) AnnouncementCategory.MAINTENANCE else AnnouncementCategory.GENERAL,
                    newPriority = AnnouncementPriority.MEDIUM,
                    attachedImageUri = null,
                    target = AnnouncementTarget.ALL,
                    scheduledAt = null,
                    editingId = null,
                    postSuccess = false
                )
            }

            is AnnouncementsEvent.TitleChanged -> _state.update { it.copy(newTitle = event.title) }
            is AnnouncementsEvent.BodyChanged -> _state.update { it.copy(newBody = event.body) }
            is AnnouncementsEvent.CategoryChanged -> _state.update { it.copy(newCategory = event.category) }
            is AnnouncementsEvent.PriorityChanged -> _state.update { it.copy(newPriority = event.priority) }
            AnnouncementsEvent.Post -> post()
            AnnouncementsEvent.ClearMessage -> _state.update {
                it.copy(
                    successMessage = null,
                    error = null
                )
            }

            AnnouncementsEvent.ClearPostSuccess -> _state.update { it.copy(postSuccess = false) }
            is AnnouncementsEvent.ImageAttached -> _state.update { it.copy(attachedImageUri = event.uri) }
            AnnouncementsEvent.RemoveImage -> _state.update { it.copy(attachedImageUri = null) }
            is AnnouncementsEvent.TargetChanged -> _state.update { it.copy(target = event.target) }
            is AnnouncementsEvent.ScheduledAtChanged -> _state.update { it.copy(scheduledAt = event.scheduledAt) }
            is AnnouncementsEvent.StartEdit -> startEdit(event.id)
            AnnouncementsEvent.ClearNavigateToCreate -> _state.update { it.copy(navigateToCreate = false) }
            is AnnouncementsEvent.DeleteAnnouncement -> deleteAnnouncement(event.id)
            is AnnouncementsEvent.ToggleActive -> toggleActive(event.id)
        }
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repo.getAnnouncements()
                .onSuccess { list ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            announcements = list
                        )
                    }
                }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    private fun startEdit(id: String) {
        val ann = _state.value.announcements.find { it.id == id } ?: return
        _state.update {
            it.copy(
                editingId = id,
                newTitle = ann.title,
                newBody = ann.body,
                newCategory = ann.category,
                newPriority = ann.priority,
                attachedImageUri = ann.imageUri?.let { uriStr -> Uri.parse(uriStr) },
                target = AnnouncementTarget.ALL,
                scheduledAt = null,
                navigateToCreate = true
            )
        }
    }

    private fun post() {
        val title = _state.value.newTitle.trim()
        val body = _state.value.newBody.trim()
        val category = _state.value.newCategory
        val priority = _state.value.newPriority
        val imageUri = _state.value.attachedImageUri?.toString()
        val scheduled = _state.value.scheduledAt
        val editId = _state.value.editingId
        if (title.isBlank() || body.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isPosting = true) }
            val result = if (editId != null) {
                repo.updateAnnouncement(
                    editId,
                    title,
                    body,
                    category,
                    priority,
                    imageUri,
                    scheduled
                )
            } else {
                repo.postAnnouncement(title, body, category, priority, imageUri, scheduled)
            }
            result
                .onSuccess { item ->
                    val updatedList = if (editId != null) {
                        _state.value.announcements.map { if (it.id == item.id) item else it }
                    } else {
                        listOf(item) + _state.value.announcements
                    }
                    _state.update {
                        it.copy(
                            isPosting = false,
                            editingId = null,
                            announcements = updatedList,
                            successMessage = "posted",
                            postSuccess = true
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(isPosting = false, error = e.message) }
                }
        }
    }

    private fun deleteAnnouncement(id: String) {
        viewModelScope.launch {
            repo.deleteAnnouncement(id)
                .onSuccess { _state.update { it.copy(announcements = it.announcements.filter { ann -> ann.id != id }) } }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }

    private fun toggleActive(id: String) {
        viewModelScope.launch {
            repo.toggleAnnouncementActive(id)
                .onSuccess { updated ->
                    _state.update { state ->
                        state.copy(announcements = state.announcements.map { if (it.id == updated.id) updated else it })
                    }
                }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }
}
