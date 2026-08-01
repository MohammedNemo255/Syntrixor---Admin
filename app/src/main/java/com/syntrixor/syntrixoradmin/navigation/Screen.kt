package com.syntrixor.syntrixoradmin.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
    object RequestDetail : Screen("request_detail/{requestId}") {
        fun createRoute(requestId: String) = "request_detail/$requestId"
    }

    object CreateAnnouncement : Screen("create_announcement")
    object Admins : Screen("admins")
    object AdminDetail : Screen("admin_detail/{adminId}") {
        fun createRoute(adminId: String) = "admin_detail/$adminId"
        const val NEW = "new"
    }
}
