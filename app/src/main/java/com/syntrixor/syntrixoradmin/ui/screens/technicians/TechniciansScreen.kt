package com.syntrixor.syntrixoradmin.ui.screens.technicians

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.syntrixor.syntrixoradmin.data.model.Technician
import com.syntrixor.syntrixoradmin.ui.components.SearchField
import com.syntrixor.syntrixoradmin.ui.theme.Dimens
import com.syntrixor.syntrixoradmin.ui.theme.Success
import com.syntrixor.syntrixoradmin.ui.theme.SyntrixorAdminPreviewTheme
import com.syntrixor.syntrixoradmin.ui.theme.TextSecondary
import com.syntrixor.syntrixoradmin.ui.theme.Violet600

@Composable
fun TechniciansScreen(modifier: Modifier = Modifier, vm: TechniciansViewModel = viewModel()) {
    val state by vm.state.collectAsState()

    TechniciansContent(
        state = state,
        onSearchChange = { vm.setSearch(it) },
        onRetry = { vm.load() },
        modifier = modifier
    )
}

@Composable
private fun TechniciansContent(
    state: TechniciansState,
    onSearchChange: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SearchField(
            query = state.searchQuery,
            onQueryChange = onSearchChange,
            hint = stringResource(R.string.search_technicians),
            modifier = Modifier.padding(horizontal = Dimens.ScreenPaddingHorizontal, vertical = Dimens.SpaceSm)
        )

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
            state.technicians.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_technicians), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Dimens.ScreenPaddingHorizontal),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSm)
            ) {
                items(state.technicians) { tech -> TechnicianCard(tech) }
            }
        }
    }
}

@Composable
private fun TechnicianCard(tech: Technician) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusMd))
            .background(MaterialTheme.colorScheme.surface)
            .padding(Dimens.SpaceLg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.AvatarSizeMd)
                .clip(CircleShape)
                .background(Violet600.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(tech.name.first().toString(), fontWeight = FontWeight.Bold, color = Violet600, fontSize = 18.sp)
        }
        Spacer(Modifier.width(Dimens.SpaceLg))
        Column(Modifier.weight(1f)) {
            Text(tech.name, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(tech.specialization, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(R.string.tech_stats, tech.openRequestsCount, tech.completedCount, tech.rating),
                fontSize = 11.sp, color = TextSecondary
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (tech.isActive) Success.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = stringResource(if (tech.isActive) R.string.active else R.string.inactive),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (tech.isActive) Success else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

private val previewTechnicians = listOf(
    Technician("t1", "Ahmed Hassan", "Plumbing", "+20 100 111 2233", true, 3, 47, 4.8f),
    Technician("t2", "Mohamed Samir", "Electrical", "+20 100 222 3344", true, 2, 61, 4.7f),
    Technician("t3", "Khaled Ibrahim", "HVAC", "+20 100 333 4455", true, 1, 33, 4.9f),
    Technician("t4", "Omar Farouk", "Carpentry", "+20 100 444 5566", false, 0, 28, 4.5f),
    Technician("t5", "Youssef Nasser", "General", "+20 100 555 6677", true, 4, 55, 4.6f),
)

@Preview(showBackground = true, name = "Light")
@Composable
private fun PreviewTechniciansScreenLight() {
    SyntrixorAdminPreviewTheme(darkTheme = false) {
        TechniciansContent(
            state = TechniciansState(technicians = previewTechnicians, isLoading = false),
            onSearchChange = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark")
@Composable
private fun PreviewTechniciansScreenDark() {
    SyntrixorAdminPreviewTheme(darkTheme = true) {
        TechniciansContent(
            state = TechniciansState(technicians = previewTechnicians, isLoading = false),
            onSearchChange = {},
            onRetry = {}
        )
    }
}
