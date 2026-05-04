package com.maeen.mahfilhub.data.model

/**
 * Domain model for a followed scholar in MahfilHub.
 * Used by FollowingScreen and FollowingViewModel.
 */
data class FollowedScholar(
    val id: Int,
    val name: String,
    val title: String,
    val location: String,
    val followedSince: String,
    val upcomingEvents: Int = 0,
    val totalEvents: Int = 0,
    val isVerified: Boolean = false
) {
    val initial: String get() = name.first().toString()
}
