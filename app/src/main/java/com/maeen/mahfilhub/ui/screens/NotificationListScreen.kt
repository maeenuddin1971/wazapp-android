package com.maeen.mahfilhub.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maeen.mahfilhub.ui.theme.*
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.delay

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
// Notification List Screen — Collapsing Header
// ══════════════════════════════════════════════════════════════════════════

private val ExpandedHeaderHeight = 310.dp
private val ToolbarHeight = 56.dp   // collapsed toolbar area (below status bar)

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

    // ── Collapsing header state ────────────────────────────────────────
    val density = LocalDensity.current
    val statusBarPx = WindowInsets.statusBars.getTop(density).toFloat()
    val toolbarPx = with(density) { ToolbarHeight.toPx() }
    val collapsedPx = statusBarPx + toolbarPx            // status bar + toolbar
    val expandedPx = with(density) { ExpandedHeaderHeight.toPx() }
    val maxOffsetPx = expandedPx - collapsedPx

    var headerOffset by remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = (headerOffset - delta).coerceIn(0f, maxOffsetPx)
                val consumed = headerOffset - newOffset
                headerOffset = newOffset
                return Offset(0f, consumed)
            }
        }
    }

    // Progress: 0f = fully expanded, 1f = fully collapsed
    val collapseProgress = (headerOffset / maxOffsetPx).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .nestedScroll(nestedScrollConnection)
    ) {
        // ── Notification list (below header) ──────────────────────────
        val headerHeightDp = with(density) { (expandedPx - headerOffset).toDp() }

        if (filteredNotifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = headerHeightDp)
                    .padding(Spacing.extraLarge),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                    top = headerHeightDp + 8.dp,
                    bottom = Spacing.medium
                )
            ) {
                itemsIndexed(
                    items = filteredNotifications,
                    key = { _, item -> item.id }
                ) { index, notification ->
                    AnimatedNotificationRow(
                        notification = notification,
                        index = index,
                        onClick = { onNotificationClick(notification.id) }
                    )
                }
            }
        }

        // ── Collapsing Header ─────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeightDp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PrimaryTeal, PrimaryTealDark)
                    )
                )
        ) {
            // Decorative circles (fade out as header collapses)
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer { alpha = 1f - collapseProgress }
            ) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.06f),
                    radius = 140f,
                    center = Offset(size.width * 0.85f, size.height * 0.15f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.04f),
                    radius = 100f,
                    center = Offset(size.width * 0.1f, size.height * 0.85f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.03f),
                    radius = 60f,
                    center = Offset(size.width * 0.5f, size.height * 0.05f)
                )
            }

            // ── Back button (always visible, stays top-left) ──────────
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(start = Spacing.small, top = 8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = lerp(0.15f, 0.0f, collapseProgress)))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // ── Animated Title "Notifications" ────────────────────────
            // Interpolate position & size between expanded and collapsed states
            val titleFontSize = lerp(24f, 18f, collapseProgress).sp
            val titleStartPadding = lerp(16f, 56f, collapseProgress).dp
            val titleTopPadding = with(density) {
                val expandedTop = statusBarPx + with(density) { 110.dp.toPx() }
                val collapsedTop = statusBarPx + with(density) { 18.dp.toPx() }
                lerp(expandedTop, collapsedTop, collapseProgress).toDp()
            }

            Text(
                text = "Notifications",
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .padding(start = titleStartPadding, top = titleTopPadding)
            )

            // ── Badge (fades out when collapsing) ─────────────────────
            if (unreadCount > 0) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AccentOrange,
                    modifier = Modifier
                        .padding(
                            start = with(density) {
                                lerp(16f + 180f, 56f + 130f, collapseProgress).dp
                            },
                            top = titleTopPadding + 2.dp
                        )
                        .graphicsLayer { alpha = 1f - collapseProgress }
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

            // ── Expanded-only content (subtitle, mark-all-read, filter chips) ─
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = (1f - collapseProgress * 2.5f).coerceIn(0f, 1f) }
                    .padding(
                        start = Spacing.medium,
                        end = Spacing.medium,
                        bottom = Spacing.medium
                    )
                    .align(Alignment.BottomStart)
            ) {
                // Mark all read
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Stay updated with events & community",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    TextButton(onClick = { }) {
                        Text(
                            text = "Mark all read",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.small))

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
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Animated Notification Row
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun AnimatedNotificationRow(
    notification: NotificationItem,
    index: Int,
    onClick: () -> Unit
) {
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(notification.id) {
        // Staggered delay: each item waits a bit longer
        delay((index * 60L).coerceAtMost(360L))
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 350)
        )
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                alpha = animProgress.value
                translationY = (1f - animProgress.value) * 60f
            }
    ) {
        NotificationRow(
            notification = notification,
            onClick = onClick
        )
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
