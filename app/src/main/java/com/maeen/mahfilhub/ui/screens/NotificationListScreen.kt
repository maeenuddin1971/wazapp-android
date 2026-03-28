package com.maeen.mahfilhub.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maeen.mahfilhub.ui.theme.*

// ══════════════════════════════════════════════════════════════════════════
// Sample Data
// ══════════════════════════════════════════════════════════════════════════

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

internal val sampleNotifications = listOf(
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

// ══════════════════════════════════════════════════════════════════════════
// Notification List Screen
// ══════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationListScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onNotificationClick: (Int) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Events", "Reminders", "System")

    val filteredNotifications = remember(selectedFilter) {
        when (selectedFilter) {
            "Events" -> sampleNotifications.filter { it.type == NotificationType.EVENT || it.type == NotificationType.MAULANA }
            "Reminders" -> sampleNotifications.filter { it.type == NotificationType.REMINDER }
            "System" -> sampleNotifications.filter { it.type == NotificationType.SYSTEM || it.type == NotificationType.COMMUNITY }
            else -> sampleNotifications
        }
    }

    val unreadCount = sampleNotifications.count { !it.isRead }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Hero Header ─────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PrimaryTeal, PrimaryTealDark)
                    )
                )
        ) {
            // Decorative circles
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = 100f,
                    center = Offset(size.width * 0.9f, size.height * 0.2f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.04f),
                    radius = 70f,
                    center = Offset(size.width * 0.1f, size.height * 0.9f)
                )
            }

            Column(
                modifier = Modifier.padding(
                    top = Spacing.extraLarge,
                    bottom = Spacing.medium,
                    start = Spacing.medium,
                    end = Spacing.medium
                )
            ) {
                // Top bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Mark all read button
                    TextButton(onClick = { }) {
                        Text(
                            text = "Mark all read",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.medium))

                // Title with badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Notifications",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (unreadCount > 0) {
                        Spacer(modifier = Modifier.width(Spacing.small))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AccentOrange
                        ) {
                            Text(
                                text = "$unreadCount new",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Stay updated with events & community",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(Spacing.medium))

                // Filter chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    filters.forEach { filter ->
                        val isSelected = filter == selectedFilter
                        Surface(
                            onClick = { selectedFilter = filter },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = filter,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) PrimaryTealDark else Color.White
                            )
                        }
                    }
                }
            }
        }

        // ── Notification list ──────────────────────────────────────────
        if (filteredNotifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacing.extraLarge),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = null,
                        tint = PrimaryTeal.copy(alpha = 0.3f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(Spacing.medium))
                    Text(
                        text = "No notifications",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = Spacing.medium,
                    bottom = Spacing.medium
                )
            ) {
                items(filteredNotifications, key = { it.id }) { notification ->
                    NotificationRow(
                        notification = notification,
                        onClick = { onNotificationClick(notification.id) }
                    )
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Notification Row
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun NotificationRow(
    notification: NotificationItem,
    onClick: () -> Unit
) {
    val iconInfo = notificationTypeIcon(notification.type)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = if (!notification.isRead)
            PrimaryTeal.copy(alpha = 0.06f)
        else
            MaterialTheme.colorScheme.surface,
        shadowElevation = if (!notification.isRead) 2.dp else 0.5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.medium),
            verticalAlignment = Alignment.Top
        ) {
            // Type icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconInfo.second.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconInfo.first,
                    contentDescription = null,
                    tint = iconInfo.second,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (!notification.isRead) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(PrimaryTeal)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = notification.time,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Helpers
// ══════════════════════════════════════════════════════════════════════════

private fun notificationTypeIcon(type: NotificationType): Pair<ImageVector, Color> {
    return when (type) {
        NotificationType.EVENT -> Icons.Filled.DateRange to PrimaryTeal
        NotificationType.MAULANA -> Icons.Filled.Person to InfoBlue
        NotificationType.SYSTEM -> Icons.Filled.Info to AccentOrange
        NotificationType.REMINDER -> Icons.Filled.Notifications to SecondaryGreen
        NotificationType.COMMUNITY -> Icons.Filled.Favorite to ErrorRed
    }
}

internal fun findNotificationById(notificationId: Int): NotificationItem? {
    return sampleNotifications.find { it.id == notificationId }
}
