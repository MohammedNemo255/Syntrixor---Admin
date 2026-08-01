package com.syntrixor.syntrixoradmin.data.model

data class Announcement(
    val id: String,
    val title: String,
    val body: String,
    val postedAt: String,
    val isActive: Boolean,
    val category: AnnouncementCategory = AnnouncementCategory.GENERAL,
    val priority: AnnouncementPriority = AnnouncementPriority.LOW,
    val imageUri: String? = null,
    val scheduledAt: String? = null
)
