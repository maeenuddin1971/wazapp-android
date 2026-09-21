package com.maeen.mahfilhub.data.model

/**
 * Domain model for a Maulana (Islamic scholar) in MahfilHub.
 * Used across HomeScreen, MaulanaScreen, and MaulanaDetailScreen.
 *
 * Mapped from [MaulanaApiItem] in the repository layer.
 */
data class MaulanaItem(
    val id: String,
    val name: String,
    val title: String,
    val specialization: String,
    val location: String,
    val totalEvents: Int,
    val upcomingEvents: Int,
    val followers: Int,
    val rating: Float,
    val isVerified: Boolean = false,
    val isFollowing: Boolean = false,
    val category: String = "All",
    val contactNumber: String? = null,
    val uniqueIdentifier: String? = null
)
