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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.syntrixor.syntrixoradmin.ui.theme.Navy
import com.syntrixor.syntrixoradmin.ui.theme.SyntrixorAdminPreviewTheme
import com.syntrixor.syntrixoradmin.ui.theme.Violet400
import com.syntrixor.syntrixoradmin.ui.theme.Violet600
import com.syntrixor.syntrixoradmin.ui.theme.White
import com.syntrixor.syntrixoradmin.utils.LocaleHelper

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

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            onEvent(LoginEvent.ClearError)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy)
            .statusBarsPadding()
    ) {

        // ── Main content — vertically centered ────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = Dimens.ScreenPaddingHorizontal),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo mark
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Violet600.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text("S", fontSize = 44.sp, fontWeight = FontWeight.Bold, color = Violet600)
            }

            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.login_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.login_subtitle),
                fontSize = 13.sp,
                color = Violet400
            )
            Spacer(Modifier.height(36.dp))

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
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff
                            else Icons.Filled.Visibility,
                            contentDescription = null,
                            tint = White.copy(alpha = 0.7f)
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
                    tint = if (state.rememberMe) Violet400 else White.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.size(Dimens.SpaceSm))
                Text(
                    text = stringResource(R.string.remember_me),
                    color = White.copy(alpha = 0.8f),
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

        // ── Language toggle — declared last so it sits on top of the scroll column ──
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

@Composable
private fun LanguageChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) Violet600 else Color.Transparent
    val border = if (selected) Violet600 else White.copy(alpha = 0.35f)
    val text = if (selected) White else White.copy(alpha = 0.6f)

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
    unfocusedBorderColor = White.copy(alpha = 0.3f),
    unfocusedLabelColor = White.copy(alpha = 0.6f),
    unfocusedLeadingIconColor = White.copy(alpha = 0.5f),
    focusedTextColor = White,
    unfocusedTextColor = White,
    errorBorderColor = Color(0xFFDC2626),
    cursorColor = Violet400
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
