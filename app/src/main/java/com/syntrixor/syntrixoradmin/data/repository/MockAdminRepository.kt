package com.syntrixor.syntrixoradmin.data.repository

import com.syntrixor.syntrixoradmin.data.AppModule
import com.syntrixor.syntrixoradmin.data.model.Admin
import com.syntrixor.syntrixoradmin.data.model.Announcement
import com.syntrixor.syntrixoradmin.data.model.AnnouncementCategory
import com.syntrixor.syntrixoradmin.data.model.AnnouncementPriority
import com.syntrixor.syntrixoradmin.data.model.Category
import com.syntrixor.syntrixoradmin.data.model.DashboardStats
import com.syntrixor.syntrixoradmin.data.model.MaintenanceRequest
import com.syntrixor.syntrixoradmin.data.model.Priority
import com.syntrixor.syntrixoradmin.data.model.RequestStatus
import com.syntrixor.syntrixoradmin.data.model.Resident
import com.syntrixor.syntrixoradmin.data.model.Technician
import com.syntrixor.syntrixoradmin.utils.FeatureFlags
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class MockAdminRepository : IAdminRepository {

    // ── Admin accounts ───────────────────────────────────────────────────────
    // password = "admin123" for all accounts
    private val adminAccounts: MutableMap<String, Admin> = mutableMapOf(
        "admin@syntrixor.com" to Admin(
            "admin1",
            "Syntrixor Admin",
            "admin@syntrixor.com",
            "+20 100 000 0000",
            "System Administrator",
            "Syntrixor Heights",
            emptyList()
        ),
        "plumbing@syntrixor.com" to Admin(
            "admin2",
            "Plumbing Admin",
            "plumbing@syntrixor.com",
            "+20 100 000 0001",
            "Category Administrator",
            "Syntrixor Heights",
            listOf(Category.PLUMBING, Category.CARPENTRY, Category.PAINTING)
        ),
        "electrical@syntrixor.com" to Admin(
            "admin3",
            "Electrical Admin",
            "electrical@syntrixor.com",
            "+20 100 000 0002",
            "Category Administrator",
            "Syntrixor Heights",
            listOf(Category.ELECTRICAL, Category.SECURITY)
        ),
        "hvac@syntrixor.com" to Admin(
            "admin4",
            "HVAC Admin",
            "hvac@syntrixor.com",
            "+20 100 000 0003",
            "Category Administrator",
            "Syntrixor Heights",
            listOf(Category.HVAC, Category.ELEVATOR)
        ),
        "cleaning@syntrixor.com" to Admin(
            "admin5",
            "Cleaning Admin",
            "cleaning@syntrixor.com",
            "+20 100 000 0004",
            "Category Administrator",
            "Syntrixor Heights",
            listOf(Category.CLEANING)
        ),
    )

    // ── Category filtering helpers ───────────────────────────────────────────
    private fun assignedCats(): List<Category> =
        AppModule.currentAdmin?.assignedCategories ?: emptyList()

    private fun isCategoryAdmin(): Boolean = assignedCats().isNotEmpty()

    private val technicians = listOf(
        Technician("t1", "Ahmed Hassan", "Plumbing", "+20 100 111 2233", true, 3, 47, 4.8f),
        Technician("t2", "Mohamed Samir", "Electrical", "+20 100 222 3344", true, 2, 61, 4.7f),
        Technician("t3", "Khaled Ibrahim", "HVAC", "+20 100 333 4455", true, 1, 33, 4.9f),
        Technician("t4", "Omar Farouk", "Carpentry", "+20 100 444 5566", false, 0, 28, 4.5f),
        Technician("t5", "Youssef Nasser", "General", "+20 100 555 6677", true, 4, 55, 4.6f),
    )

    private val residents = listOf(
        Resident(
            "r1",
            "Nadia Mostafa",
            "nadia@example.com",
            "+20 111 100 0011",
            "A",
            1,
            "A-101",
            5,
            "Jan 2024"
        ),
        Resident(
            "r2",
            "Sara Ahmed",
            "sara@example.com",
            "+20 111 200 0022",
            "A",
            2,
            "A-203",
            3,
            "Feb 2024"
        ),
        Resident(
            "r3",
            "Hana Khalil",
            "hana@example.com",
            "+20 111 300 0033",
            "B",
            1,
            "B-104",
            8,
            "Mar 2024"
        ),
        Resident(
            "r4",
            "Layla Ibrahim",
            "layla@example.com",
            "+20 111 400 0044",
            "B",
            3,
            "B-312",
            2,
            "Apr 2024"
        ),
        Resident(
            "r5",
            "Mariam Samir",
            "mariam@example.com",
            "+20 111 500 0055",
            "C",
            2,
            "C-205",
            4,
            "May 2024"
        ),
        Resident(
            "r6",
            "Dina Farouk",
            "dina@example.com",
            "+20 111 600 0066",
            "C",
            4,
            "C-401",
            6,
            "Jun 2024"
        ),
        Resident(
            "r7",
            "Rana Nasser",
            "rana@example.com",
            "+20 111 700 0077",
            "D",
            1,
            "D-102",
            1,
            "Jul 2024"
        ),
        Resident(
            "r8",
            "Reem Youssef",
            "reem@example.com",
            "+20 111 800 0088",
            "D",
            5,
            "D-503",
            7,
            "Aug 2024"
        ),
    )

    private var requests = mutableListOf(
        MaintenanceRequest(
            "req001",
            "r1",
            "Nadia Mostafa",
            "A-101",
            Category.PLUMBING,
            Priority.HIGH,
            "Leaking bathroom pipe",
            "Pipe under the sink has been leaking for two days.",
            RequestStatus.PENDING,
            null,
            null,
            "2026-06-28 09:15",
            "2026-06-28 09:15",
            null
        ),
        MaintenanceRequest(
            "req002",
            "r2",
            "Sara Ahmed",
            "A-203",
            Category.ELECTRICAL,
            Priority.URGENT,
            "Power outlet not working",
            "The outlet in the kitchen stopped working suddenly.",
            RequestStatus.IN_PROGRESS,
            "t2",
            "Mohamed Samir",
            "2026-06-27 14:30",
            "2026-06-28 10:00",
            "2026-06-29 10:00"
        ),
        MaintenanceRequest(
            "req003",
            "r3",
            "Hana Khalil",
            "B-104",
            Category.HVAC,
            Priority.MEDIUM,
            "AC not cooling",
            "Air conditioner runs but doesn't cool the room properly.",
            RequestStatus.IN_PROGRESS,
            "t3",
            "Khaled Ibrahim",
            "2026-06-26 11:00",
            "2026-06-27 09:00",
            "2026-06-28 12:00"
        ),
        MaintenanceRequest(
            "req004",
            "r4",
            "Layla Ibrahim",
            "B-312",
            Category.CARPENTRY,
            Priority.LOW,
            "Cabinet door hinge broken",
            "Kitchen cabinet door fell off. Hinge needs replacement.",
            RequestStatus.COMPLETED,
            "t4",
            "Omar Farouk",
            "2026-06-20 08:00",
            "2026-06-25 16:00",
            "2026-06-21 09:00"
        ),
        MaintenanceRequest(
            "req005",
            "r5",
            "Mariam Samir",
            "C-205",
            Category.PLUMBING,
            Priority.HIGH,
            "Water heater failure",
            "Hot water stopped working. Heater makes noise but no hot water.",
            RequestStatus.PENDING,
            null,
            null,
            "2026-07-01 07:45",
            "2026-07-01 07:45",
            null
        ),
        MaintenanceRequest(
            "req006",
            "r6",
            "Dina Farouk",
            "C-401",
            Category.PAINTING,
            Priority.LOW,
            "Wall paint peeling",
            "Paint is peeling in the living room near the window.",
            RequestStatus.PENDING,
            null,
            null,
            "2026-07-02 13:00",
            "2026-07-02 13:00",
            null
        ),
        MaintenanceRequest(
            "req007",
            "r7",
            "Rana Nasser",
            "D-102",
            Category.ELECTRICAL,
            Priority.MEDIUM,
            "Flickering hallway light",
            "The hallway light flickers on and off every few minutes.",
            RequestStatus.COMPLETED,
            "t2",
            "Mohamed Samir",
            "2026-06-15 10:30",
            "2026-06-18 14:00",
            "2026-06-16 11:00"
        ),
        MaintenanceRequest(
            "req008",
            "r8",
            "Reem Youssef",
            "D-503",
            Category.CLEANING,
            Priority.MEDIUM,
            "Common area deep clean",
            "Hallway carpet and elevator need professional cleaning.",
            RequestStatus.CANCELLED,
            null,
            null,
            "2026-06-10 09:00",
            "2026-06-12 11:00",
            null
        ),
        MaintenanceRequest(
            "req009",
            "r1",
            "Nadia Mostafa",
            "A-101",
            Category.SECURITY,
            Priority.HIGH,
            "Door lock malfunction",
            "Front door lock is stuck. Key gets jammed when turning.",
            RequestStatus.IN_PROGRESS,
            "t5",
            "Youssef Nasser",
            "2026-07-02 18:00",
            "2026-07-03 08:00",
            "2026-07-03 10:00"
        ),
        MaintenanceRequest(
            "req010",
            "r3",
            "Hana Khalil",
            "B-104",
            Category.ELEVATOR,
            Priority.URGENT,
            "Elevator making loud noise",
            "Elevator makes grinding noise between floors 3 and 4.",
            RequestStatus.PENDING,
            null,
            null,
            "2026-07-03 07:00",
            "2026-07-03 07:00",
            null
        ),
    )

    private val announcements = mutableListOf(
        Announcement(
            "a1",
            "Scheduled Water Maintenance",
            "Water will be shut off on July 5 from 8 AM to 12 PM for annual maintenance.",
            "2026-07-01",
            true,
            AnnouncementCategory.MAINTENANCE,
            AnnouncementPriority.HIGH
        ),
        Announcement(
            "a2",
            "Elevator Service Reminder",
            "Please report any elevator issues immediately to the management office.",
            "2026-06-28",
            true,
            AnnouncementCategory.GENERAL,
            AnnouncementPriority.MEDIUM
        ),
        Announcement(
            "a3",
            "Compound Cleaning Day",
            "A general cleaning crew will work throughout the compound on July 10.",
            "2026-06-25",
            true,
            AnnouncementCategory.EVENT,
            AnnouncementPriority.LOW
        ),
    )

    override suspend fun login(email: String, password: String): Result<Admin> {
        delay(1200.milliseconds)
        val admin = adminAccounts[email.lowercase()]
        return if (admin != null && password == "admin123") {
            AppModule.currentAdmin = admin
            Result.success(admin)
        } else {
            Result.failure(Exception("Invalid credentials"))
        }
    }

    override suspend fun getDashboardStats(): Result<DashboardStats> {
        delay(800.milliseconds)
        val cats = assignedCats()
        val visibleRequests = if (cats.isEmpty()) requests
        else requests.filter { it.category in cats }
        val visibleTechnicians =
            if (!isCategoryAdmin() || FeatureFlags.CATEGORY_ADMIN_SEE_ALL_TECHNICIANS) technicians
            else technicians.filter { tech -> cats.any { it.displayName == tech.specialization } }
        val stats = DashboardStats(
            totalRequests = visibleRequests.size,
            pendingRequests = visibleRequests.count { it.status == RequestStatus.PENDING },
            inProgressRequests = visibleRequests.count { it.status == RequestStatus.IN_PROGRESS },
            completedRequests = visibleRequests.count { it.status == RequestStatus.COMPLETED },
            activeTechnicians = visibleTechnicians.count { it.isActive },
            totalResidents = residents.size,
            recentRequests = visibleRequests.sortedByDescending { it.submittedAt }.take(5)
        )
        return Result.success(stats)
    }

    override suspend fun getRequests(status: RequestStatus?): Result<List<MaintenanceRequest>> {
        delay(600.milliseconds)
        val cats = assignedCats()
        val filtered = requests
            .filter { status == null || it.status == status }
            .filter { cats.isEmpty() || it.category in cats }
        return Result.success(filtered.sortedByDescending { it.submittedAt })
    }

    override suspend fun getRequestById(id: String): Result<MaintenanceRequest> {
        delay(400.milliseconds)
        val req = requests.find { it.id == id }
        return if (req != null) Result.success(req)
        else Result.failure(Exception("Request not found"))
    }

    override suspend fun assignTechnician(
        requestId: String,
        technicianId: String
    ): Result<MaintenanceRequest> {
        delay(700.milliseconds)
        val index = requests.indexOfFirst { it.id == requestId }
        if (index == -1) return Result.failure(Exception("Request not found"))
        val tech = technicians.find { it.id == technicianId }
            ?: return Result.failure(Exception("Technician not found"))
        val updated = requests[index].copy(
            assignedTechnicianId = tech.id,
            assignedTechnicianName = tech.name,
            status = RequestStatus.IN_PROGRESS,
            updatedAt = "2026-07-03 09:00"
        )
        requests[index] = updated
        return Result.success(updated)
    }

    override suspend fun updateRequestStatus(
        requestId: String,
        status: RequestStatus
    ): Result<MaintenanceRequest> {
        delay(600.milliseconds)
        val index = requests.indexOfFirst { it.id == requestId }
        if (index == -1) return Result.failure(Exception("Request not found"))
        val updated = requests[index].copy(status = status, updatedAt = "2026-07-03 09:00")
        requests[index] = updated
        return Result.success(updated)
    }

    override suspend fun getTechnicians(): Result<List<Technician>> {
        delay(500.milliseconds)
        val cats = assignedCats()
        val visible =
            if (!isCategoryAdmin() || FeatureFlags.CATEGORY_ADMIN_SEE_ALL_TECHNICIANS) technicians
            else technicians.filter { tech -> cats.any { it.displayName == tech.specialization } }
        return Result.success(visible)
    }

    override suspend fun getResidents(): Result<List<Resident>> {
        delay(500.milliseconds)
        return Result.success(residents)
    }

    override suspend fun getAnnouncements(): Result<List<Announcement>> {
        delay(400.milliseconds)
        val visible =
            if (!isCategoryAdmin() || FeatureFlags.CATEGORY_ADMIN_SEE_ALL_ANNOUNCEMENTS) announcements.toList()
            else announcements.filter { it.category == AnnouncementCategory.MAINTENANCE }
        return Result.success(visible)
    }

    override suspend fun postAnnouncement(
        title: String,
        body: String,
        category: AnnouncementCategory,
        priority: AnnouncementPriority,
        imageUri: String?,
        scheduledAt: String?
    ): Result<Announcement> {
        delay(700.milliseconds)
        val new = Announcement(
            "a${announcements.size + 1}",
            title,
            body,
            "",
            true,
            category,
            priority,
            imageUri,
            scheduledAt
        )
        announcements.add(new)
        return Result.success(new)
    }

    override suspend fun updateAnnouncement(
        id: String,
        title: String,
        body: String,
        category: AnnouncementCategory,
        priority: AnnouncementPriority,
        imageUri: String?,
        scheduledAt: String?
    ): Result<Announcement> {
        delay(600.milliseconds)
        val index = announcements.indexOfFirst { it.id == id }
        if (index == -1) return Result.failure(Exception("Announcement not found"))
        val updated = announcements[index].copy(
            title = title,
            body = body,
            category = category,
            priority = priority,
            imageUri = imageUri,
            scheduledAt = scheduledAt
        )
        announcements[index] = updated
        return Result.success(updated)
    }

    override suspend fun deleteAnnouncement(id: String): Result<Unit> {
        delay(400.milliseconds)
        return if (announcements.removeIf { it.id == id }) Result.success(Unit)
        else Result.failure(Exception("Announcement not found"))
    }

    override suspend fun toggleAnnouncementActive(id: String): Result<Announcement> {
        delay(400.milliseconds)
        val index = announcements.indexOfFirst { it.id == id }
        if (index == -1) return Result.failure(Exception("Announcement not found"))
        val updated = announcements[index].copy(isActive = !announcements[index].isActive)
        announcements[index] = updated
        return Result.success(updated)
    }

    // ── Admin management ─────────────────────────────────────────────────────

    override suspend fun getAdmins(): Result<List<Admin>> {
        delay(400.milliseconds)
        return Result.success(adminAccounts.values.toList())
    }

    override suspend fun updateAdmin(admin: Admin): Result<Admin> {
        delay(500.milliseconds)
        val entry = adminAccounts.entries.find { it.value.id == admin.id }
            ?: return Result.failure(Exception("Admin not found"))
        val updated = admin.copy(
            role = if (admin.assignedCategories.isEmpty()) "System Administrator" else "Category Administrator"
        )
        adminAccounts[entry.key] = updated
        if (AppModule.currentAdmin?.id == admin.id) AppModule.currentAdmin = updated
        return Result.success(updated)
    }

    override suspend fun createAdmin(
        name: String,
        email: String,
        phone: String,
        categories: List<Category>
    ): Result<Admin> {
        delay(600.milliseconds)
        val key = email.lowercase()
        if (adminAccounts.containsKey(key)) return Result.failure(Exception("Email already in use"))
        val newId = "admin${adminAccounts.size + 1}"
        val role = if (categories.isEmpty()) "System Administrator" else "Category Administrator"
        val newAdmin = Admin(newId, name, key, phone, role, "Syntrixor Heights", categories, true)
        adminAccounts[key] = newAdmin
        return Result.success(newAdmin)
    }
}
