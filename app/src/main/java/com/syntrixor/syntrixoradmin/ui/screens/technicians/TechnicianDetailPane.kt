package com.syntrixor.syntrixoradmin.ui.screens.technicians

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
import com.syntrixor.syntrixoradmin.data.model.Technician
import com.syntrixor.syntrixoradmin.ui.components.SyntrixorTopBar
import com.syntrixor.syntrixoradmin.ui.theme.Dimens
import com.syntrixor.syntrixoradmin.ui.theme.Success
import com.syntrixor.syntrixoradmin.ui.theme.SyntrixorAdminPreviewTheme
import com.syntrixor.syntrixoradmin.ui.theme.Violet600

@Composable
fun TechnicianDetailPane(
    technician: Technician,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { SyntrixorTopBar(title = technician.name, onBack = onBack) },
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
                    .background(Violet600.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = technician.name.first().toString(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Violet600
                )
            }

            Spacer(Modifier.height(Dimens.SpaceLg))

            Text(
                text = technician.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(Dimens.SpaceXs))
            Text(
                text = technician.phone,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(Dimens.SpaceMd))

            // Status badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (technician.isActive) Success.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(if (technician.isActive) R.string.active else R.string.inactive),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (technician.isActive) Success else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

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
                DetailRow(stringResource(R.string.phone), technician.phone)
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                DetailRow(stringResource(R.string.specialization), technician.specialization)
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                DetailRow(stringResource(R.string.open_requests), technician.openRequestsCount.toString())
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                DetailRow(stringResource(R.string.completed), technician.completedCount.toString())
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                DetailRow(stringResource(R.string.rating), "★ ${"%.1f".format(technician.rating)}")
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

private val previewTechnician = Technician(
    id = "t1", name = "Ahmed Hassan", specialization = "Plumbing",
    phone = "+20 100 111 2233", isActive = true,
    openRequestsCount = 3, completedCount = 47, rating = 4.8f
)

@Preview(showBackground = true, name = "Light")
@Composable
private fun PreviewTechnicianDetailLight() {
    SyntrixorAdminPreviewTheme(darkTheme = false) {
        TechnicianDetailPane(technician = previewTechnician, onBack = {})
    }
}

@Preview(showBackground = true, name = "Dark")
@Composable
private fun PreviewTechnicianDetailDark() {
    SyntrixorAdminPreviewTheme(darkTheme = true) {
        TechnicianDetailPane(technician = previewTechnician, onBack = {})
    }
}
