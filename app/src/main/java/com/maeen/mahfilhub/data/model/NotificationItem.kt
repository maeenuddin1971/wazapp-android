package com.maeen.mahfilhub.data.model

/**
 * Domain model for a notification in MahfilHub.
 * Used across NotificationListScreen and NotificationDetailScreen.
 *
 * NOTE: This replaces the inline data classes that were previously
 * defined inside NotificationListScreen.kt.
 */
data class NotificationItem(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val relatedId: Int? = null
)

enum class NotificationType {
    EVENT, MAULANA, SYSTEM, REMINDER, COMMUNITY
}
