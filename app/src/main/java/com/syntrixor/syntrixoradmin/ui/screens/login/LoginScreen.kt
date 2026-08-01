package com.syntrixor.syntrixoradmin.ui.screens.login

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.syntrixor.syntrixoradmin.R
import com.syntrixor.syntrixoradmin.ui.theme.Dimens
import com.syntrixor.syntrixoradmin.ui.theme.ForceStatusBarIcons
import com.syntrixor.syntrixoradmin.ui.theme.SyntrixorAdminPreviewTheme
import com.syntrixor.syntrixoradmin.ui.theme.Violet600
import com.syntrixor.syntrixoradmin.ui.theme.White
import com.syntrixor.syntrixoradmin.utils.LocaleHelper
import com.syntrixor.syntrixoradmin.utils.LocalIsTablet
import com.syntrixor.syntrixoradmin.utils.ThemeManager

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    vm: LoginViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(state.isSuccess) { if (state.isSuccess) onLoginSuccess() }

    LoginContent(state = state, onEvent = vm::onEvent)
}

// ── Stateless UI (previewable) ───────────────────────────────────────────────

@Composable
private fun LoginContent(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var passwordVisible by remember { mutableStateOf(false) }
    val currentLang = LocaleHelper.getLanguage(context)
    val isTablet = LocalIsTablet.current
    val isDark by ThemeManager.isDarkMode

    // In dark mode the background is deep Navy — force white status icons.
    // In light mode the background is a light surface — use the normal dark icons.
    ForceStatusBarIcons(useLightIcons = isDark)

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            onEvent(LoginEvent.ClearError)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        if (isTablet) {
            TabletLoginLayout(
                state = state,
                onEvent = onEvent,
                focusManager = focusManager,
                passwordVisible = passwordVisible,
                onTogglePasswordVisible = { passwordVisible = !passwordVisible }
            )
        } else {
            PhoneLoginLayout(
                state = state,
                onEvent = onEvent,
                focusManager = focusManager,
                passwordVisible = passwordVisible,
                onTogglePasswordVisible = { passwordVisible = !passwordVisible }
            )
        }

        // Snackbar — pinned bottom
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) { data ->
            Snackbar(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                snackbarData = data
            )
        }

        // ── Language toggle — declared last so it sits on top of everything ──
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = Dimens.SpaceLg, end = Dimens.SpaceLg),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceXs)
        ) {
            LanguageChip("EN", currentLang == "en") {
                if (currentLang != "en") {
                    LocaleHelper.setLanguage(context, "en")
                    (context as? Activity)?.recreate()
                }
            }
            LanguageChip("AR", currentLang == "ar") {
                if (currentLang != "ar") {
                    LocaleHelper.setLanguage(context, "ar")
                    (context as? Activity)?.recreate()
                }
            }
        }
    }
}

// ── Phone layout — single centered column ───────────────────────────────────

@Composable
private fun PhoneLoginLayout(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit,
    focusManager: FocusManager,
    passwordVisible: Boolean,
    onTogglePasswordVisible: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = Dimens.ScreenPaddingHorizontal),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LoginBranding(logoSize = 80.dp, titleSize = 22.sp, subtitleSize = 13.sp)
        Spacer(Modifier.height(36.dp))
        LoginFormFields(state, onEvent, focusManager, passwordVisible, onTogglePasswordVisible)
    }
}

// ── Tablet landscape layout — branding panel + centered form panel ──────────

@Composable
private fun TabletLoginLayout(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit,
    focusManager: FocusManager,
    passwordVisible: Boolean,
    onTogglePasswordVisible: () -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        // Branding panel
        Column(
            modifier = Modifier
                .weight(0.42f)
                .fillMaxHeight()
                .background(Violet600.copy(alpha = 0.10f))
                .padding(Dimens.Space2Xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LoginBranding(logoSize = 108.dp, titleSize = 30.sp, subtitleSize = 16.sp)
        }

        // Form panel
        Box(
            modifier = Modifier
                .weight(0.58f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 420.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = Dimens.SpaceXl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginFormFields(
                    state,
                    onEvent,
                    focusManager,
                    passwordVisible,
                    onTogglePasswordVisible
                )
            }
        }
    }
}

@Composable
private fun LoginBranding(
    logoSize: androidx.compose.ui.unit.Dp,
    titleSize: androidx.compose.ui.unit.TextUnit,
    subtitleSize: androidx.compose.ui.unit.TextUnit
) {
    Box(
        modifier = Modifier
            .size(logoSize)
            .clip(RoundedCornerShape(20.dp))
            .background(Violet600.copy(alpha = 0.18f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "S",
            fontSize = logoSize.value.sp * 0.55f,
            fontWeight = FontWeight.Bold,
            color = Violet600
        )
    }
    Spacer(Modifier.height(20.dp))
    Text(
        text = stringResource(R.string.login_title),
        fontSize = titleSize,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(Modifier.height(6.dp))
    Text(
        text = stringResource(R.string.login_subtitle),
        fontSize = subtitleSize,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
    )
}

// ── Shared form fields ───────────────────────────────────────────────────────

@Composable
private fun LoginFormFields(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit,
    focusManager: FocusManager,
    passwordVisible: Boolean,
    onTogglePasswordVisible: () -> Unit
) {
    // Email
    OutlinedTextField(
        value = state.email,
        onValueChange = { onEvent(LoginEvent.EmailChanged(it)) },
        label = { Text(stringResource(R.string.email_hint)) },
        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
        isError = state.emailError != null,
        supportingText = state.emailError?.let { msg -> { Text(msg) } },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = fieldColors()
    )

    Spacer(Modifier.height(Dimens.SpaceMd))

    // Password
    OutlinedTextField(
        value = state.password,
        onValueChange = { onEvent(LoginEvent.PasswordChanged(it)) },
        label = { Text(stringResource(R.string.password_hint)) },
        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = onTogglePasswordVisible) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff
                    else Icons.Filled.Visibility,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None
        else PasswordVisualTransformation(),
        isError = state.passwordError != null,
        supportingText = state.passwordError?.let { msg -> { Text(msg) } },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
            onEvent(LoginEvent.Submit)
        }),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = fieldColors()
    )

    Spacer(Modifier.height(Dimens.SpaceMd))

    // Remember Me
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEvent(LoginEvent.RememberMeChanged(!state.rememberMe)) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = if (state.rememberMe) Icons.Filled.CheckBox
            else Icons.Filled.CheckBoxOutlineBlank,
            contentDescription = null,
            tint = if (state.rememberMe) Violet600 else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.size(Dimens.SpaceSm))
        Text(
            text = stringResource(R.string.remember_me),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            fontSize = 14.sp
        )
    }

    Spacer(Modifier.height(Dimens.SpaceXl))

    // Sign In button
    Button(
        onClick = {
            focusManager.clearFocus()
            onEvent(LoginEvent.Submit)
        },
        enabled = !state.isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.ButtonHeight),
        shape = RoundedCornerShape(Dimens.RadiusMd),
        colors = ButtonDefaults.buttonColors(containerColor = Violet600)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(Dimens.ProgressIndicatorSize),
                strokeWidth = Dimens.ProgressStrokeWidth,
                color = White
            )
        } else {
            Text(
                text = stringResource(R.string.sign_in),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = White
            )
        }
    }
}

@Composable
private fun LanguageChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) Violet600 else Color.Transparent
    val border = if (selected) Violet600 else MaterialTheme.colorScheme.outline
    val text = if (selected) White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = text)
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Violet600,
    focusedLabelColor = Violet600,
    focusedLeadingIconColor = Violet600,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedTextColor = MaterialTheme.colorScheme.onBackground,
    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
    errorBorderColor = MaterialTheme.colorScheme.error,
    cursorColor = Violet600
)

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Light")
@Composable
private fun PreviewLoginLight() {
    SyntrixorAdminPreviewTheme(darkTheme = false) {
        LoginContent(
            state = LoginState(
                email = "admin@syntrixor.com",
                password = "admin123",
                rememberMe = true
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark")
@Composable
private fun PreviewLoginDark() {
    SyntrixorAdminPreviewTheme(darkTheme = true) {
        LoginContent(
            state = LoginState(
                email = "plumbing@syntrixor.com",
                password = "",
                emailError = null,
                passwordError = "Password must be at least 6 characters"
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 1280, heightDp = 800, name = "Tablet landscape")
@Composable
private fun PreviewLoginTablet() {
    SyntrixorAdminPreviewTheme(darkTheme = false) {
        CompositionLocalProvider(LocalIsTablet provides true) {
            LoginContent(
                state = LoginState(
                    email = "admin@syntrixor.com",
                    password = "admin123",
                    rememberMe = true
                ),
                onEvent = {}
            )
        }
    }
}
