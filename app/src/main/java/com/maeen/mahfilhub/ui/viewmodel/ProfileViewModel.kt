package com.maeen.mahfilhub.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maeen.mahfilhub.data.model.UserProfileResponse
import com.maeen.mahfilhub.data.repository.UserRepository
import com.maeen.mahfilhub.util.Resource
import com.maeen.mahfilhub.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the Profile screen.
 */
data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: UserProfileResponse? = null,
    val errorMessage: String? = null
) {
    val isLoaded: Boolean get() = profile != null
    val displayName: String get() = profile?.fullName ?: "Guest"
    val displayEmail: String get() = profile?.email ?: ""
    val displayInitial: String get() = profile?.initial ?: "?"
    val isMaolana: Boolean get() = profile?.isMaolana == true
    val isVerified: Boolean get() = profile?.isVerified == true
    val locationDisplay: String get() = profile?.locationDisplay ?: "Not set"
    val contactNumber: String get() = profile?.contactNumber ?: "Not set"
    val roleBadge: String
        get() = when (profile?.role) {
            "ISLAMIC_CLERIC" -> "Scholar"
            "MODERATOR" -> "Moderator"
            "ADMIN" -> "Admin"
            else -> "Member"
        }
}

/**
 * ViewModel for the Profile screen.
 * Fetches user profile from GET /user/profile on init,
 * passing the saved JWT token explicitly.
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        val token = SessionManager.getToken(getApplication())
        if (token.isNullOrBlank()) {
            _uiState.update {
                it.copy(isLoading = false, errorMessage = "Not logged in")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = userRepository.getProfile(token)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            profile = result.data
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                is Resource.Loading -> { /* handled by isLoading flag */ }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
