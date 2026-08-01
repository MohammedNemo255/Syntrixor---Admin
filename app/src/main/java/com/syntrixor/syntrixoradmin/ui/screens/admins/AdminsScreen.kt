package com.syntrixor.syntrixoradmin.ui.screens.admins

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.syntrixor.syntrixoradmin.R
import com.syntrixor.syntrixoradmin.data.model.Admin
import com.syntrixor.syntrixoradmin.ui.components.SearchField
import com.syntrixor.syntrixoradmin.ui.components.SyntrixorTopBar
import com.syntrixor.syntrixoradmin.ui.theme.Dimens
import com.syntrixor.syntrixoradmin.ui.theme.Info
import com.syntrixor.syntrixoradmin.ui.theme.Success
import com.syntrixor.syntrixoradmin.ui.theme.Violet600

@Composable
fun AdminsScreen(
    onBack: () -> Unit,
    onAdminClick: (String) -> Unit,
    onCreateAdmin: () -> Unit,
    vm: AdminsViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val filtered = remember(state.admins, state.searchQuery) {
        if (state.searchQuery.isBlank()) state.admins
        else state.admins.filter {
            it.name.contains(state.searchQuery, ignoreCase = true) ||
                    it.email.contains(state.searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = { SyntrixorTopBar(stringResource(R.string.admins_title), onBack = onBack) },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateAdmin, containerColor = Violet600) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SearchField(
                query = state.searchQuery,
                onQueryChange = { vm.onEvent(AdminsEvent.SearchChanged(it)) },
                hint = stringResource(R.string.search_admins),
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
                            onClick = { vm.onEvent(AdminsEvent.Load) },
                            colors = ButtonDefaults.buttonColors(containerColor = Violet600)
                        ) { Text(stringResource(R.string.retry), color = Color.White) }
                    }
                }

                filtered.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_admins), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(Dimens.ScreenPaddingHorizontal),
                    verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSm)
                ) {
                    items(filtered, key = { it.id }) { admin ->
                        AdminCard(admin = admin, onClick = { onAdminClick(admin.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminCard(admin: Admin, onClick: () -> Unit) {
    val isSuperAdmin = admin.assignedCategories.isEmpty()
    val avatarColor = if (isSuperAdmin) Violet600 else Info

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusMd))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(Dimens.SpaceLg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.AvatarSizeMd)
                .clip(CircleShape)
                .background(avatarColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = admin.name.firstOrNull()?.uppercaseChar()?.toString() ?: "A",
                fontWeight = FontWeight.Bold,
                color = avatarColor,
                fontSize = 18.sp
            )
        }
        Spacer(Modifier.width(Dimens.SpaceLg))
        Column(Modifier.weight(1f)) {
            Text(admin.name, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(admin.email, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AdminChip(
                    label = stringResource(if (isSuperAdmin) R.string.super_admin_label else R.string.category_admin_label),
                    color = if (isSuperAdmin) Violet600 else Info
                )
                if (!isSuperAdmin) {
                    admin.assignedCategories.take(2).forEach { cat ->
                        AdminChip(label = stringResource(cat.labelRes), color = cat.color)
                    }
                    if (admin.assignedCategories.size > 2) {
                        AdminChip("+${admin.assignedCategories.size - 2}", MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        Spacer(Modifier.width(Dimens.SpaceSm))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(
                    if (admin.isActive) Success.copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = stringResource(if (admin.isActive) R.string.admin_active else R.string.admin_inactive),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (admin.isActive) Success else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AdminChip(label: String, color: Color) {
    Text(
        text = label,
        fontSize = 10.sp,
        color = color,
        maxLines = 1,
        softWrap = false,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}
