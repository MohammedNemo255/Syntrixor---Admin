package com.syntrixor.syntrixoradmin.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.syntrixor.syntrixoradmin.R
import com.syntrixor.syntrixoradmin.data.AppModule
import com.syntrixor.syntrixoradmin.data.model.Admin
import com.syntrixor.syntrixoradmin.ui.components.SyntrixorTopBar
import com.syntrixor.syntrixoradmin.ui.theme.Dimens
import com.syntrixor.syntrixoradmin.ui.theme.SyntrixorAdminPreviewTheme
import com.syntrixor.syntrixoradmin.ui.theme.Violet600
import com.syntrixor.syntrixoradmin.utils.FeatureFlags
import com.syntrixor.syntrixoradmin.utils.LocaleHelper
import com.syntrixor.syntrixoradmin.utils.ThemeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onLogout: () -> Unit,
    onNavigateToAdmins: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val isDark by ThemeManager.isDarkMode
    val isSuperAdmin = AppModule.currentAdmin?.assignedCategories?.isEmpty() == true

    ProfileContent(
        modifier = modifier,
        admin = AppModule.currentAdmin,
        isDark = isDark,
        isArabic = LocaleHelper.getLanguage(context) == "ar",
        isSuperAdmin = isSuperAdmin,
        onBack = onBack,
        onNavigateToAdmins = onNavigateToAdmins,
        onLogout = onLogout,
        onToggleDark = { ThemeManager.toggle(context) },
        onToggleArabic = { on ->
            LocaleHelper.setLanguage(context, if (on) "ar" else "en")
            (context as? android.app.Activity)?.recreate()
        }
    )
}

// ── Stateless UI (previewable) ───────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    modifier: Modifier = Modifier,
    admin: Admin?,
    isDark: Boolean,
    isArabic: Boolean,
    isSuperAdmin: Boolean,
    onBack: (() -> Unit)?,
    onNavigateToAdmins: (() -> Unit)?,
    onLogout: () -> Unit,
    onToggleDark: () -> Unit,
    onToggleArabic: (Boolean) -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SyntrixorTopBar(
                title = stringResource(R.string.profile_title),
                onBack = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Dimens.ScreenPaddingHorizontal),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(Dimens.SpaceXl))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Violet600),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = admin?.name?.firstOrNull()?.uppercaseChar()?.toString() ?: "A",
                    fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.White
                )
            }
            Spacer(Modifier.height(Dimens.SpaceMd))
            Text(
                admin?.name ?: stringResource(R.string.admin_user),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                admin?.role ?: stringResource(R.string.admin_role),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!admin?.email.isNullOrBlank()) {
                Text(
                    admin.email,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(Dimens.SpaceXl))

            SettingsGroup {
                SettingsToggleRow(
                    label = stringResource(R.string.dark_mode),
                    checked = isDark,
                    onToggle = { onToggleDark() }
                )
                SettingsDivider()
                SettingsToggleRow(
                    label = stringResource(R.string.arabic_language),
                    checked = isArabic,
                    onToggle = onToggleArabic
                )
            }

            if (isSuperAdmin && FeatureFlags.ADMIN_MANAGEMENT) {
                Spacer(Modifier.height(Dimens.SpaceLg))
                SettingsGroup {
                    SettingsNavRow(
                        label = stringResource(R.string.manage_admins),
                        onClick = { onNavigateToAdmins?.invoke() }
                    )
                }
            }

            Spacer(Modifier.height(Dimens.SpaceLg))

            SettingsGroup {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(R.string.version),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        stringResource(R.string.app_version),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(Dimens.SpaceXl))

            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.ButtonHeight),
                shape = RoundedCornerShape(Dimens.RadiusMd),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(
                    stringResource(R.string.logout),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        } // Box

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text(stringResource(R.string.logout)) },
                text = { Text(stringResource(R.string.logout_confirm)) },
                confirmButton = {
                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(stringResource(R.string.logout), color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showLogoutDialog = false
                    }) { Text(stringResource(R.string.cancel)) }
                }
            )
        }
    }
}

@Composable
private fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusMd))
            .background(MaterialTheme.colorScheme.surface)
            .padding(Dimens.SpaceLg),
        content = content
    )
}

@Composable
private fun SettingsToggleRow(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Violet600
            )
        )
    }
}

@Composable
private fun SettingsNavRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsDivider() {
    androidx.compose.material3.HorizontalDivider(
        modifier = Modifier.padding(vertical = Dimens.SpaceSm),
        color = MaterialTheme.colorScheme.outline
    )
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Light")
@Composable
private fun PreviewProfileLight() {
    SyntrixorAdminPreviewTheme(darkTheme = false) {
        ProfileContent(
            admin = Admin(
                id = "admin1",
                name = "Nadia Mostafa",
                email = "admin@syntrixor.com",
                phone = "+20 100 000 0000",
                role = "System Administrator",
                compoundName = "Syntrixor Heights",
                assignedCategories = emptyList()
            ),
            isDark = false,
            isArabic = false,
            isSuperAdmin = true,
            onBack = {},
            onNavigateToAdmins = {},
            onLogout = {},
            onToggleDark = {},
            onToggleArabic = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark")
@Composable
private fun PreviewProfileDark() {
    SyntrixorAdminPreviewTheme(darkTheme = true) {
        ProfileContent(
            admin = Admin(
                id = "admin2",
                name = "Plumbing Admin",
                email = "plumbing@syntrixor.com",
                phone = "+20 100 000 0001",
                role = "Category Administrator",
                compoundName = "Syntrixor Heights",
                assignedCategories = listOf(
                    com.syntrixor.syntrixoradmin.data.model.Category.PLUMBING,
                    com.syntrixor.syntrixoradmin.data.model.Category.CARPENTRY
                )
            ),
            isDark = true,
            isArabic = false,
            isSuperAdmin = false,
            onBack = {},
            onNavigateToAdmins = null,
            onLogout = {},
            onToggleDark = {},
            onToggleArabic = {}
        )
    }
}
