package com.maeen.mahfilhub.data.model

/**
 * Domain model for an event in MahfilHub.
 * Used across HomeScreen, EventsScreen, and EventDetailScreen.
 *
 * NOTE: This replaces the inline `EventItem` data class that was previously
 * defined inside EventsScreen.kt. Moving it here allows the ViewModel and
 * multiple screens to share the same type without circular dependencies.
 */
data class EventItem(
    val id: Int,
    val title: String,
    val maulana: String,
    val location: String,
    val date: String,
    val time: String,
    val isLive: Boolean = false,
    val isFeatured: Boolean = false,
    val attendees: Int = 0,
    val category: String = "All"
)
