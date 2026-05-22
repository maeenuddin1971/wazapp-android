package com.maeen.mahfilhub.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response from the Maulana registration endpoint.
 * Returns auth token + role on success, same pattern as [LoginResponse].
 */
data class MaulanaSignupResponse(
    val token: String,
    val role: String,
    val message: String? = null,

    @SerializedName("maulana_id")
    val maulanaId: String? = null
)
