package com.syntrixor.syntrixoradmin.data.model

data class MaintenanceRequest(
    val id: String,
    val residentId: String,
    val residentName: String,
    val unit: String,
    val category: Category,
    val priority: Priority,
    val title: String,
    val description: String,
    val status: RequestStatus,
    val assignedTechnicianId: String?,
    val assignedTechnicianName: String?,
    val submittedAt: String,
    val updatedAt: String,
    val scheduledAt: String?
)
