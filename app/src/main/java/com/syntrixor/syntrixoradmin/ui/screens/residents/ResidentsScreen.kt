package com.syntrixor.syntrixoradmin.ui.screens.residents

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
import com.syntrixor.syntrixoradmin.data.model.Resident
import com.syntrixor.syntrixoradmin.ui.components.SearchField
import com.syntrixor.syntrixoradmin.ui.theme.Dimens
import com.syntrixor.syntrixoradmin.ui.theme.Info
import com.syntrixor.syntrixoradmin.ui.theme.SyntrixorAdminPreviewTheme
import com.syntrixor.syntrixoradmin.ui.theme.Violet600

@Composable
fun ResidentsScreen(modifier: Modifier = Modifier, vm: ResidentsViewModel = viewModel()) {
    val state by vm.state.collectAsState()

    ResidentsContent(
        state = state,
        onSearchChange = { vm.setSearch(it) },
        onRetry = { vm.load() },
        modifier = modifier
    )
}

@Composable
private fun ResidentsContent(
    state: ResidentsState,
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
            hint = stringResource(R.string.search_residents),
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
            state.residents.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_residents), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Dimens.ScreenPaddingHorizontal),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSm)
            ) {
                items(state.residents) { resident -> ResidentCard(resident) }
            }
        }
    }
}

@Composable
private fun ResidentCard(resident: Resident) {
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
                .background(Info.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(resident.name.first().toString(), fontWeight = FontWeight.Bold, color = Info, fontSize = 18.sp)
        }
        Spacer(Modifier.width(Dimens.SpaceLg))
        Column(Modifier.weight(1f)) {
            Text(resident.name, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(resident.email, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(R.string.resident_stats, resident.unit, resident.building, resident.totalRequests),
                fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            stringResource(R.string.floor_label, resident.floor),
            fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Violet600
        )
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

private val previewResidents = listOf(
    Resident("r1", "Nadia Mostafa", "nadia@example.com", "+20 111 100 0011", "A", 1, "A-101", 5, "Jan 2024"),
    Resident("r2", "Sara Ahmed", "sara@example.com", "+20 111 200 0022", "A", 2, "A-203", 3, "Feb 2024"),
    Resident("r3", "Hana Khalil", "hana@example.com", "+20 111 300 0033", "B", 1, "B-104", 8, "Mar 2024"),
    Resident("r4", "Layla Ibrahim", "layla@example.com", "+20 111 400 0044", "B", 3, "B-312", 2, "Apr 2024"),
)

@Preview(showBackground = true, name = "Light")
@Composable
private fun PreviewResidentsScreenLight() {
    SyntrixorAdminPreviewTheme(darkTheme = false) {
        ResidentsContent(
            state = ResidentsState(residents = previewResidents, isLoading = false),
            onSearchChange = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark")
@Composable
private fun PreviewResidentsScreenDark() {
    SyntrixorAdminPreviewTheme(darkTheme = true) {
        ResidentsContent(
            state = ResidentsState(residents = previewResidents, isLoading = false),
            onSearchChange = {},
            onRetry = {}
        )
    }
}
