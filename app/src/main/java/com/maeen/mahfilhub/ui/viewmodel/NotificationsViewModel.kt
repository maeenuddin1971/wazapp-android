package com.maeen.mahfilhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.maeen.mahfilhub.data.model.NotificationItem
import com.maeen.mahfilhub.data.model.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * UI state for screens that consume notification data.
 */
data class NotificationsUiState(
    val notifications: List<NotificationItem> = emptyList(),
    val selectedFilter: String = "All",
    val filters: List<String> = listOf("All", "Events", "Reminders", "System"),
    val filteredNotifications: List<NotificationItem> = emptyList(),
    val unreadCount: Int = 0
)

/**
 * Shared ViewModel powering NotificationListScreen and
 * NotificationDetailScreen.
 *
 * Mirrors the iOS `NotificationsViewModel` for feature parity.
 * Follows the same StateFlow pattern as [EventsViewModel] and [MaulanaViewModel].
 */
class NotificationsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(notifications = seedNotifications) }
        recompute()
    }

    // ── Public Actions ───────────────────────────────────────────────────

    fun setFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
        recompute()
    }

    fun markAsRead(notificationId: Int) {
        _uiState.update { state ->
            val updated = state.notifications.map { n ->
                if (n.id == notificationId) n.copy(isRead = true) else n
            }
            state.copy(notifications = updated)
        }
        recompute()
    }

    fun markAllAsRead() {
        _uiState.update { state ->
            val updated = state.notifications.map { it.copy(isRead = true) }
            state.copy(notifications = updated)
        }
        recompute()
    }

    // ── Lookup Helpers ───────────────────────────────────────────────────

    fun notificationById(id: Int): NotificationItem? {
        return _uiState.value.notifications.find { it.id == id }
    }

    // ── Private ──────────────────────────────────────────────────────────

    private fun recompute() {
        _uiState.update { state ->
            val filtered = when (state.selectedFilter) {
                "Events" -> state.notifications.filter {
                    it.type == NotificationType.EVENT || it.type == NotificationType.MAULANA
                }
                "Reminders" -> state.notifications.filter {
                    it.type == NotificationType.REMINDER
                }
                "System" -> state.notifications.filter {
                    it.type == NotificationType.SYSTEM || it.type == NotificationType.COMMUNITY
                }
                else -> state.notifications
            }
            state.copy(
                filteredNotifications = filtered,
                unreadCount = state.notifications.count { !it.isRead }
            )
        }
    }

    // ── Seed Data (replace with API call later) ──────────────────────────

    companion object {
        private val seedNotifications = listOf(
            NotificationItem(
                id = 1,
                title = "New Event Added",
                message = "Friday Waz Mahfil by Maulana Abdul Karim has been scheduled at Dhaka Central Mosque. Don't miss this enlightening session!",
                time = "2 min ago",
                type = NotificationType.EVENT,
                relatedId = 1
            ),
            NotificationItem(
                id = 2,
                title = "Event Starting Soon",
                message = "Tafseer Al-Quran session by Maulana Tariq Jameel is starting in 30 minutes at Baitul Mukarram National Mosque.",
                time = "30 min ago",
                type = NotificationType.REMINDER,
                relatedId = 2
            ),
            NotificationItem(
                id = 3,
                title = "Maulana Hassan Ali",
                message = "Maulana Hassan Ali has been verified and joined the platform. Follow to get updates about upcoming events.",
                time = "1 hour ago",
                type = NotificationType.MAULANA,
                isRead = true,
                relatedId = 3
            ),
            NotificationItem(
                id = 4,
                title = "Seerah Conference Update",
                message = "The venue for the Seerah Conference has been updated to Chittagong Grand Masjid. Please check the event details for more info.",
                time = "2 hours ago",
                type = NotificationType.EVENT,
                isRead = true,
                relatedId = 3
            ),
            NotificationItem(
                id = 5,
                title = "Welcome to MahfilHub!",
                message = "Assalamu Alaikum! Welcome to MahfilHub. Explore events, follow your favorite scholars, and stay connected with the community.",
                time = "3 hours ago",
                type = NotificationType.SYSTEM,
                isRead = true
            ),
            NotificationItem(
                id = 6,
                title = "Community Milestone",
                message = "MahfilHub has reached 10,000 active users! JazakAllah Khair for being a part of this growing community.",
                time = "1 day ago",
                type = NotificationType.COMMUNITY,
                isRead = true
            ),
            NotificationItem(
                id = 7,
                title = "Reminder: Youth Islamic Seminar",
                message = "Don't forget the Youth Islamic Seminar tomorrow at 3:00 PM at Sylhet Central Eidgah. Set your reminder now!",
                time = "1 day ago",
                type = NotificationType.REMINDER,
                isRead = true,
                relatedId = 4
            ),
            NotificationItem(
                id = 8,
                title = "New Feature: Event Reminders",
                message = "You can now set reminders for upcoming events. Tap the bell icon on any event to get notified before it starts.",
                time = "2 days ago",
                type = NotificationType.SYSTEM,
                isRead = true
            )
        )
    }
}
