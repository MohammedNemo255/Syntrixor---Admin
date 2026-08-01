package com.syntrixor.syntrixoradmin.ui.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.syntrixor.syntrixoradmin.R
import com.syntrixor.syntrixoradmin.ui.components.SyntrixorBottomNavBar
import com.syntrixor.syntrixoradmin.ui.screens.announcements.AnnouncementsEvent
import com.syntrixor.syntrixoradmin.ui.screens.announcements.AnnouncementsScreen
import com.syntrixor.syntrixoradmin.ui.screens.announcements.AnnouncementsViewModel
import com.syntrixor.syntrixoradmin.ui.screens.dashboard.DashboardScreen
import com.syntrixor.syntrixoradmin.ui.screens.profile.ProfileScreen
import com.syntrixor.syntrixoradmin.ui.screens.requests.RequestsScreen
import com.syntrixor.syntrixoradmin.ui.screens.residents.ResidentsScreen
import com.syntrixor.syntrixoradmin.ui.screens.technicians.TechniciansScreen
import com.syntrixor.syntrixoradmin.ui.theme.Violet600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToRequestDetail: (String) -> Unit,
    onNavigateToCreateAnnouncement: () -> Unit,
    onNavigateToAdmins: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableStateOf("dashboard") }
    var showProfile by rememberSaveable { mutableStateOf(false) }

    val announcementsVm: AnnouncementsViewModel = viewModel()

    // Intercept system back press while profile is open
    BackHandler(enabled = showProfile) {
        showProfile = false
    }

    // Profile overlay — full screen, no bottom nav
    if (showProfile) {
        ProfileScreen(
            modifier           = Modifier.fillMaxSize(),
            onBack             = { showProfile = false },
            onLogout           = onLogout,
            onNavigateToAdmins = { onNavigateToAdmins() }
        )
        return
    }

    val topBarTitle = when (selectedTab) {
        "dashboard"     -> stringResource(R.string.dashboard_title)
        "requests"      -> stringResource(R.string.requests_title)
        "technicians"   -> stringResource(R.string.technicians_title)
        "residents"     -> stringResource(R.string.residents_title)
        "announcements" -> stringResource(R.string.announcements_title)
        else            -> stringResource(R.string.app_name)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = topBarTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = { showProfile = true }) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            SyntrixorBottomNavBar(
                selectedRoute = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        floatingActionButton = {
            if (selectedTab == "announcements") {
                FloatingActionButton(
                    onClick = {
                        announcementsVm.onEvent(AnnouncementsEvent.ResetForm)
                        onNavigateToCreateAnnouncement()
                    },
                    containerColor = Violet600
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White)
                }
            }
        }
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)

        when (selectedTab) {
            "dashboard"     -> DashboardScreen(
                modifier                 = contentModifier,
                onRequestClick           = onNavigateToRequestDetail,
                onViewAllRequests        = { selectedTab = "requests" },
                onNavigateToTechnicians  = { selectedTab = "technicians" },
                onNavigateToResidents    = { selectedTab = "residents" }
            )
            "requests"      -> RequestsScreen(
                modifier       = contentModifier,
                onRequestClick = onNavigateToRequestDetail
            )
            "technicians"   -> TechniciansScreen(modifier = contentModifier)
            "residents"     -> ResidentsScreen(modifier = contentModifier)
            "announcements" -> AnnouncementsScreen(
                modifier = contentModifier,
                vm = announcementsVm,
                onNavigateToCreate = onNavigateToCreateAnnouncement
            )
        }
    }
}
