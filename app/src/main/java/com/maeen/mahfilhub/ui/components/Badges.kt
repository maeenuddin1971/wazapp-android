package com.maeen.mahfilhub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.maeen.mahfilhub.R
import com.maeen.mahfilhub.ui.theme.VerifiedBadge

/**
 * Verified badge component for Maulana profiles
 */
@Composable
fun VerifiedBadge(
    modifier: Modifier = Modifier,
    size: BadgeSize = BadgeSize.Small
) {
    val iconSize = when (size) {
        BadgeSize.Small -> 16.dp
        BadgeSize.Medium -> 20.dp
        BadgeSize.Large -> 24.dp
    }
    
    Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = "Verified",
        modifier = modifier.size(iconSize),
        tint = VerifiedBadge
    )
}

enum class BadgeSize {
    Small, Medium, Large
}

/**
 * Status badge component for event status, user status, etc.
 */
@Composable
fun StatusBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    icon: ImageVector? = null
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = contentColor
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Notification badge (dot indicator)
 */
@Composable
fun NotificationDot(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.error
) {
    Box(
        modifier = modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color)
    )
}

/**
 * Count badge for notifications, messages, etc.
 */
@Composable
fun CountBadge(
    count: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.error,
    contentColor: Color = MaterialTheme.colorScheme.onError,
    maxCount: Int = 99
) {
    if (count > 0) {
        Surface(
            modifier = modifier,
            shape = CircleShape,
            color = backgroundColor
        ) {
            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = 18.dp, minHeight = 18.dp)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (count > maxCount) "$maxCount+" else count.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Sponsored/Featured badge for events
 */
@Composable
fun SponsoredBadge(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.tertiary
    ) {
        Text(
            text = stringResource(R.string.event_sponsored),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onTertiary,
            fontWeight = FontWeight.Bold
        )
    }
}
