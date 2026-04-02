package com.maeen.mahfilhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maeen.mahfilhub.data.model.LoginResponse
import com.maeen.mahfilhub.data.repository.AuthRepository
import com.maeen.mahfilhub.data.repository.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val loginResponse: LoginResponse? = null,
    val errorMessage: String? = null
)

class LoginViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        // Basic client-side validation
        if (email.isBlank()) {
            _uiState.value = LoginUiState(errorMessage = "Please enter your email")
            return
        }
        if (password.isBlank()) {
            _uiState.value = LoginUiState(errorMessage = "Please enter your password")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            when (val result = repository.login(email, password)) {
                is Resource.Success -> {
                    _uiState.value = LoginUiState(loginResponse = result.data)
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

    fun resetState() {
        _uiState.value = LoginUiState()
    }
}

