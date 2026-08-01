package com.syntrixor.syntrixoradmin.ui.screens.admins

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.syntrixor.syntrixoradmin.R
import com.syntrixor.syntrixoradmin.data.model.Category
import com.syntrixor.syntrixoradmin.ui.components.SyntrixorTopBar
import com.syntrixor.syntrixoradmin.ui.theme.Dimens
import com.syntrixor.syntrixoradmin.ui.theme.Success
import com.syntrixor.syntrixoradmin.ui.theme.SyntrixorAdminPreviewTheme
import com.syntrixor.syntrixoradmin.ui.theme.Violet600

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminDetailScreen(
    adminId: String?,
    onSaveSuccess: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    vm: AdminDetailViewModel = viewModel(factory = AdminDetailViewModel.factory(adminId))
) {
    val state by vm.state.collectAsState()
    AdminDetailContent(
        state = state,
        onEvent = vm::onEvent,
        onSaveSuccess = onSaveSuccess,
        onBack = onBack,
        modifier = modifier
    )
}

// ── Stateless UI (previewable) ───────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AdminDetailContent(
    state: AdminDetailState,
    onEvent: (AdminDetailEvent) -> Unit,
    onSaveSuccess: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbar = remember { SnackbarHostState() }

    val savedMsg =
        stringResource(if (state.isCreateMode) R.string.admin_created else R.string.admin_saved)

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            snackbar.showSnackbar(savedMsg)
            onEvent(AdminDetailEvent.ClearSaveSuccess)
            onSaveSuccess()
        }
    }

    LaunchedEffect(state.error) {
        if (state.error != null) {
            snackbar.showSnackbar(state.error ?: "")
            onEvent(AdminDetailEvent.ClearError)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            SyntrixorTopBar(
                title = stringResource(if (state.isCreateMode) R.string.new_admin else R.string.admin_detail_title),
                onBack = onBack,
                actions = {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(Dimens.ProgressIndicatorSize)
                                .padding(end = Dimens.SpaceSm),
                            strokeWidth = Dimens.ProgressStrokeWidth,
                            color = Violet600
                        )
                    } else {
                        TextButton(onClick = { onEvent(AdminDetailEvent.Save) }) {
                            Text(
                                text = stringResource(if (state.isCreateMode) R.string.create_admin_btn else R.string.save_changes),
                                color = Violet600,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(Dimens.ScreenPaddingHorizontal),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceLg)
        ) {
            Spacer(Modifier.height(Dimens.SpaceSm))

            // ── Personal Information ────────────────────────────────────────
            SectionCard {
                AdminField(
                    value = state.name,
                    onValue = { onEvent(AdminDetailEvent.NameChanged(it)) },
                    label = stringResource(R.string.admin_name_hint),
                    error = state.nameError,
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words
                )
                Spacer(Modifier.height(Dimens.SpaceMd))
                AdminField(
                    value = state.email,
                    onValue = { onEvent(AdminDetailEvent.EmailChanged(it)) },
                    label = stringResource(R.string.email_hint),
                    error = state.emailError,
                    keyboardType = KeyboardType.Email,
                    readOnly = !state.isCreateMode,
                    ltr = true
                )
                Spacer(Modifier.height(Dimens.SpaceMd))
                AdminField(
                    value = state.phone,
                    onValue = { onEvent(AdminDetailEvent.PhoneChanged(it)) },
                    label = stringResource(R.string.admin_phone_hint),
                    keyboardType = KeyboardType.Phone,
                    ltr = true
                )
            }

            // ── Account Status ──────────────────────────────────────────────
            SectionHeader(stringResource(R.string.account_status))
            SectionCard {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatusChip(
                        label = stringResource(R.string.admin_active),
                        selected = state.isActive,
                        activeColor = Success,
                        onClick = { if (!state.isActive) onEvent(AdminDetailEvent.ToggleActive) },
                        modifier = Modifier.weight(1f)
                    )
                    StatusChip(
                        label = stringResource(R.string.admin_inactive),
                        selected = !state.isActive,
                        activeColor = MaterialTheme.colorScheme.error,
                        onClick = { if (state.isActive) onEvent(AdminDetailEvent.ToggleActive) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ── Assigned Categories ────────────────────────────────────────
            SectionHeader(stringResource(R.string.assigned_categories))
            SectionCard {
                Text(
                    text = stringResource(R.string.super_admin_note),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(Dimens.SpaceMd))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
                    verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSm)
                ) {
                    Category.entries.forEach { cat ->
                        val selected = cat in state.selectedCategories
                        CategoryToggleChip(
                            label = stringResource(cat.labelRes),
                            selected = selected,
                            color = cat.color,
                            onClick = { onEvent(AdminDetailEvent.ToggleCategory(cat)) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(Dimens.SpaceXl))
        }
    }
}

@Composable
private fun AdminField(
    value: String,
    onValue: (String) -> Unit,
    label: String,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    readOnly: Boolean = false,
    ltr: Boolean = false
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val resolvedStyle = if (ltr) {
        MaterialTheme.typography.bodyMedium.copy(
            textDirection = TextDirection.Ltr,
            textAlign = if (isRtl) TextAlign.Right else TextAlign.Left
        )
    } else {
        MaterialTheme.typography.bodyMedium
    }
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        label = { Text(label) },
        isError = error != null,
        supportingText = if (error != null) ({
            Text(error, color = MaterialTheme.colorScheme.error)
        }) else null,
        singleLine = true,
        readOnly = readOnly,
        modifier = Modifier.fillMaxWidth(),
        textStyle = resolvedStyle,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            capitalization = capitalization,
            imeAction = ImeAction.Next
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Violet600,
            focusedLabelColor = Violet600,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun SectionCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusMd))
            .background(MaterialTheme.colorScheme.surface)
            .padding(Dimens.SpaceLg)
    ) { content() }
}

@Composable
private fun StatusChip(
    label: String,
    selected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.RadiusSm))
            .background(if (selected) activeColor.copy(alpha = 0.15f) else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (selected) activeColor else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(Dimens.RadiusSm)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CategoryToggleChip(
    label: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    val bg = if (selected) color else Color.Transparent
    val border = if (selected) color else MaterialTheme.colorScheme.outline
    val text = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    Text(
        text = label,
        fontSize = 12.sp,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        color = text,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    )
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Light — Edit mode")
@Composable
private fun PreviewAdminDetailEditLight() {
    SyntrixorAdminPreviewTheme(darkTheme = false) {
        AdminDetailContent(
            state = AdminDetailState(
                isCreateMode = false,
                name = "Plumbing Admin",
                email = "plumbing@syntrixor.com",
                phone = "+20 100 000 0001",
                selectedCategories = listOf(Category.PLUMBING, Category.CARPENTRY, Category.PAINTING),
                isActive = true
            ),
            onEvent = {},
            onSaveSuccess = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark — Edit mode")
@Composable
private fun PreviewAdminDetailEditDark() {
    SyntrixorAdminPreviewTheme(darkTheme = true) {
        AdminDetailContent(
            state = AdminDetailState(
                isCreateMode = false,
                name = "Plumbing Admin",
                email = "plumbing@syntrixor.com",
                phone = "+20 100 000 0001",
                selectedCategories = listOf(Category.PLUMBING, Category.CARPENTRY, Category.PAINTING),
                isActive = true
            ),
            onEvent = {},
            onSaveSuccess = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Light — Create mode")
@Composable
private fun PreviewAdminDetailCreateLight() {
    SyntrixorAdminPreviewTheme(darkTheme = false) {
        AdminDetailContent(
            state = AdminDetailState(isCreateMode = true),
            onEvent = {},
            onSaveSuccess = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark — Create mode")
@Composable
private fun PreviewAdminDetailCreateDark() {
    SyntrixorAdminPreviewTheme(darkTheme = true) {
        AdminDetailContent(
            state = AdminDetailState(isCreateMode = true),
            onEvent = {},
            onSaveSuccess = {},
            onBack = {}
        )
    }
}
