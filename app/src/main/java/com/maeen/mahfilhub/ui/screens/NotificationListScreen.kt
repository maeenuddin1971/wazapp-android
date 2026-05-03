package com.maeen.mahfilhub.ui.screens

import androidx.lifecycle.viewmodel.compose.viewModel

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
import com.maeen.mahfilhub.data.model.NotificationItem
import com.maeen.mahfilhub.data.model.NotificationType
import com.maeen.mahfilhub.ui.viewmodel.NotificationsViewModel
import com.maeen.mahfilhub.ui.theme.*
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.delay

// ══════════════════════════════════════════════════════════════════════════
// NotificationItem model is now in data/model/NotificationItem.kt
// Seed data & state logic is now in ui/viewmodel/NotificationsViewModel.kt
// ══════════════════════════════════════════════════════════════════════════

// ══════════════════════════════════════════════════════════════════════════
// Notification List Screen — Collapsing Header
// ══════════════════════════════════════════════════════════════════════════

private val ExpandedHeaderHeight = 310.dp
private val ToolbarHeight = 56.dp   // collapsed toolbar area (below status bar)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationListScreen(
    modifier: Modifier = Modifier,
    notificationsViewModel: NotificationsViewModel = viewModel(),
    onBack: () -> Unit = {},
    onNotificationClick: (Int) -> Unit = {}
) {
    val state by notificationsViewModel.uiState.collectAsState()

    val selectedFilter = state.selectedFilter
    val filters = state.filters
    val filteredNotifications = state.filteredNotifications
    val unreadCount = state.unreadCount

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
                val expandedTop = statusBarPx + 110.dp.toPx()
                val collapsedTop = statusBarPx + 18.dp.toPx()
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
                    TextButton(onClick = { notificationsViewModel.markAllAsRead() }) {
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
                            onClick = { notificationsViewModel.setFilter(filter) },
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

// Notification lookup is now delegated to NotificationsViewModel.notificationById()
