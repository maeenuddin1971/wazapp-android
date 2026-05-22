package com.maeen.mahfilhub.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maeen.mahfilhub.data.model.MaulanaSignupRequest
import com.maeen.mahfilhub.data.model.MaulanaSignupResponse
import com.maeen.mahfilhub.data.repository.MaulanaRepository
import com.maeen.mahfilhub.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the multi-step Maulana Signup form.
 *
 * Holds all form field values, step tracking, validation errors,
 * and submission state so the Composable never manages these itself.
 */
data class MaulanaSignupUiState(
    // ── Step Tracking ────────────────────────────────────────────────
    val currentStep: Int = 0,
    val totalSteps: Int = 3,

    // ── Step 1: Account Info ─────────────────────────────────────────
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,

    // ── Step 2: Scholar Profile ──────────────────────────────────────
    val title: String = "",
    val specialization: String = "",
    val location: String = "",
    val experience: String = "",
    val qualification: String = "",

    // ── Step 3: Bio & Verification ───────────────────────────────────
    val bio: String = "",
    val referenceContact: String = "",
    val agreeTerms: Boolean = false,

    // ── Submission State ─────────────────────────────────────────────
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val signupResponse: MaulanaSignupResponse? = null,
    val successMessage: String? = null,

    // ── Field Validation Errors ──────────────────────────────────────
    val fullNameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val titleError: String? = null,
    val specializationError: String? = null,
    val locationError: String? = null,
    val experienceError: String? = null,
    val qualificationError: String? = null,
    val bioError: String? = null,
    val referenceContactError: String? = null
) {
    /** Whether the Submit button should be enabled on the final step. */
    val isSubmitEnabled: Boolean get() = agreeTerms && !isLoading
}

/**
 * ViewModel powering MaulanaSignupScreen.
 *
 * Follows the same StateFlow pattern established by [LoginViewModel]
 * and other ViewModels in the project. Manages multi-step navigation,
 * per-step validation, and signup submission.
 *
 * NOTE: Signup currently simulates a network call. Replace with a real
 * repository call once the backend API endpoint is available.
 */
class MaulanaSignupViewModel : ViewModel() {

    private val maulanaRepository = MaulanaRepository()

    private val _uiState = MutableStateFlow(MaulanaSignupUiState())
    val uiState: StateFlow<MaulanaSignupUiState> = _uiState.asStateFlow()

    // ══════════════════════════════════════════════════════════════════
    // Step 1: Account Info Field Updates
    // ══════════════════════════════════════════════════════════════════

    fun onFullNameChange(value: String) {
        _uiState.update { it.copy(fullName = value, fullNameError = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    fun onPhoneChange(value: String) {
        _uiState.update { it.copy(phone = value, phoneError = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    // ══════════════════════════════════════════════════════════════════
    // Step 2: Scholar Profile Field Updates
    // ══════════════════════════════════════════════════════════════════

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value, titleError = null) }
    }

    fun onSpecializationChange(value: String) {
        _uiState.update { it.copy(specialization = value, specializationError = null) }
    }

    fun onLocationChange(value: String) {
        _uiState.update { it.copy(location = value, locationError = null) }
    }

    fun onExperienceChange(value: String) {
        _uiState.update { it.copy(experience = value, experienceError = null) }
    }

    fun onQualificationChange(value: String) {
        _uiState.update { it.copy(qualification = value, qualificationError = null) }
    }

    // ══════════════════════════════════════════════════════════════════
    // Step 3: Bio & Verification Field Updates
    // ══════════════════════════════════════════════════════════════════

    fun onBioChange(value: String) {
        // Enforce 500-character limit
        if (value.length <= 500) {
            _uiState.update { it.copy(bio = value, bioError = null) }
        }
    }

    fun onReferenceContactChange(value: String) {
        _uiState.update { it.copy(referenceContact = value, referenceContactError = null) }
    }

    fun onAgreeTermsChange(value: Boolean) {
        _uiState.update { it.copy(agreeTerms = value) }
    }

    // ══════════════════════════════════════════════════════════════════
    // Step Navigation
    // ══════════════════════════════════════════════════════════════════

    /**
     * Validate current step and advance to the next one.
     * Returns `true` if validation passed and the step advanced.
     */
    fun goToNextStep(): Boolean {
        val state = _uiState.value
        if (!validateStep(state.currentStep)) return false

        if (state.currentStep < state.totalSteps - 1) {
            _uiState.update { it.copy(currentStep = it.currentStep + 1) }
        }
        return true
    }

    /**
     * Go back to the previous step. Returns `true` if there was a
     * previous step to go back to, `false` if already on step 0.
     */
    fun goToPreviousStep(): Boolean {
        val state = _uiState.value
        return if (state.currentStep > 0) {
            _uiState.update { it.copy(currentStep = it.currentStep - 1) }
            true
        } else {
            false
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // Validation
    // ══════════════════════════════════════════════════════════════════

    private fun validateStep(step: Int): Boolean {
        return when (step) {
            0 -> validateStep1()
            1 -> validateStep2()
            2 -> validateStep3()
            else -> true
        }
    }

    private fun validateStep1(): Boolean {
        val state = _uiState.value
        var hasError = false

        var fullNameErr: String? = null
        var emailErr: String? = null
        var phoneErr: String? = null
        var passwordErr: String? = null
        var confirmPasswordErr: String? = null

        if (state.fullName.isBlank()) {
            fullNameErr = "Please enter your full name"
            hasError = true
        }

        if (state.email.isBlank()) {
            emailErr = "Please enter your email"
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            emailErr = "Please enter a valid email"
            hasError = true
        }

        if (state.phone.isBlank()) {
            phoneErr = "Please enter your phone number"
            hasError = true
        }

        if (state.password.isBlank()) {
            passwordErr = "Please enter a password"
            hasError = true
        } else if (state.password.length < 6) {
            passwordErr = "Password must be at least 6 characters"
            hasError = true
        }

        if (state.confirmPassword.isBlank()) {
            confirmPasswordErr = "Please confirm your password"
            hasError = true
        } else if (state.password != state.confirmPassword) {
            confirmPasswordErr = "Passwords do not match"
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    fullNameError = fullNameErr,
                    emailError = emailErr,
                    phoneError = phoneErr,
                    passwordError = passwordErr,
                    confirmPasswordError = confirmPasswordErr
                )
            }
        }

        return !hasError
    }

    private fun validateStep2(): Boolean {
        val state = _uiState.value
        var hasError = false

        var titleErr: String? = null
        var specializationErr: String? = null
        var locationErr: String? = null
        var experienceErr: String? = null
        var qualificationErr: String? = null

        if (state.title.isBlank()) {
            titleErr = "Please enter your title"
            hasError = true
        }

        if (state.specialization.isBlank()) {
            specializationErr = "Please enter your specialization"
            hasError = true
        }

        if (state.location.isBlank()) {
            locationErr = "Please enter your location"
            hasError = true
        }

        if (state.experience.isBlank()) {
            experienceErr = "Please enter your years of experience"
            hasError = true
        }

        if (state.qualification.isBlank()) {
            qualificationErr = "Please enter your qualification"
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    titleError = titleErr,
                    specializationError = specializationErr,
                    locationError = locationErr,
                    experienceError = experienceErr,
                    qualificationError = qualificationErr
                )
            }
        }

        return !hasError
    }

    private fun validateStep3(): Boolean {
        val state = _uiState.value
        var hasError = false

        var bioErr: String? = null
        var referenceErr: String? = null

        if (state.bio.isBlank()) {
            bioErr = "Please write a short bio"
            hasError = true
        } else if (state.bio.length < 20) {
            bioErr = "Bio should be at least 20 characters"
            hasError = true
        }

        if (state.referenceContact.isBlank()) {
            referenceErr = "Please provide a reference contact"
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    bioError = bioErr,
                    referenceContactError = referenceErr
                )
            }
        }

        return !hasError
    }

    // ══════════════════════════════════════════════════════════════════
    // Signup Submission
    // ══════════════════════════════════════════════════════════════════

    /**
     * Validate the final step and submit the signup request.
     * Calls [onSuccess] when the signup is complete.
     *
     * NOTE: Replace the simulated delay with a real repository call
     * once the backend API endpoint is available.
     */
    fun submitSignup() {
        if (!validateStep3()) return

        val state = _uiState.value
        if (!state.agreeTerms) {
            _uiState.update { it.copy(errorMessage = "Please agree to the Terms of Service") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val request = MaulanaSignupRequest(
                fullName = state.fullName,
                email = state.email,
                phone = state.phone,
                password = state.password,
                title = state.title,
                specialization = state.specialization,
                location = state.location,
                experience = state.experience,
                qualification = state.qualification,
                bio = state.bio,
                referenceContact = state.referenceContact
            )

            when (val result = maulanaRepository.register(request)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = result.data.message ?: "Application submitted successfully!",
                            signupResponse = result.data
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

    // ══════════════════════════════════════════════════════════════════
    // State Clearing Helpers
    // ══════════════════════════════════════════════════════════════════

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearSuccess() {
        _uiState.update { it.copy(successMessage = null) }
    }

    fun clearFieldErrors() {
        _uiState.update {
            it.copy(
                fullNameError = null,
                emailError = null,
                phoneError = null,
                passwordError = null,
                confirmPasswordError = null,
                titleError = null,
                specializationError = null,
                locationError = null,
                experienceError = null,
                qualificationError = null,
                bioError = null,
                referenceContactError = null
            )
        }
    }

    fun resetState() {
        _uiState.value = MaulanaSignupUiState()
    }
}
