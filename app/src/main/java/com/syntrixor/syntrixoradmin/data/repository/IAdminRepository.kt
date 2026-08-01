package com.syntrixor.syntrixoradmin.data.repository

import com.syntrixor.syntrixoradmin.data.model.Admin
import com.syntrixor.syntrixoradmin.data.model.Announcement
import com.syntrixor.syntrixoradmin.data.model.AnnouncementCategory
import com.syntrixor.syntrixoradmin.data.model.AnnouncementPriority
import com.syntrixor.syntrixoradmin.data.model.DashboardStats
import com.syntrixor.syntrixoradmin.data.model.MaintenanceRequest
import com.syntrixor.syntrixoradmin.data.model.RequestStatus
import com.syntrixor.syntrixoradmin.data.model.Category
import com.syntrixor.syntrixoradmin.data.model.Resident
import com.syntrixor.syntrixoradmin.data.model.Technician

interface IAdminRepository {
    suspend fun login(email: String, password: String): Result<Admin>
    suspend fun getDashboardStats(): Result<DashboardStats>
    suspend fun getRequests(status: RequestStatus? = null): Result<List<MaintenanceRequest>>
    suspend fun getRequestById(id: String): Result<MaintenanceRequest>
    suspend fun assignTechnician(requestId: String, technicianId: String): Result<MaintenanceRequest>
    suspend fun updateRequestStatus(requestId: String, status: RequestStatus): Result<MaintenanceRequest>
    suspend fun getTechnicians(): Result<List<Technician>>
    suspend fun getResidents(): Result<List<Resident>>
    suspend fun getAnnouncements(): Result<List<Announcement>>
    suspend fun postAnnouncement(title: String, body: String, category: AnnouncementCategory, priority: AnnouncementPriority, imageUri: String? = null, scheduledAt: String? = null): Result<Announcement>
    suspend fun updateAnnouncement(id: String, title: String, body: String, category: AnnouncementCategory, priority: AnnouncementPriority, imageUri: String?, scheduledAt: String?): Result<Announcement>
    suspend fun deleteAnnouncement(id: String): Result<Unit>
    suspend fun toggleAnnouncementActive(id: String): Result<Announcement>

    // Admin management (super admin only)
    suspend fun getAdmins(): Result<List<Admin>>
    suspend fun updateAdmin(admin: Admin): Result<Admin>
    suspend fun createAdmin(name: String, email: String, phone: String, categories: List<Category>): Result<Admin>
}
