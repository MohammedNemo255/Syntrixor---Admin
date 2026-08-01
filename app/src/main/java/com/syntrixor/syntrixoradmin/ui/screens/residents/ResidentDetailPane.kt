package com.syntrixor.syntrixoradmin.ui.screens.residents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.syntrixor.syntrixoradmin.R
import com.syntrixor.syntrixoradmin.data.model.Resident
import com.syntrixor.syntrixoradmin.ui.components.SyntrixorTopBar
import com.syntrixor.syntrixoradmin.ui.theme.Dimens
import com.syntrixor.syntrixoradmin.ui.theme.Info
import com.syntrixor.syntrixoradmin.ui.theme.SyntrixorAdminPreviewTheme

@Composable
fun ResidentDetailPane(
    resident: Resident,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { SyntrixorTopBar(title = resident.name, onBack = onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(Dimens.ScreenPaddingHorizontal),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(Dimens.Space2Xl))

            // Avatar
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Info.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = resident.name.first().toString(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Info
                )
            }

            Spacer(Modifier.height(Dimens.SpaceLg))

            Text(
                text = resident.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(Dimens.SpaceXs))
            Text(
                text = resident.email,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(Dimens.SpaceXs))
            Text(
                text = resident.phone,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(Dimens.Space2Xl))

            // Stats card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.RadiusMd))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(Dimens.SpaceLg),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMd)
            ) {
                DetailRow(stringResource(R.string.unit), resident.unit)
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                DetailRow(stringResource(R.string.building), resident.building)
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                DetailRow(stringResource(R.string.floor), resident.floor.toString())
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                DetailRow(stringResource(R.string.phone), resident.phone)
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                DetailRow(stringResource(R.string.total_requests), resident.totalRequests.toString())
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                DetailRow(stringResource(R.string.join_date), resident.joinedDate)
            }

            Spacer(Modifier.height(Dimens.SpaceXl))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

private val previewResident = Resident(
    id = "r1", name = "Nadia Mostafa", email = "nadia@example.com",
    phone = "+20 111 100 0011", building = "A", floor = 1,
    unit = "A-101", totalRequests = 5, joinedDate = "Jan 2024"
)

@Preview(showBackground = true, name = "Light")
@Composable
private fun PreviewResidentDetailLight() {
    SyntrixorAdminPreviewTheme(darkTheme = false) {
        ResidentDetailPane(resident = previewResident, onBack = {})
    }
}

@Preview(showBackground = true, name = "Dark")
@Composable
private fun PreviewResidentDetailDark() {
    SyntrixorAdminPreviewTheme(darkTheme = true) {
        ResidentDetailPane(resident = previewResident, onBack = {})
    }
}
