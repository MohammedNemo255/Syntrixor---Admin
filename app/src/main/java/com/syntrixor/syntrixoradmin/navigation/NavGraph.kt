package com.syntrixor.syntrixoradmin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.syntrixor.syntrixoradmin.ui.screens.admins.AdminDetailScreen
import com.syntrixor.syntrixoradmin.ui.screens.admins.AdminsScreen
import com.syntrixor.syntrixoradmin.ui.screens.admins.AdminsViewModel
import com.syntrixor.syntrixoradmin.ui.screens.announcements.AnnouncementsViewModel
import com.syntrixor.syntrixoradmin.ui.screens.announcements.CreateAnnouncementScreen
import com.syntrixor.syntrixoradmin.ui.screens.home.HomeScreen
import com.syntrixor.syntrixoradmin.ui.screens.login.LoginScreen
import com.syntrixor.syntrixoradmin.ui.screens.requestdetail.RequestDetailScreen
import com.syntrixor.syntrixoradmin.ui.screens.splash.SplashScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(Screen.Admins.route) { backStackEntry ->
            val adminsVm: AdminsViewModel = viewModel(backStackEntry)
            val currentEntry by navController.currentBackStackEntryAsState()
            LaunchedEffect(currentEntry) {
                if (currentEntry?.destination?.route == Screen.Admins.route) {
                    adminsVm.load()
                }
            }
            AdminsScreen(
                onBack = { navController.popBackStack() },
                onAdminClick = { adminId ->
                    navController.navigate(
                        Screen.AdminDetail.createRoute(
                            adminId
                        )
                    )
                },
                onCreateAdmin = { navController.navigate(Screen.AdminDetail.createRoute(Screen.AdminDetail.NEW)) },
                vm = adminsVm
            )
        }

        composable(
            route = Screen.AdminDetail.route,
            arguments = listOf(navArgument("adminId") { type = NavType.StringType })
        ) { backStackEntry ->
            val adminId = backStackEntry.arguments?.getString("adminId")
            AdminDetailScreen(
                adminId = adminId,
                onSaveSuccess = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToRequestDetail = { requestId ->
                    navController.navigate(Screen.RequestDetail.createRoute(requestId))
                },
                onNavigateToCreateAnnouncement = {
                    navController.navigate(Screen.CreateAnnouncement.route)
                },
                onNavigateToAdmins = {
                    navController.navigate(Screen.Admins.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.RequestDetail.route,
            arguments = listOf(navArgument("requestId") { type = NavType.StringType })
        ) { backStackEntry ->
            val requestId = backStackEntry.arguments?.getString("requestId") ?: return@composable
            RequestDetailScreen(
                requestId = requestId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CreateAnnouncement.route) { backStackEntry ->
            // Scope the ViewModel to the Home back stack entry so AnnouncementsScreen
            // and CreateAnnouncementScreen share the same instance.
            val homeEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Home.route)
            }
            val announcementsVm: AnnouncementsViewModel = viewModel(homeEntry)
            CreateAnnouncementScreen(
                vm = announcementsVm,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
