package com.syntrixor.syntrixoradmin.data.model

data class DashboardStats(
    val totalRequests: Int,
    val pendingRequests: Int,
    val inProgressRequests: Int,
    val completedRequests: Int,
    val activeTechnicians: Int,
    val totalResidents: Int,
    val recentRequests: List<MaintenanceRequest>
)
