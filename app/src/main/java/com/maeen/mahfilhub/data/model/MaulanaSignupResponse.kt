package com.maeen.mahfilhub.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response from the Maulana creation endpoint: POST /maolana/create.
 *
 * The backend may return the created Maulana object or a simple
 * status message. This model accommodates both patterns.
 */
data class MaulanaSignupResponse(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("fullName")
    val fullName: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("designation")
    val designation: String? = null,

    @SerializedName("isActive")
    val isActive: Boolean? = null,

    @SerializedName("isVerified")
    val isVerified: Boolean? = null,

    @SerializedName("message")
    val message: String? = null
)
