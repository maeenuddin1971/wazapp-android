package com.maeen.mahfilhub.data.model

/**
 * Domain model for a saved/bookmarked event in MahfilHub.
 * Used by SavedEventsScreen and SavedEventsViewModel.
 *
 * NOTE: This replaces the inline `SavedEventItem` data class that was
 * previously defined inside SavedEventsScreen.kt.
 */
data class SavedEventItem(
    val id: Int,
    val title: String,
    val maulana: String,
    val location: String,
    val date: String,
    val time: String,
    val savedDate: String,
    val isUpcoming: Boolean = true,
    val isLive: Boolean = false,
    val attendees: Int = 0,
    val category: String = "Upcoming"
)
