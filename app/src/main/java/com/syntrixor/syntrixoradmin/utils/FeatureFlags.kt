package com.syntrixor.syntrixoradmin.utils

object FeatureFlags {
    const val USE_REAL_API = false
    const val ENABLE_REPORTS = false
    const val ENABLE_PUSH_NOTIFICATIONS = false

    // Category admin restrictions — default false = restricted
    // true  → category admin sees ALL technicians
    // false → category admin sees only technicians whose specialization matches their categories
    const val CATEGORY_ADMIN_SEE_ALL_TECHNICIANS = false

    // true  → category admin sees ALL announcements and can post to any category
    // false → category admin sees only MAINTENANCE announcements and is locked to that category
    const val CATEGORY_ADMIN_SEE_ALL_ANNOUNCEMENTS = false

    // true  → "Manage Admins" section appears in Profile for super admins
    // false → feature hidden entirely
    const val ADMIN_MANAGEMENT = true
}
