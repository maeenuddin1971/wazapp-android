package com.maeen.mahfilhub.data.model

import com.google.gson.annotations.SerializedName

/**
 * Paginated response wrapper for the maolana/list API.
 */
data class MaulanaListResponse(
    val data: MaulanaPageData,
    val message: String,
    val status: Int,
    val timestamp: String? = null,
    val path: String? = null
)

/**
 * Page data containing the list of maolana items and pagination info.
 */
data class MaulanaPageData(
    val content: List<MaulanaApiItem>,
    val page: Int = 0,
    val size: Int = 10,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    @SerializedName("first")
    val isFirst: Boolean = true,
    @SerializedName("last")
    val isLast: Boolean = true,
    @SerializedName("empty")
    val isEmpty: Boolean = false
)

/**
 * Single maolana item from the API response.
 */
data class MaulanaApiItem(
    val id: String,
    val fullName: String,
    val uniqueIdentifier: String? = null,
    val contactNumber: String? = null,
    val specializationField: String? = null,
    @SerializedName("isActive")
    val isActive: Boolean = true,
    val createdAt: String? = null
)
