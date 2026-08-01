package com.syntrixor.syntrixoradmin.data.model

data class Technician(
    val id: String,
    val name: String,
    val specialization: String,
    val phone: String,
    val isActive: Boolean,
    val openRequestsCount: Int,
    val completedCount: Int,
    val rating: Float
)
