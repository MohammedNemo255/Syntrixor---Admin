package com.syntrixor.syntrixoradmin.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.syntrixor.syntrixoradmin.R
import com.syntrixor.syntrixoradmin.data.model.Category
import com.syntrixor.syntrixoradmin.data.model.DashboardStats
import com.syntrixor.syntrixoradmin.data.model.MaintenanceRequest
import com.syntrixor.syntrixoradmin.data.model.Priority
import com.syntrixor.syntrixoradmin.data.model.RequestStatus
import com.syntrixor.syntrixoradmin.ui.components.RequestCard
import com.syntrixor.syntrixoradmin.ui.theme.Dimens
import com.syntrixor.syntrixoradmin.ui.theme.Info
import com.syntrixor.syntrixoradmin.ui.theme.Success
import com.syntrixor.syntrixoradmin.ui.theme.SyntrixorAdminPreviewTheme
import com.syntrixor.syntrixoradmin.ui.theme.Violet600
import com.syntrixor.syntrixoradmin.ui.theme.Warning

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    onRequestClick: (String) -> Unit,
    onViewAllRequests: () -> Unit,
    onNavigateToTechnicians: () -> Unit = {},
    onNavigateToResidents: () -> Unit = {},
    vm: DashboardViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    DashboardContent(
        modifier = modifier,
        state = state,
        onRequestClick = onRequestClick,
        onViewAllRequests = onViewAllRequests,
        onNavigateToTechnicians = onNavigateToTechnicians,
        onNavigateToResidents = onNavigateToResidents,
        onRetry = { vm.load() }
    )
}

// ── Stateless UI (previewable) ───────────────────────────────────────────────

@Composable
private fun DashboardContent(
    modifier: Modifier = Modifier,
    state: DashboardState,
    onRequestClick: (String) -> Unit,
    onViewAllRequests: () -> Unit,
    onNavigateToTechnicians: () -> Unit = {},
    onNavigateToResidents: () -> Unit = {},
    onRetry: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Violet600)
            }

            state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.error ?: "", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(containerColor = Violet600)
                    ) { Text(stringResource(R.string.retry), color = Color.White) }
                }
            }

            state.stats != null -> {
                val stats = state.stats!!
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = Dimens.ScreenPaddingHorizontal,
                        vertical = Dimens.SpaceLg
                    ),
                    verticalArrangement = Arrangement.spacedBy(Dimens.SpaceLg)
                ) {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMd)) {
                            StatCard(stringResource(R.string.stat_total),               "${stats.totalRequests}",      Violet600, Modifier.weight(1f), onClick = onViewAllRequests)
                            StatCard(stringResource(R.string.pending_requests),          "${stats.pendingRequests}",    Warning,   Modifier.weight(1f), onClick = onViewAllRequests)
                            StatCard(stringResource(R.string.in_progress_requests),      "${stats.inProgressRequests}", Info,      Modifier.weight(1f), onClick = onViewAllRequests)
                            StatCard(stringResource(R.string.completed_requests),        "${stats.completedRequests}",  Success,   Modifier.weight(1f), onClick = onViewAllRequests)
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMd)) {
                            StatCard(stringResource(R.string.stat_technicians), "${stats.activeTechnicians}", Violet600, Modifier.weight(1f), onClick = onNavigateToTechnicians)
                            StatCard(stringResource(R.string.stat_residents),   "${stats.totalResidents}",    Info,      Modifier.weight(1f), onClick = onNavigateToResidents)
                        }
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                stringResource(R.string.recent_requests),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            TextButton(onClick = onViewAllRequests) {
                                Text(stringResource(R.string.view_all), color = Violet600, fontSize = 13.sp)
                            }
                        }
                    }
                    items(stats.recentRequests) { request ->
                        RequestCard(request = request, onClick = { onRequestClick(request.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.RadiusMd))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
        Spacer(Modifier.height(2.dp))
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

private val previewRecentRequests = listOf(
    MaintenanceRequest(
        id = "req001",
        residentId = "r1",
        residentName = "Nadia Mostafa",
        unit = "A-101",
        category = Category.PLUMBING,
        priority = Priority.HIGH,
        title = "Leaking bathroom pipe",
        description = "Pipe under the sink has been leaking for two days.",
        status = RequestStatus.PENDING,
        assignedTechnicianId = null,
        assignedTechnicianName = null,
        submittedAt = "2026-06-28 09:15",
        updatedAt = "2026-06-28 09:15",
        scheduledAt = null
    ),
    MaintenanceRequest(
        id = "req002",
        residentId = "r2",
        residentName = "Sara Ahmed",
        unit = "A-203",
        category = Category.ELECTRICAL,
        priority = Priority.URGENT,
        title = "Power outlet not working",
        description = "The outlet in the kitchen stopped working suddenly.",
        status = RequestStatus.IN_PROGRESS,
        assignedTechnicianId = "t2",
        assignedTechnicianName = "Mohamed Samir",
        submittedAt = "2026-06-27 14:30",
        updatedAt = "2026-06-28 10:00",
        scheduledAt = "2026-06-29 10:00"
    )
)

@Preview(showBackground = true, name = "Light")
@Composable
private fun PreviewDashboardLight() {
    SyntrixorAdminPreviewTheme(darkTheme = false) {
        DashboardContent(
            state = DashboardState(
                isLoading = false,
                stats = DashboardStats(
                    totalRequests = 10,
                    pendingRequests = 4,
                    inProgressRequests = 3,
                    completedRequests = 2,
                    activeTechnicians = 4,
                    totalResidents = 8,
                    recentRequests = previewRecentRequests
                )
            ),
            onRequestClick = {},
            onViewAllRequests = {},
            onNavigateToTechnicians = {},
            onNavigateToResidents = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark")
@Composable
private fun PreviewDashboardDark() {
    SyntrixorAdminPreviewTheme(darkTheme = true) {
        DashboardContent(
            state = DashboardState(
                isLoading = false,
                stats = DashboardStats(
                    totalRequests = 10,
                    pendingRequests = 4,
                    inProgressRequests = 3,
                    completedRequests = 2,
                    activeTechnicians = 4,
                    totalResidents = 8,
                    recentRequests = previewRecentRequests
                )
            ),
            onRequestClick = {},
            onViewAllRequests = {},
            onNavigateToTechnicians = {},
            onNavigateToResidents = {}
        )
    }
}
