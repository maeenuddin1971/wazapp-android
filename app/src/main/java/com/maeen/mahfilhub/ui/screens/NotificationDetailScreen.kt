package com.maeen.mahfilhub.ui.screens

import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.util.lerp
import com.maeen.mahfilhub.data.model.NotificationType
import com.maeen.mahfilhub.ui.viewmodel.NotificationsViewModel
import com.maeen.mahfilhub.ui.theme.*

// ══════════════════════════════════════════════════════════════════════════
// Constants
// ══════════════════════════════════════════════════════════════════════════

private val DetailExpandedHeaderHeight = 300.dp
private val DetailToolbarHeight = 56.dp

// ══════════════════════════════════════════════════════════════════════════
// Notification Detail Screen — Collapsing Header
// ══════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailScreen(
    notificationId: Int,
    modifier: Modifier = Modifier,
    notificationsViewModel: NotificationsViewModel = viewModel(),
    onBack: () -> Unit = {},
    onEventClick: ((Int) -> Unit)? = null,
    onMaulanaClick: ((Int) -> Unit)? = null
) {
    val notification = remember {
        notificationsViewModel.notificationById(notificationId)
    }

    // Mark as read when the detail screen opens
    LaunchedEffect(notificationId) {
        notificationsViewModel.markAsRead(notificationId)
    }

    if (notification == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Notification not found", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    val iconInfo = when (notification.type) {
        NotificationType.EVENT -> Icons.Filled.DateRange to PrimaryTeal
        NotificationType.MAULANA -> Icons.Filled.Person to InfoBlue
        NotificationType.SYSTEM -> Icons.Filled.Info to AccentOrange
        NotificationType.REMINDER -> Icons.Filled.Notifications to SecondaryGreen
        NotificationType.COMMUNITY -> Icons.Filled.Favorite to ErrorRed
    }

    val headerColors = when (notification.type) {
        NotificationType.EVENT -> listOf(PrimaryTeal, PrimaryTealDark)
        NotificationType.MAULANA -> listOf(InfoBlue, Color(0xFF1565C0))
        NotificationType.SYSTEM -> listOf(AccentOrange, Color(0xFFE65100))
        NotificationType.REMINDER -> listOf(SecondaryGreen, SecondaryGreenDark)
        NotificationType.COMMUNITY -> listOf(ErrorRed, Color(0xFFC62828))
    }

    // ── Collapsing header state ────────────────────────────────────────
    val density = LocalDensity.current
    val statusBarPx = WindowInsets.statusBars.getTop(density).toFloat()
    val toolbarPx = with(density) { DetailToolbarHeight.toPx() }
    val collapsedPx = statusBarPx + toolbarPx
    val expandedPx = with(density) { DetailExpandedHeaderHeight.toPx() }
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

    val collapseProgress = (headerOffset / maxOffsetPx).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .nestedScroll(nestedScrollConnection)
    ) {
        // ── Content (below header) ────────────────────────────────────
        val headerHeightDp = with(density) { (expandedPx - headerOffset).toDp() }

        LazyColumn(
            contentPadding = PaddingValues(
                top = headerHeightDp + 8.dp,
                bottom = Spacing.large,
                start = Spacing.medium,
                end = Spacing.medium
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            // Message Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(Spacing.large)) {
                        Text(
                            text = "Message",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = notification.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 24.sp
                        )
                    }
                }
            }

            // Details Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(Spacing.large)) {
                        Text(
                            text = "Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        DetailInfoRow(
                            icon = Icons.Outlined.Info,
                            label = "Type",
                            value = notification.type.name.lowercase()
                                .replaceFirstChar { it.uppercase() },
                            color = iconInfo.second
                        )
                        Spacer(modifier = Modifier.height(Spacing.small))
                        DetailInfoRow(
                            icon = Icons.Filled.Info,
                            label = "Time",
                            value = notification.time,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(Spacing.small))
                        DetailInfoRow(
                            icon = if (notification.isRead) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                            label = "Status",
                            value = if (notification.isRead) "Read" else "Unread",
                            color = if (notification.isRead) SecondaryGreen else AccentOrange
                        )
                    }
                }
            }

            // Action button
            if (notification.relatedId != null) {
                val actionText = when (notification.type) {
                    NotificationType.EVENT, NotificationType.REMINDER -> "View Event"
                    NotificationType.MAULANA -> "View Maulana Profile"
                    else -> null
                }
                if (actionText != null) {
                    item {
                        Button(
                            onClick = {
                                when (notification.type) {
                                    NotificationType.EVENT, NotificationType.REMINDER ->
                                        onEventClick?.invoke(notification.relatedId)
                                    NotificationType.MAULANA ->
                                        onMaulanaClick?.invoke(notification.relatedId)
                                    else -> {}
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = iconInfo.second
                            )
                        ) {
                            Icon(
                                imageVector = when (notification.type) {
                                    NotificationType.EVENT, NotificationType.REMINDER -> Icons.Filled.DateRange
                                    NotificationType.MAULANA -> Icons.Filled.Person
                                    else -> Icons.Filled.Info
                                },
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.small))
                            Text(
                                text = actionText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // ── Collapsing Header ─────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeightDp)
                .background(Brush.verticalGradient(colors = headerColors))
        ) {
            // Decorative circles (fade out)
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer { alpha = 1f - collapseProgress }
            ) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.06f),
                    radius = 140f,
                    center = Offset(size.width * 0.85f, size.height * 0.2f)
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

            // ── Back button (always visible) ──────────────────────────
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

            // ── Delete button (fades out) ─────────────────────────────
            IconButton(
                onClick = { },
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(end = Spacing.small, top = 8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = lerp(0.15f, 0.0f, collapseProgress)))
                    .align(Alignment.TopEnd)
                    .graphicsLayer { alpha = 1f - collapseProgress }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // ── Animated Title ────────────────────────────────────────
            val titleFontSize = lerp(22f, 18f, collapseProgress).sp
            val titleStartPadding = lerp(16f, 56f, collapseProgress).dp
            val titleTopPadding = with(density) {
                val expandedTop = statusBarPx + with(density) { 160.dp.toPx() }
                val collapsedTop = statusBarPx + with(density) { 18.dp.toPx() }
                lerp(expandedTop, collapsedTop, collapseProgress).toDp()
            }

            Text(
                text = notification.title,
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = if (collapseProgress > 0.5f) 1 else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(start = titleStartPadding, top = titleTopPadding, end = Spacing.medium)
            )

            // ── Expanded-only content (icon, time, type badge) ────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = (1f - collapseProgress * 2.5f).coerceIn(0f, 1f) }
                    .padding(start = Spacing.medium, end = Spacing.medium)
            ) {
                // Type icon — positioned below the back button row
                Spacer(
                    modifier = Modifier
                        .statusBarsPadding()
                        .height(56.dp + Spacing.large)
                )

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconInfo.first,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // ── Time + type badge (bottom of expanded header) ─────────
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = Spacing.medium, end = Spacing.medium, bottom = Spacing.large)
                    .graphicsLayer { alpha = (1f - collapseProgress * 2.5f).coerceIn(0f, 1f) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = notification.time,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.width(Spacing.medium))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = notification.type.name.lowercase()
                            .replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Detail Info Row
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun DetailInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
