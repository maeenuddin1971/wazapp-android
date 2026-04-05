package com.maeen.mahfilhub.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.maeen.mahfilhub.ui.theme.*

// ══════════════════════════════════════════════════════════════════════════
// Helper: resolve event by ID from sample data
// ══════════════════════════════════════════════════════════════════════════

internal fun findEventById(eventId: Int): EventItem? {
    return sampleEvents.find { it.id == eventId }
}

// ══════════════════════════════════════════════════════════════════════════
// Constants
// ══════════════════════════════════════════════════════════════════════════

private val EventExpandedHeaderHeight = 310.dp
private val EventToolbarHeight = 56.dp

// ══════════════════════════════════════════════════════════════════════════
// Event Detail Screen — Collapsing Header
// ══════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: Int,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val event = remember { findEventById(eventId) }

    if (event == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Event not found", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    var isSaved by remember { mutableStateOf(false) }

    val headerGradient = if (event.isLive)
        listOf(ErrorRed, ErrorRed.copy(alpha = 0.8f))
    else
        listOf(PrimaryTeal, PrimaryTealDark)

    // ── Collapsing header state ────────────────────────────────────────
    val density = LocalDensity.current
    val statusBarPx = WindowInsets.statusBars.getTop(density).toFloat()
    val toolbarPx = with(density) { EventToolbarHeight.toPx() }
    val collapsedPx = statusBarPx + toolbarPx
    val expandedPx = with(density) { EventExpandedHeaderHeight.toPx() }
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .nestedScroll(nestedScrollConnection)
        ) {
            // ── Scrollable content (below header) ─────────────────────
            val headerHeightDp = with(density) { (expandedPx - headerOffset).toDp() }

            LazyColumn(
                contentPadding = PaddingValues(
                    top = headerHeightDp + 8.dp,
                    bottom = Spacing.large,
                    start = Spacing.medium,
                    end = Spacing.medium
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Date & Time
                item {
                    EventInfoCard(
                        icon = Icons.Outlined.DateRange,
                        iconColor = PrimaryTeal,
                        title = "Date & Time",
                        primaryText = event.date,
                        secondaryText = event.time
                    )
                }

                // Location
                item {
                    EventInfoCard(
                        icon = Icons.Outlined.LocationOn,
                        iconColor = AccentOrange,
                        title = "Location",
                        primaryText = event.location,
                        secondaryText = "Tap for directions"
                    )
                }

                // Scholar
                item {
                    EventInfoCard(
                        icon = Icons.Outlined.Person,
                        iconColor = VerifiedBadge,
                        title = "Scholar",
                        primaryText = event.maulana,
                        secondaryText = "View profile"
                    )
                }

                // About Section
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "About This Event",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Join us for an enlightening session of ${event.title} led by ${event.maulana}. " +
                                        "This event brings together the Muslim community for spiritual growth, " +
                                        "knowledge sharing, and strengthening of faith. Everyone is welcome to attend " +
                                        "and benefit from this blessed gathering.\n\n" +
                                        "The program will include recitation of the Holy Quran, " +
                                        "an insightful lecture, and a Q&A session. " +
                                        "Light refreshments will be provided after the event.",
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Quick Stats
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        EventQuickStat(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.Person,
                            value = "${event.attendees}",
                            label = "Attending",
                            color = PrimaryTeal
                        )
                        EventQuickStat(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.Star,
                            value = if (event.isFeatured) "Featured" else "Regular",
                            label = "Status",
                            color = AccentOrange
                        )
                        EventQuickStat(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.CheckCircle,
                            value = event.category,
                            label = "Category",
                            color = SecondaryGreen
                        )
                    }
                }

                // Guidelines
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = PrimaryTeal.copy(alpha = 0.08f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = PrimaryTeal,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Event Guidelines",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryTeal
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            listOf(
                                "Please arrive 15 minutes early",
                                "Maintain silence during the lecture",
                                "Bring your own prayer mat if possible",
                                "Photography is not allowed during the program"
                            ).forEach { guideline ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text("•", fontSize = 14.sp, color = PrimaryTeal, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = guideline,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Collapsing Header ─────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeightDp)
                    .background(Brush.verticalGradient(colors = headerGradient))
            ) {
                // Decorative circles (fade out)
                Canvas(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer { alpha = 1f - collapseProgress }
                ) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.06f),
                        radius = 160f,
                        center = Offset(size.width * 0.85f, size.height * 0.3f)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.04f),
                        radius = 120f,
                        center = Offset(size.width * 0.1f, size.height * 0.7f)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.03f),
                        radius = 80f,
                        center = Offset(size.width * 0.5f, size.height * 0.1f)
                    )
                }

                // ── Back button (always visible) ──────────────────────
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

                // ── Save & Share buttons (always visible) ─────────────
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(end = Spacing.small, top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { isSaved = !isSaved },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = lerp(0.15f, 0.0f, collapseProgress)))
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Favorite
                            else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Save",
                            tint = if (isSaved) ErrorRed else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = lerp(0.15f, 0.0f, collapseProgress)))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // ── Animated Event Title ──────────────────────────────
                val titleFontSize = lerp(24f, 18f, collapseProgress).sp
                val titleStartPadding = lerp(16f, 56f, collapseProgress).dp
                val titleEndPadding = lerp(16f, 16f, collapseProgress).dp
                val titleTopPadding = with(density) {
                    val expandedTop = statusBarPx + with(density) { 190.dp.toPx() }
                    val collapsedTop = statusBarPx + with(density) { 18.dp.toPx() }
                    lerp(expandedTop, collapsedTop, collapseProgress).toDp()
                }

                Text(
                    text = event.title,
                    fontSize = titleFontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = if (collapseProgress > 0.5f) 1 else 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(
                            start = titleStartPadding,
                            top = titleTopPadding,
                            end = titleEndPadding
                        )
                )

                // ── Expanded-only content (badges, attendees) ─────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer { alpha = (1f - collapseProgress * 2.5f).coerceIn(0f, 1f) }
                        .padding(start = Spacing.medium, end = Spacing.medium)
                ) {
                    // Badges — positioned below back button
                    Spacer(
                        modifier = Modifier
                            .statusBarsPadding()
                            .height(56.dp + Spacing.large)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (event.isLive) {
                            EventDetailBadge(
                                text = "● LIVE NOW",
                                containerColor = Color.White.copy(alpha = 0.25f),
                                contentColor = Color.White
                            )
                        }
                        if (event.isFeatured) {
                            EventDetailBadge(
                                text = "⭐ Featured",
                                containerColor = AccentOrange,
                                contentColor = Color.White
                            )
                        }
                    }
                }

                // ── Attendees row (bottom of expanded header) ─────────
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = Spacing.medium, bottom = Spacing.medium)
                        .graphicsLayer { alpha = (1f - collapseProgress * 2.5f).coerceIn(0f, 1f) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${event.attendees} attending",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // ── Bottom Action Bar (always visible) ────────────────────────
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Remind Me", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryTeal,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Attend", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Sub-components
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun EventDetailBadge(
    text: String,
    containerColor: Color,
    contentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = containerColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
private fun EventInfoCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    primaryText: String,
    secondaryText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = primaryText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = secondaryText,
                    fontSize = 12.sp,
                    color = iconColor,
                    fontWeight = FontWeight.Medium
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun EventQuickStat(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Previews
// ══════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun EventDetailScreenPreview() {
    MahfilHubTheme {
        EventDetailScreen(eventId = 1)
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun EventDetailScreenDarkPreview() {
    MahfilHubTheme(darkTheme = true) {
        EventDetailScreen(eventId = 2)
    }
}
