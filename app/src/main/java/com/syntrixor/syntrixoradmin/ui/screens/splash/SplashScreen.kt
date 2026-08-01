package com.syntrixor.syntrixoradmin.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.syntrixor.syntrixoradmin.R
import com.syntrixor.syntrixoradmin.data.AppModule
import com.syntrixor.syntrixoradmin.ui.theme.Navy
import com.syntrixor.syntrixoradmin.ui.theme.SyntrixorAdminPreviewTheme
import com.syntrixor.syntrixoradmin.ui.theme.Violet400
import com.syntrixor.syntrixoradmin.ui.theme.Violet600
import com.syntrixor.syntrixoradmin.ui.theme.White
import com.syntrixor.syntrixoradmin.utils.AuthPreferences
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(1400)

        if (AuthPreferences.isRemembered(context)) {
            val email    = AuthPreferences.getSavedEmail(context)
            val password = AuthPreferences.getSavedPassword(context)
            val result   = AppModule.repository.login(email, password)

            if (result.isSuccess) {
                onNavigateToHome()
            } else {
                AuthPreferences.clearCredentials(context)
                onNavigateToLogin()
            }
        } else {
            onNavigateToLogin()
        }
    }

    SplashContent()
}

// ── Stateless UI (previewable) ───────────────────────────────────────────────

@Composable
private fun SplashContent() {
    Box(
        modifier = Modifier.fillMaxSize().background(Navy),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Violet600.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text("S", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Violet600)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.brand_name),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                color = White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.login_title),
                fontSize = 13.sp,
                color = Violet400,
                letterSpacing = 1.sp
            )
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Light")
@Composable
private fun PreviewSplashLight() {
    SyntrixorAdminPreviewTheme(darkTheme = false) {
        SplashContent()
    }
}

@Preview(showBackground = true, name = "Dark")
@Composable
private fun PreviewSplashDark() {
    SyntrixorAdminPreviewTheme(darkTheme = true) {
        SplashContent()
    }
}
