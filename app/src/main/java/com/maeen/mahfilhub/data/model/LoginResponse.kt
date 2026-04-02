package com.maeen.mahfilhub.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("role")
    val role: String,
    @SerializedName("token")
    val token: String
)

