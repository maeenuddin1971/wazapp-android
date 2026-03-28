package com.maeen.mahfilhub.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maeen.mahfilhub.ui.theme.*

// ══════════════════════════════════════════════════════════════════════════
// Notification Detail Screen
// ══════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailScreen(
    notificationId: Int,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onEventClick: ((Int) -> Unit)? = null,
    onMaulanaClick: ((Int) -> Unit)? = null
) {
    val notification = remember { findNotificationById(notificationId) }

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Hero Header ─────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(colors = headerColors))
        ) {
            // Decorative circles
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.06f),
                    radius = 120f,
                    center = Offset(size.width * 0.85f, size.height * 0.3f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.04f),
                    radius = 80f,
                    center = Offset(size.width * 0.1f, size.height * 0.8f)
                )
            }

            Column(
                modifier = Modifier.padding(
                    top = Spacing.extraLarge,
                    bottom = Spacing.large,
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

                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.large))

                // Type icon
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

                Spacer(modifier = Modifier.height(Spacing.medium))

                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
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

        // ── Content ─────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(Spacing.medium)
        ) {
            Spacer(modifier = Modifier.height(Spacing.small))

            // Message Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.large)
                ) {
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

            Spacer(modifier = Modifier.height(Spacing.medium))

            // Details Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.large)
                ) {
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

            // Action button - navigate to related content
            if (notification.relatedId != null) {
                Spacer(modifier = Modifier.height(Spacing.large))

                val actionText = when (notification.type) {
                    NotificationType.EVENT, NotificationType.REMINDER -> "View Event"
                    NotificationType.MAULANA -> "View Maulana Profile"
                    else -> null
                }

                if (actionText != null) {
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

            Spacer(modifier = Modifier.height(Spacing.large))
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Detail Info Row
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun DetailInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
