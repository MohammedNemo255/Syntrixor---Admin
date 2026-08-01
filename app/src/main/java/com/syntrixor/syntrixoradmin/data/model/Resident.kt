package com.syntrixor.syntrixoradmin.data.model

data class Resident(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val building: String,
    val floor: Int,
    val unit: String,
    val totalRequests: Int,
    val joinedDate: String
)
