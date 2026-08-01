package com.syntrixor.syntrixoradmin.ui.screens.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.syntrixor.syntrixoradmin.R
import com.syntrixor.syntrixoradmin.data.AppModule
import com.syntrixor.syntrixoradmin.utils.AuthPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AppModule.repository
    private val context = app.applicationContext

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    init {
        if (AuthPreferences.isRemembered(context)) {
            _state.update {
                it.copy(
                    email = AuthPreferences.getSavedEmail(context),
                    password = AuthPreferences.getSavedPassword(context),
                    rememberMe = true
                )
            }
        }
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> _state.update {
                it.copy(
                    email = event.email,
                    emailError = null
                )
            }

            is LoginEvent.PasswordChanged -> _state.update {
                it.copy(
                    password = event.password,
                    passwordError = null
                )
            }

            is LoginEvent.RememberMeChanged -> _state.update { it.copy(rememberMe = event.checked) }
            LoginEvent.Submit -> submit()
            LoginEvent.ClearError -> _state.update { it.copy(error = null) }
        }
    }

    private fun submit() {
        val email = _state.value.email.trim()
        val password = _state.value.password

        val emailError = when {
            email.isEmpty() -> context.getString(R.string.error_email_empty)
            !email.contains("@") -> context.getString(R.string.error_email_invalid)
            else -> null
        }
        val passwordError = when {
            password.isEmpty() -> context.getString(R.string.error_password_empty)
            password.length < 6 -> context.getString(R.string.error_password_short)
            else -> null
        }

        if (emailError != null || passwordError != null) {
            _state.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repo.login(email, password)
                .onSuccess { admin ->
                    AppModule.currentAdmin = admin
                    if (_state.value.rememberMe) {
                        AuthPreferences.saveCredentials(context, email, password)
                    } else {
                        AuthPreferences.clearCredentials(context)
                    }
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: context.getString(R.string.error_unknown)
                        )
                    }
                }
        }
    }
}
