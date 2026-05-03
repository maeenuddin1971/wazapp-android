package com.maeen.mahfilhub.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.draw.shadow
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
import com.maeen.mahfilhub.data.model.SavedEventItem
import com.maeen.mahfilhub.ui.viewmodel.SavedEventsViewModel
import com.maeen.mahfilhub.ui.theme.*
import kotlinx.coroutines.delay

// ══════════════════════════════════════════════════════════════════════════
// SavedEventItem model is now in data/model/SavedEventItem.kt
// Seed data & state logic is now in ui/viewmodel/SavedEventsViewModel.kt
// ══════════════════════════════════════════════════════════════════════════

// ══════════════════════════════════════════════════════════════════════════
// Constants
// ══════════════════════════════════════════════════════════════════════════

private val ExpandedHeaderHeight = 240.dp
private val ToolbarHeight = 56.dp

// ══════════════════════════════════════════════════════════════════════════
// Saved Events Screen — Collapsing Header
// ══════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedEventsScreen(
    modifier: Modifier = Modifier,
    savedEventsViewModel: SavedEventsViewModel = viewModel(),
    onBack: () -> Unit = {},
    onEventClick: (Int) -> Unit = {}
) {
    val state by savedEventsViewModel.uiState.collectAsState()

    val selectedFilter = state.selectedFilter
    val filters = state.filters
    val filteredEvents = state.filteredEvents

    // ── Collapsing header logic ─────────────────────────────────────────
    val density = LocalDensity.current
    val expandedHeightPx = with(density) { ExpandedHeaderHeight.toPx() }
    val toolbarHeightPx = with(density) { ToolbarHeight.toPx() }
    val scrollOffset = remember { Animatable(0f) }
    val collapseFraction =
        (scrollOffset.value / (expandedHeightPx - toolbarHeightPx)).coerceIn(0f, 1f)

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = (scrollOffset.value - delta)
                    .coerceIn(0f, expandedHeightPx - toolbarHeightPx)
                val consumed = scrollOffset.value - newOffset
                kotlinx.coroutines.runBlocking {
                    scrollOffset.snapTo(newOffset)
                }
                return Offset(0f, consumed)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .nestedScroll(nestedScrollConnection)
    ) {
        // ── Scrollable content ──────────────────────────────────────────
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = ExpandedHeaderHeight,
                bottom = Spacing.large
            )
        ) {
            // Filter Chips
            item {
                SavedEventsFilterRow(
                    filters = filters,
                    selectedFilter = selectedFilter,
                    onFilterSelected = { savedEventsViewModel.setFilter(it) }
                )
            }

            // Stats bar
            item {
                SavedEventsStatsBar(
                    totalSaved = state.totalSaved,
                    upcomingCount = state.upcomingCount
                )
            }

            // Events list
            if (filteredEvents.isEmpty()) {
                item { SavedEventsEmptyState() }
            } else {
                itemsIndexed(
                    items = filteredEvents,
                    key = { _, item -> item.id }
                ) { index, event ->
                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        delay(index * 50L)
                        visible = true
                    }
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(300)) + slideInVertically(
                            initialOffsetY = { it / 4 },
                            animationSpec = tween(300)
                        )
                    ) {
                        SavedEventCard(
                            event = event,
                            onClick = { onEventClick(event.id) }
                        )
                    }
                }
            }
        }

        // ── Collapsing Header ───────────────────────────────────────────
        SavedEventsCollapsingHeader(
            collapseFraction = collapseFraction,
            expandedHeight = ExpandedHeaderHeight,
            toolbarHeight = ToolbarHeight,
            onBack = onBack,
            totalSaved = state.totalSaved,
            upcomingCount = state.upcomingCount
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Collapsing Header
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun SavedEventsCollapsingHeader(
    collapseFraction: Float,
    expandedHeight: androidx.compose.ui.unit.Dp,
    toolbarHeight: androidx.compose.ui.unit.Dp,
    onBack: () -> Unit,
    totalSaved: Int,
    upcomingCount: Int
) {
    val headerHeight = lerp(
        expandedHeight.value,
        toolbarHeight.value,
        collapseFraction
    ).dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(headerHeight)
            .shadow(
                elevation = lerp(0f, 8f, collapseFraction).dp,
                shape = RoundedCornerShape(
                    bottomStart = lerp(0f, 0f, collapseFraction).dp,
                    bottomEnd = lerp(0f, 0f, collapseFraction).dp
                )
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PrimaryTeal, PrimaryTealDark)
                )
            )
    ) {
        // Decorative circles
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.06f),
                radius = 130f,
                center = Offset(size.width * 0.85f, size.height * 0.25f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.04f),
                radius = 90f,
                center = Offset(size.width * 0.1f, size.height * 0.8f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.03f),
                radius = 60f,
                center = Offset(size.width * 0.5f, size.height * 0.1f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Toolbar row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(toolbarHeight)
                    .padding(horizontal = Spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Saved Events",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            alpha = if (collapseFraction > 0.5f) {
                                lerp(0f, 1f, ((collapseFraction - 0.5f) * 2f).coerceIn(0f, 1f))
                            } else 0f
                        }
                )

                // Count badge (visible in collapsed state)
                if (collapseFraction > 0.5f) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.padding(end = Spacing.small)
                    ) {
                        Text(
                            text = "$totalSaved",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Expanded content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.medium)
                    .graphicsLayer {
                        alpha = 1f - collapseFraction
                        translationY = -collapseFraction * 50f
                    }
            ) {
                Spacer(modifier = Modifier.height(Spacing.small))

                // Icon + Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Saved Events",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Your bookmarked events collection",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.medium))

                // Stats chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    SavedHeaderChip(
                        icon = Icons.Filled.Favorite,
                        label = "$totalSaved Saved",
                        color = ErrorRed
                    )
                    SavedHeaderChip(
                        icon = Icons.Filled.DateRange,
                        label = "$upcomingCount Upcoming",
                        color = AccentOrange
                    )
                    SavedHeaderChip(
                        icon = Icons.Filled.Check,
                        label = "${totalSaved - upcomingCount} Past",
                        color = SuccessGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedHeaderChip(
    icon: ImageVector,
    label: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Filter Row
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun SavedEventsFilterRow(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium, vertical = Spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        filters.forEach { filter ->
            val isSelected = filter == selectedFilter
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) PrimaryTeal else MaterialTheme.colorScheme.surface,
                label = "filter_bg"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                label = "filter_text"
            )

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onFilterSelected(filter) },
                shape = RoundedCornerShape(20.dp),
                color = bgColor,
                shadowElevation = if (isSelected) 2.dp else 0.dp
            ) {
                Text(
                    text = filter,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = textColor
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Stats Bar
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun SavedEventsStatsBar(
    totalSaved: Int,
    upcomingCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium)
            .padding(bottom = Spacing.small),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$totalSaved saved events",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (upcomingCount > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(SuccessGreen)
                )
                Text(
                    text = "$upcomingCount Upcoming",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SuccessGreen
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Saved Event Card
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun SavedEventCard(
    event: SavedEventItem,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium, vertical = Spacing.extraSmall)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = CardShadow,
                spotColor = CardShadow
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = onClick
    ) {
        Column {
            // Top accent strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = if (event.isLive) {
                                listOf(ErrorRed, ErrorRed.copy(alpha = 0.8f))
                            } else if (event.isUpcoming) {
                                listOf(PrimaryTeal, PrimaryTealDark)
                            } else {
                                listOf(
                                    MaterialTheme.colorScheme.outlineVariant,
                                    MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.medium),
                verticalAlignment = Alignment.Top
            ) {
                // Date column
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(52.dp)
                ) {
                    val dateParts = event.date.split(" ")
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (event.isUpcoming) {
                            PrimaryTeal.copy(alpha = 0.1f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (dateParts.size >= 2) dateParts[1].removeSuffix(",") else "",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (event.isUpcoming) PrimaryTeal else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (dateParts.isNotEmpty()) dateParts[0] else "",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (event.isUpcoming) PrimaryTeal else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(Spacing.medium))

                // Event details
                Column(modifier = Modifier.weight(1f)) {
                    // Title + badges row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                    ) {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (event.isLive) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ErrorRed
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                    )
                                    Text(
                                        "LIVE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Maulana
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = PrimaryTeal,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = event.maulana,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Location
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = AccentOrange,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = event.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Time
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = SecondaryGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = event.time,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.small))

                    // Bottom row: saved date + actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Saved date
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FavoriteBorder,
                                contentDescription = null,
                                tint = ErrorRed.copy(alpha = 0.7f),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = event.savedDate,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Action buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.extraSmall)) {
                            // Remove saved
                            IconButton(
                                onClick = { },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = "Remove saved",
                                    tint = ErrorRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            // Share
                            IconButton(
                                onClick = { },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Share,
                                    contentDescription = "Share",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Empty State
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun SavedEventsEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.extraLarge),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(ErrorRed.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = ErrorRed,
                    modifier = Modifier.size(40.dp)
                )
            }

            Text(
                text = "No Saved Events",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Events you save will appear here.\nTap the heart icon on any event to save it.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Previews
// ══════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun SavedEventsScreenPreview() {
    MahfilHubTheme {
        SavedEventsScreen()
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SavedEventsScreenDarkPreview() {
    MahfilHubTheme(darkTheme = true) {
        SavedEventsScreen()
    }
}

