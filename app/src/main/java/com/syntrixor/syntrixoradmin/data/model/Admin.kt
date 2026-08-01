package com.syntrixor.syntrixoradmin.data.model

data class Admin(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String = "System Administrator",
    val compoundName: String,
    val assignedCategories: List<Category> = emptyList(), // empty = super admin (sees everything)
    val isActive: Boolean = true
)
