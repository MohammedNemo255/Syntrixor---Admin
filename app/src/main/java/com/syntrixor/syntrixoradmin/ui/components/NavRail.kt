package com.syntrixor.syntrixoradmin.ui.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.syntrixor.syntrixoradmin.ui.theme.SyntrixorAdminPreviewTheme

@Composable
fun SyntrixorNavRail(
    selectedRoute: String,
    onTabSelected: (String) -> Unit
) {
    NavigationRail(
        modifier = Modifier.fillMaxHeight(),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        bottomNavItems.forEach { item ->
            val selected = selectedRoute == item.route
            NavigationRailItem(
                selected = selected,
                onClick = { onTabSelected(item.route) },
                icon = { Icon(imageVector = item.icon, contentDescription = null) },
                label = { Text(text = stringResource(item.labelRes)) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                )
            )
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Light")
@Composable
private fun PreviewSyntrixorNavRailLight() {
    SyntrixorAdminPreviewTheme(darkTheme = false) {
        SyntrixorNavRail(selectedRoute = "dashboard", onTabSelected = {})
    }
}

@Preview(showBackground = true, name = "Dark")
@Composable
private fun PreviewSyntrixorNavRailDark() {
    SyntrixorAdminPreviewTheme(darkTheme = true) {
        SyntrixorNavRail(selectedRoute = "technicians", onTabSelected = {})
    }
}
