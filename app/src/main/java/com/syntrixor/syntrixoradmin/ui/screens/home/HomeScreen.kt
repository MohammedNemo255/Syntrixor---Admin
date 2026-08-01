package com.syntrixor.syntrixoradmin.ui.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.syntrixor.syntrixoradmin.R
import com.syntrixor.syntrixoradmin.ui.components.EmptyDetailPane
import com.syntrixor.syntrixoradmin.ui.components.SyntrixorBottomNavBar
import com.syntrixor.syntrixoradmin.ui.components.SyntrixorNavRail
import com.syntrixor.syntrixoradmin.ui.screens.announcements.AnnouncementsEvent
import com.syntrixor.syntrixoradmin.ui.screens.announcements.AnnouncementsScreen
import com.syntrixor.syntrixoradmin.ui.screens.announcements.AnnouncementsViewModel
import com.syntrixor.syntrixoradmin.ui.screens.dashboard.DashboardScreen
import com.syntrixor.syntrixoradmin.ui.screens.profile.ProfileScreen
import com.syntrixor.syntrixoradmin.data.model.Resident
import com.syntrixor.syntrixoradmin.data.model.Technician
import com.syntrixor.syntrixoradmin.ui.screens.requestdetail.RequestDetailScreen
import com.syntrixor.syntrixoradmin.ui.screens.requests.RequestsScreen
import com.syntrixor.syntrixoradmin.ui.screens.requests.RequestsViewModel
import com.syntrixor.syntrixoradmin.ui.screens.residents.ResidentDetailPane
import com.syntrixor.syntrixoradmin.ui.screens.residents.ResidentsScreen
import com.syntrixor.syntrixoradmin.ui.screens.technicians.TechnicianDetailPane
import com.syntrixor.syntrixoradmin.ui.screens.technicians.TechniciansScreen
import com.syntrixor.syntrixoradmin.ui.theme.SyntrixorAdminPreviewTheme
import com.syntrixor.syntrixoradmin.ui.theme.Violet600
import com.syntrixor.syntrixoradmin.utils.LocalIsTablet

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
    val isTablet = LocalIsTablet.current

    val announcementsVm: AnnouncementsViewModel = viewModel()
    val requestsVm: RequestsViewModel = viewModel()

    // Intercept system back press while profile is open
    BackHandler(enabled = showProfile) {
        showProfile = false
    }

    // Profile overlay — full screen, no bottom nav
    if (showProfile) {
        ProfileScreen(
            modifier = Modifier.fillMaxSize(),
            onBack = { showProfile = false },
            onLogout = onLogout,
            onNavigateToAdmins = { onNavigateToAdmins() }
        )
        return
    }

    val topBarTitle = when (selectedTab) {
        "dashboard" -> stringResource(R.string.dashboard_title)
        "requests" -> stringResource(R.string.requests_title)
        "technicians" -> stringResource(R.string.technicians_title)
        "residents" -> stringResource(R.string.residents_title)
        "announcements" -> stringResource(R.string.announcements_title)
        else -> stringResource(R.string.app_name)
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
            // On tablet the same items live in a permanent side rail instead.
            if (!isTablet) {
                SyntrixorBottomNavBar(
                    selectedRoute = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
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
        if (isTablet) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                SyntrixorNavRail(
                    selectedRoute = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
                HomeContent(
                    selectedTab = selectedTab,
                    isTablet = true,
                    onSelectTab = { selectedTab = it },
                    onNavigateToRequestDetail = onNavigateToRequestDetail,
                    onNavigateToCreateAnnouncement = onNavigateToCreateAnnouncement,
                    announcementsVm = announcementsVm,
                    requestsVm = requestsVm,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        } else {
            HomeContent(
                selectedTab = selectedTab,
                isTablet = false,
                onSelectTab = { selectedTab = it },
                onNavigateToRequestDetail = onNavigateToRequestDetail,
                onNavigateToCreateAnnouncement = onNavigateToCreateAnnouncement,
                announcementsVm = announcementsVm,
                requestsVm = requestsVm,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}

@Composable
private fun HomeContent(
    selectedTab: String,
    isTablet: Boolean,
    onSelectTab: (String) -> Unit,
    onNavigateToRequestDetail: (String) -> Unit,
    onNavigateToCreateAnnouncement: () -> Unit,
    announcementsVm: AnnouncementsViewModel,
    requestsVm: RequestsViewModel,
    modifier: Modifier = Modifier
) {
    when (selectedTab) {
        "dashboard" -> DashboardScreen(
            modifier = modifier,
            onRequestClick = onNavigateToRequestDetail,
            onViewAllRequests = { onSelectTab("requests") },
            onNavigateToTechnicians = { onSelectTab("technicians") },
            onNavigateToResidents = { onSelectTab("residents") }
        )

        "requests" -> {
            if (isTablet) {
                var selectedRequestId by rememberSaveable { mutableStateOf<String?>(null) }
                Row(modifier = modifier) {
                    RequestsScreen(
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight(),
                        onRequestClick = { selectedRequestId = it },
                        vm = requestsVm
                    )
                    VerticalDivider()
                    if (selectedRequestId != null) {
                        key(selectedRequestId) {
                            RequestDetailScreen(
                                requestId = selectedRequestId!!,
                                onBack = { selectedRequestId = null },
                                onStatusChanged = { requestsVm.load() },
                                modifier = Modifier
                                    .weight(0.6f)
                                    .fillMaxHeight()
                            )
                        }
                    } else {
                        EmptyDetailPane(
                            message = stringResource(R.string.select_a_request),
                            modifier = Modifier
                                .weight(0.6f)
                                .fillMaxHeight()
                        )
                    }
                }
            } else {
                RequestsScreen(
                    modifier = modifier,
                    onRequestClick = onNavigateToRequestDetail,
                    vm = requestsVm
                )
            }
        }

        "technicians" -> {
            if (isTablet) {
                var selectedTechnician by remember { mutableStateOf<Technician?>(null) }
                Row(modifier = modifier) {
                    TechniciansScreen(
                        modifier = Modifier.weight(0.4f).fillMaxHeight(),
                        onTechnicianClick = { selectedTechnician = it }
                    )
                    VerticalDivider()
                    if (selectedTechnician != null) {
                        TechnicianDetailPane(
                            technician = selectedTechnician!!,
                            onBack = { selectedTechnician = null },
                            modifier = Modifier.weight(0.6f).fillMaxHeight()
                        )
                    } else {
                        EmptyDetailPane(
                            message = stringResource(R.string.select_a_technician),
                            modifier = Modifier.weight(0.6f).fillMaxHeight()
                        )
                    }
                }
            } else {
                TechniciansScreen(modifier = modifier)
            }
        }

        "residents" -> {
            if (isTablet) {
                var selectedResident by remember { mutableStateOf<Resident?>(null) }
                Row(modifier = modifier) {
                    ResidentsScreen(
                        modifier = Modifier.weight(0.4f).fillMaxHeight(),
                        onResidentClick = { selectedResident = it }
                    )
                    VerticalDivider()
                    if (selectedResident != null) {
                        ResidentDetailPane(
                            resident = selectedResident!!,
                            onBack = { selectedResident = null },
                            modifier = Modifier.weight(0.6f).fillMaxHeight()
                        )
                    } else {
                        EmptyDetailPane(
                            message = stringResource(R.string.select_a_resident),
                            modifier = Modifier.weight(0.6f).fillMaxHeight()
                        )
                    }
                }
            } else {
                ResidentsScreen(modifier = modifier)
            }
        }
        "announcements" -> AnnouncementsScreen(
            modifier = modifier,
            vm = announcementsVm,
            onNavigateToCreate = onNavigateToCreateAnnouncement
        )
    }
}

// ── Preview shell — Scaffold chrome (top bar + nav) without inner screens ─────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeShell(selectedTab: String, isTablet: Boolean) {
    val topBarTitle = when (selectedTab) {
        "dashboard"     -> "Dashboard"
        "requests"      -> "Requests"
        "technicians"   -> "Technicians"
        "residents"     -> "Residents"
        "announcements" -> "Announcements"
        else            -> "Syntrixor Admin"
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(topBarTitle, style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold)
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            if (!isTablet) {
                SyntrixorBottomNavBar(selectedRoute = selectedTab, onTabSelected = {})
            }
        }
    ) { innerPadding ->
        if (isTablet) {
            Row(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                SyntrixorNavRail(selectedRoute = selectedTab, onTabSelected = {})
                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text("Content area", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Content area", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Phone – Light – Dashboard")
@Composable
private fun PreviewHomePhoneLight() {
    SyntrixorAdminPreviewTheme(darkTheme = false) {
        CompositionLocalProvider(LocalIsTablet provides false) {
            HomeShell(selectedTab = "dashboard", isTablet = false)
        }
    }
}

@Preview(showBackground = true, name = "Phone – Dark – Requests")
@Composable
private fun PreviewHomePhoneDark() {
    SyntrixorAdminPreviewTheme(darkTheme = true) {
        CompositionLocalProvider(LocalIsTablet provides false) {
            HomeShell(selectedTab = "requests", isTablet = false)
        }
    }
}

@Preview(showBackground = true, widthDp = 1280, heightDp = 800, name = "Tablet – Light – Dashboard")
@Composable
private fun PreviewHomeTabletLight() {
    SyntrixorAdminPreviewTheme(darkTheme = false) {
        CompositionLocalProvider(LocalIsTablet provides true) {
            HomeShell(selectedTab = "dashboard", isTablet = true)
        }
    }
}

@Preview(showBackground = true, widthDp = 1280, heightDp = 800, name = "Tablet – Dark – Technicians")
@Composable
private fun PreviewHomeTabletDark() {
    SyntrixorAdminPreviewTheme(darkTheme = true) {
        CompositionLocalProvider(LocalIsTablet provides true) {
            HomeShell(selectedTab = "technicians", isTablet = true)
        }
    }
}
