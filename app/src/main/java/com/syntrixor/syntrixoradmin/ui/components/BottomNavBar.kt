package com.syntrixor.syntrixoradmin.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.syntrixor.syntrixoradmin.R

data class BottomNavItem(val labelRes: Int, val icon: ImageVector, val route: String)

val bottomNavItems = listOf(
    BottomNavItem(R.string.nav_dashboard,     Icons.Filled.Dashboard,   "dashboard"),
    BottomNavItem(R.string.nav_requests,      Icons.Filled.Home,        "requests"),
    BottomNavItem(R.string.nav_technicians,   Icons.Filled.Engineering, "technicians"),
    BottomNavItem(R.string.nav_residents,     Icons.Filled.Groups,      "residents"),
    BottomNavItem(R.string.nav_announcements, Icons.Filled.Campaign,    "announcements"),
)

@Composable
fun SyntrixorBottomNavBar(
    selectedRoute: String,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        bottomNavItems.forEach { item ->
            val selected = selectedRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(item.route) },
                icon = { Icon(imageVector = item.icon, contentDescription = null) },
                label = { Text(text = stringResource(item.labelRes), fontSize = 9.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = MaterialTheme.colorScheme.primary,
                    selectedTextColor   = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor      = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                )
            )
        }
    }
}
