package com.syntrixor.syntrixoradmin.ui.screens.announcements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.syntrixor.syntrixoradmin.R
import com.syntrixor.syntrixoradmin.data.model.Announcement
import com.syntrixor.syntrixoradmin.ui.theme.Dimens
import com.syntrixor.syntrixoradmin.ui.theme.Success
import com.syntrixor.syntrixoradmin.ui.theme.Violet600

@Composable
fun AnnouncementsScreen(
    modifier: Modifier = Modifier,
    vm: AnnouncementsViewModel = viewModel(),
    onNavigateToCreate: () -> Unit = {}
) {
    val state by vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val postedMsg = stringResource(R.string.announcement_posted)

    // Image viewer state
    var viewerImageUri by remember { mutableStateOf<String?>(null) }

    // Navigate to Create/Edit when triggered by ViewModel
    LaunchedEffect(state.navigateToCreate) {
        if (state.navigateToCreate) {
            onNavigateToCreate()
            vm.onEvent(AnnouncementsEvent.ClearNavigateToCreate)
        }
    }

    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) {
            snackbarHostState.showSnackbar(postedMsg)
            vm.onEvent(AnnouncementsEvent.ClearMessage)
        }
    }

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
                        onClick = { vm.load() },
                        colors = ButtonDefaults.buttonColors(containerColor = Violet600)
                    ) { Text(stringResource(R.string.retry)) }
                }
            }
            state.announcements.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_announcements), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Dimens.ScreenPaddingHorizontal),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSm)
            ) {
                items(state.announcements, key = { it.id }) { item ->
                    AnnouncementCard(
                        announcement = item,
                        onImageClick  = { uri -> viewerImageUri = uri },
                        onEdit        = { vm.onEvent(AnnouncementsEvent.StartEdit(item.id)) },
                        onDelete      = { vm.onEvent(AnnouncementsEvent.DeleteAnnouncement(item.id)) },
                        onToggleActive = { vm.onEvent(AnnouncementsEvent.ToggleActive(item.id)) }
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }

    // Full-screen image viewer
    viewerImageUri?.let { uri ->
        FullScreenImageViewer(uri = uri, onDismiss = { viewerImageUri = null })
    }
}

@Composable
private fun AnnouncementCard(
    announcement: Announcement,
    onImageClick: (String) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleActive: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusMd))
            .background(
                if (announcement.isActive) MaterialTheme.colorScheme.surface
                else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Image (clickable → full-screen viewer)
            if (announcement.imageUri != null) {
                AsyncImage(
                    model = announcement.imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clickable { onImageClick(announcement.imageUri) },
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(Dimens.SpaceLg)) {
                // Leave room on the right for the 3-dot button
                Text(
                    text = announcement.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = if (announcement.isActive) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 32.dp)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = announcement.body,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnnChip(
                        label = stringResource(announcement.category.labelRes),
                        color = Violet600
                    )
                    AnnChip(
                        label = stringResource(announcement.priority.labelRes),
                        color = priorityColor(announcement.priority)
                    )
                    Spacer(Modifier.weight(1f))
                    // Status badge
                    val statusLabel: String
                    val statusColor: androidx.compose.ui.graphics.Color
                    when {
                        announcement.scheduledAt != null -> {
                            statusLabel = stringResource(R.string.status_scheduled)
                            statusColor = Violet600
                        }
                        announcement.isActive -> {
                            statusLabel = stringResource(R.string.status_active)
                            statusColor = Success
                        }
                        else -> {
                            statusLabel = stringResource(R.string.status_inactive)
                            statusColor = MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    }
                    AnnChip(label = statusLabel, color = statusColor)
                }
                Spacer(Modifier.height(4.dp))
                val displayDate = announcement.scheduledAt ?: announcement.postedAt.takeIf { it.isNotBlank() }
                if (displayDate != null) {
                    Text(
                        text = displayDate,
                        fontSize = 10.sp,
                        color = if (announcement.scheduledAt != null) Violet600
                                else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = stringResource(R.string.coming_soon),
                        fontSize = 10.sp,
                        color = Violet600
                    )
                }
            }
        }

        // ── 3-dot overflow menu ──────────────────────────────────────────────
        Box(modifier = Modifier.align(Alignment.TopEnd)) {
            IconButton(
                onClick = { menuExpanded = true },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.ann_edit), fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Filled.Edit, null, modifier = Modifier.size(18.dp)) },
                    onClick = { menuExpanded = false; onEdit() }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(if (announcement.isActive) R.string.ann_deactivate else R.string.ann_activate),
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            if (announcement.isActive) Icons.Filled.Block else Icons.Filled.CheckCircle,
                            null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    onClick = { menuExpanded = false; onToggleActive() }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.ann_delete), fontSize = 14.sp, color = MaterialTheme.colorScheme.error) },
                    leadingIcon = { Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp)) },
                    onClick = { menuExpanded = false; onDelete() }
                )
            }
        }
    }
}
