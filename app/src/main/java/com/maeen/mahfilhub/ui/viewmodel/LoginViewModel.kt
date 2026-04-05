package com.maeen.mahfilhub.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maeen.mahfilhub.data.model.LoginResponse
import com.maeen.mahfilhub.data.repository.AuthRepository
import com.maeen.mahfilhub.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val loginResponse: LoginResponse? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null
)

class LoginViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        // Clear previous field errors
        var hasError = false
        var emailErr: String? = null
        var passwordErr: String? = null

        if (email.isBlank()) {
            emailErr = "Please enter your email"
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailErr = "Please enter a valid email"
            hasError = true
        }

        if (password.isBlank()) {
            passwordErr = "Please enter your password"
            hasError = true
        } else if (password.length < 6) {
            passwordErr = "Password must be at least 6 characters"
            hasError = true
        }

        if (hasError) {
            _uiState.value = LoginUiState(emailError = emailErr, passwordError = passwordErr)
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            when (val result = repository.login(email, password)) {
                is Resource.Success -> {
                    _uiState.value = LoginUiState(
                        loginResponse = result.data,
                        successMessage = "Login successful!"
                    )
                }
                is Resource.Error -> {
                    _uiState.value = LoginUiState(errorMessage = result.message)
                }
                is Resource.Loading -> {
                    // Already handled above
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    fun clearFieldErrors() {
        _uiState.value = _uiState.value.copy(emailError = null, passwordError = null)
    }

    fun resetState() {
        _uiState.value = LoginUiState()
    }
}

