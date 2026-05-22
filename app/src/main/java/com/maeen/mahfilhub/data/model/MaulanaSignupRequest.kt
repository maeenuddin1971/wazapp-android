package com.maeen.mahfilhub.data.model

import com.google.gson.annotations.SerializedName

/**
 * Request body for the Maulana registration endpoint.
 * Maps to all fields collected across the 3-step signup form.
 */
data class MaulanaSignupRequest(
    @SerializedName("full_name")
    val fullName: String,

    val email: String,
    val phone: String,
    val password: String,

    val title: String,
    val specialization: String,
    val location: String,
    val experience: String,
    val qualification: String,

    val bio: String,

    @SerializedName("reference_contact")
    val referenceContact: String
)
