package com.syntrixor.syntrixoradmin.ui.screens.announcements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.syntrixor.syntrixoradmin.R
import com.syntrixor.syntrixoradmin.data.AppModule
import com.syntrixor.syntrixoradmin.data.model.AnnouncementCategory
import com.syntrixor.syntrixoradmin.data.model.AnnouncementPriority
import com.syntrixor.syntrixoradmin.data.model.AnnouncementTarget
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.syntrixor.syntrixoradmin.ui.theme.Dimens
import com.syntrixor.syntrixoradmin.ui.theme.SyntrixorAdminTheme
import com.syntrixor.syntrixoradmin.ui.theme.Violet600

// ── ViewModel wrapper ────────────────────────────────────────────────────────

@Composable
fun CreateAnnouncementScreen(
    vm: AnnouncementsViewModel,
    onBack: () -> Unit
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(state.postSuccess) {
        if (state.postSuccess) {
            vm.onEvent(AnnouncementsEvent.ClearPostSuccess)
            onBack()
        }
    }

    CreateAnnouncementContent(
        state = state,
        onEvent = vm::onEvent,
        onBack = onBack
    )
}

// ── Stateless UI (previewable) ───────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateAnnouncementContent(
    state: AnnouncementsState,
    onEvent: (AnnouncementsEvent) -> Unit,
    onBack: () -> Unit
) {
    var showTargetSheet by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var pendingDateMillis by remember { mutableStateOf<Long?>(null) }
    var userPickedDate by remember { mutableStateOf(false) }

    val imageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) onEvent(AnnouncementsEvent.ImageAttached(uri))
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        // ── Fixed top bar ────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .statusBarsPadding()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.navigate_back),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = stringResource(if (state.editingId != null) R.string.edit_announcement else R.string.new_announcement),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { onEvent(AnnouncementsEvent.Post) },
                enabled = state.newTitle.isNotBlank() && state.newBody.isNotBlank() && !state.isPosting,
                colors = ButtonDefaults.buttonColors(containerColor = Violet600),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                if (state.isPosting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = stringResource(if (state.editingId != null) R.string.ann_update else R.string.post),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // ── Scrollable content — Box(weight) bounds it so bottom bar always shows ──
        Box(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Author row
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Violet600),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = AppModule.currentAdmin?.name?.firstOrNull()?.uppercaseChar()?.toString() ?: "A",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = AppModule.currentAdmin?.name ?: stringResource(R.string.admin_user),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Violet600.copy(alpha = 0.10f))
                                .padding(horizontal = 10.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Groups,
                                contentDescription = null,
                                tint = Violet600,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = stringResource(state.target.labelRes),
                                color = Violet600,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                Spacer(Modifier.height(12.dp))

                // Title card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(Dimens.RadiusMd))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    if (state.newTitle.isEmpty()) {
                        Text(
                            text = stringResource(R.string.ann_title_placeholder),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    }
                    BasicTextField(
                        value = state.newTitle,
                        onValueChange = {
                            if (it.length <= 100) onEvent(
                                AnnouncementsEvent.TitleChanged(
                                    it
                                )
                            )
                        },
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(Violet600),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Description card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(Dimens.RadiusMd))
                        .background(MaterialTheme.colorScheme.surface)
                        .defaultMinSize(minHeight = 120.dp)
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    if (state.newBody.isEmpty()) {
                        Text(
                            text = stringResource(R.string.write_announcement),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    }
                    BasicTextField(
                        value = state.newBody,
                        onValueChange = {
                            if (it.length <= 500) onEvent(
                                AnnouncementsEvent.BodyChanged(
                                    it
                                )
                            )
                        },
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 21.sp
                        ),
                        cursorBrush = SolidColor(Violet600),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Attached image preview
                if (state.attachedImageUri != null) {
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(180.dp)
                            .clip(RoundedCornerShape(Dimens.RadiusMd))
                    ) {
                        AsyncImage(
                            model = state.attachedImageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .clickable { onEvent(AnnouncementsEvent.RemoveImage) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                // Category chips
                Text(
                    text = stringResource(R.string.ann_category),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 6.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AnnouncementCategory.entries.forEach { cat ->
                        val selected = state.newCategory == cat
                        val locked = state.categoryLocked
                        val isSelectable = !locked
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    when {
                                        selected && locked -> Violet600.copy(alpha = 0.5f)
                                        selected           -> Violet600
                                        else               -> MaterialTheme.colorScheme.surface
                                    }
                                )
                                .border(
                                    width = 0.5.dp,
                                    color = if (selected) Violet600 else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .then(if (isSelectable) Modifier.clickable { onEvent(AnnouncementsEvent.CategoryChanged(cat)) } else Modifier)
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = stringResource(cat.labelRes),
                                fontSize = 12.sp,
                                color = when {
                                    selected -> Color.White
                                    locked   -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                    else     -> MaterialTheme.colorScheme.onSurface
                                },
                                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
                            )
                        }
                    }
                }

                // Priority chips
                Text(
                    text = stringResource(R.string.ann_priority),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 6.dp)
                )
                Row(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AnnouncementPriority.entries.forEach { pri ->
                        val selected = state.newPriority == pri
                        val accentColor = priorityColor(pri)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selected) accentColor else MaterialTheme.colorScheme.surface)
                                .border(0.5.dp, accentColor, RoundedCornerShape(20.dp))
                                .clickable { onEvent(AnnouncementsEvent.PriorityChanged(pri)) }
                                .padding(horizontal = 18.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = stringResource(pri.labelRes),
                                fontSize = 12.sp,
                                color = if (selected) Color.White else accentColor,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }

                // Scheduled send chip
                if (state.scheduledAt != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(Dimens.RadiusMd))
                            .background(Violet600.copy(alpha = 0.08f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = null,
                            tint = Violet600,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = stringResource(R.string.scheduled_for, state.scheduledAt),
                            fontSize = 12.sp,
                            color = Violet600,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .clickable { onEvent(AnnouncementsEvent.ScheduledAtChanged(null)) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                null,
                                tint = Violet600,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                // Live preview card
                if (state.newTitle.isNotBlank() || state.newBody.isNotBlank()) {
                    Text(
                        text = stringResource(R.string.ann_preview),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp, top = 10.dp, bottom = 6.dp)
                    )
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(Dimens.RadiusMd))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(Dimens.SpaceLg),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        if (state.newTitle.isNotBlank()) {
                            Text(
                                text = state.newTitle,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        if (state.newBody.isNotBlank()) {
                            Text(
                                text = state.newBody,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(Modifier.height(2.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AnnChip(stringResource(state.newCategory.labelRes), Violet600)
                            AnnChip(
                                stringResource(state.newPriority.labelRes),
                                priorityColor(state.newPriority)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            } // inner Column
        } // Box(weight(1f))

        // ── Fixed bottom bar ─────────────────────────────────────────────────
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                IconButton(onClick = { imageLauncher.launch("image/*") }) {
                    Icon(
                        imageVector = Icons.Filled.Image,
                        contentDescription = stringResource(R.string.cd_attach_image),
                        tint = if (state.attachedImageUri != null) Violet600
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
                IconButton(onClick = { showTargetSheet = true }) {
                    Icon(
                        imageVector = Icons.Filled.Apartment,
                        contentDescription = stringResource(R.string.cd_target_building),
                        tint = if (state.target != AnnouncementTarget.ALL) Violet600
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
                IconButton(onClick = {
                    if (state.scheduledAt != null) onEvent(
                        AnnouncementsEvent.ScheduledAtChanged(
                            null
                        )
                    )
                    else { userPickedDate = false; pendingDateMillis = null; showDatePicker = true }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = stringResource(R.string.cd_schedule),
                        tint = if (state.scheduledAt != null) Violet600
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Text(
                text = "${state.newBody.length} / 500",
                fontSize = 12.sp,
                modifier = Modifier.padding(end = 8.dp),
                color = if (state.newBody.length > 450) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } // outer Column

    // ── Target audience bottom sheet ─────────────────────────────────────────
    if (showTargetSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTargetSheet = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            Text(
                text = stringResource(R.string.target_audience),
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                modifier = Modifier.padding(start = 20.dp, top = 4.dp, bottom = 12.dp)
            )
            AnnouncementTarget.entries.forEach { target ->
                val isSelected = state.target == target
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onEvent(AnnouncementsEvent.TargetChanged(target))
                            showTargetSheet = false
                        }
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (target == AnnouncementTarget.ALL) Icons.Filled.Groups
                            else Icons.Filled.Apartment,
                            contentDescription = null,
                            tint = if (isSelected) Violet600 else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(target.labelRes),
                            fontSize = 14.sp,
                            color = if (isSelected) Violet600 else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                    if (isSelected) {
                        Icon(
                            Icons.Filled.Check,
                            null,
                            tint = Violet600,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }

    // ── Date picker ──────────────────────────────────────────────────────────
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = null)
        LaunchedEffect(Unit) {
            // Wait for Material3 to finish all internal initialization before observing
            kotlinx.coroutines.delay(100)
            val baseline = datePickerState.selectedDateMillis
            snapshotFlow { datePickerState.selectedDateMillis }.collect { millis ->
                if (millis != baseline) userPickedDate = true
            }
        }
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (!userPickedDate) return@TextButton
                        pendingDateMillis = datePickerState.selectedDateMillis
                        showDatePicker = false
                        if (pendingDateMillis != null) showTimePicker = true
                    },
                    enabled = userPickedDate
                ) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.cancel)) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // ── Time picker ──────────────────────────────────────────────────────────
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(is24Hour = false)
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text(stringResource(R.string.select_time)) },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val cal = Calendar.getInstance().apply {
                        timeInMillis = pendingDateMillis ?: System.currentTimeMillis()
                        set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        set(Calendar.MINUTE, timePickerState.minute)
                    }
                    val formatted =
                        SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.US).format(cal.time)
                    onEvent(AnnouncementsEvent.ScheduledAtChanged(formatted))
                    showTimePicker = false
                }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

// ── Helpers ──────────────────────────────────────────────────────────────────

internal fun priorityColor(priority: AnnouncementPriority): Color = when (priority) {
    AnnouncementPriority.LOW -> Color(0xFF059669)
    AnnouncementPriority.MEDIUM -> Color(0xFFD97706)
    AnnouncementPriority.HIGH -> Color(0xFFDC2626)
}

@Composable
internal fun AnnChip(label: String, color: Color) {
    Text(
        text = label,
        fontSize = 10.sp,
        color = color,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Empty form")
@Composable
private fun PreviewCreateAnnouncementEmpty() {
    SyntrixorAdminTheme {
        CreateAnnouncementContent(
            state = AnnouncementsState(),
            onEvent = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Filled — shows preview card")
@Composable
private fun PreviewCreateAnnouncementFilled() {
    SyntrixorAdminTheme {
        CreateAnnouncementContent(
            state = AnnouncementsState(
                newTitle = "Pool closed this weekend",
                newBody = "The swimming pool will be closed on Friday and Saturday for routine maintenance and cleaning.",
                newCategory = AnnouncementCategory.MAINTENANCE,
                newPriority = AnnouncementPriority.MEDIUM
            ),
            onEvent = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark theme — Emergency")
@Composable
private fun PreviewCreateAnnouncementDark() {
    SyntrixorAdminTheme {
        CreateAnnouncementContent(
            state = AnnouncementsState(
                newTitle = "Emergency water shutdown",
                newBody = "Water supply will be cut off on July 10 from 8 AM to 2 PM due to pipe repair works.",
                newCategory = AnnouncementCategory.EMERGENCY,
                newPriority = AnnouncementPriority.HIGH
            ),
            onEvent = {},
            onBack = {}
        )
    }
}
