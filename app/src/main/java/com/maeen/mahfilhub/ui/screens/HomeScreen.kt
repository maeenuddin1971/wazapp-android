package com.maeen.mahfilhub.ui.screens


import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maeen.mahfilhub.R
import com.maeen.mahfilhub.data.model.EventItem
import com.maeen.mahfilhub.data.model.MaulanaItem
import com.maeen.mahfilhub.ui.viewmodel.EventsViewModel
import com.maeen.mahfilhub.ui.viewmodel.MaulanaViewModel
import com.maeen.mahfilhub.ui.theme.*
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch



// ──────────────────────────────────────────────────────────────────────────
// Home Screen - Main shell with bottom navigation
// ──────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onEventClick: (Int) -> Unit = {},
    onMaulanaClick: (Int) -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onPrivacySecurityClick: () -> Unit = {},
    onSavedEventsClick: () -> Unit = {},
    onFollowingClick: () -> Unit = {},
    onMyRemindersClick: () -> Unit = {},
    onEventHistoryClick: () -> Unit = {},
    onLanguageClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onHelpFeedbackClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val eventsViewModel: EventsViewModel = viewModel()
    val eventsState by eventsViewModel.uiState.collectAsState()
    val maulanaViewModel: MaulanaViewModel = viewModel()
    val maulanaState by maulanaViewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(initialPage = 0) { 4 }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var backPressedOnce by remember { mutableStateOf(false) }

    // Back press: non-Home tab → go to Home; Home tab → double-press to exit
    BackHandler(enabled = true) {
        if (pagerState.currentPage != 0) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(0)
            }
        } else if (backPressedOnce) {
            (context as? android.app.Activity)?.finish()
        } else {
            backPressedOnce = true
            Toast.makeText(context, "Tap back again to exit", Toast.LENGTH_SHORT).show()
            coroutineScope.launch {
                kotlinx.coroutines.delay(2000)
                backPressedOnce = false
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            CompactBottomNavBar(
                selectedIndex = pagerState.currentPage,
                onTabSelected = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            beyondViewportPageCount = 1,
            key = { it }
        ) { page ->
            when (page) {
                0 -> HomeContent(
                    upcomingEvents = eventsState.upcomingEvents,
                    featuredMaulanas = maulanaState.featuredMaulanas,
                    onEventClick = onEventClick,
                    onMaulanaClick = onMaulanaClick,
                    onNotificationsClick = onNotificationsClick
                )
                1 -> EventsScreen(
                    eventsViewModel = eventsViewModel,
                    onEventClick = onEventClick
                )
                2 -> MaulanaScreen(
                    maulanaViewModel = maulanaViewModel,
                    onMaulanaClick = onMaulanaClick
                )
                3 -> ProfileScreen(
                    onNotificationsClick = onNotificationsClick,
                    onEditProfileClick = onEditProfileClick,
                    onPrivacySecurityClick = onPrivacySecurityClick,
                    onSavedEventsClick = onSavedEventsClick,
                    onFollowingClick = onFollowingClick,
                    onMyRemindersClick = onMyRemindersClick,
                    onEventHistoryClick = onEventHistoryClick,
                    onLanguageClick = onLanguageClick,
                    onAboutClick = onAboutClick,
                    onHelpFeedbackClick = onHelpFeedbackClick,
                    onLogoutClick = onLogoutClick,
                    onSettingsClick = onSettingsClick
                )
                else -> HomeContent(
                    upcomingEvents = eventsState.upcomingEvents,
                    featuredMaulanas = maulanaState.featuredMaulanas,
                    onEventClick = onEventClick,
                    onMaulanaClick = onMaulanaClick,
                    onNotificationsClick = onNotificationsClick
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Home Content (Tab 0)
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    upcomingEvents: List<EventItem> = emptyList(),
    featuredMaulanas: List<MaulanaItem> = emptyList(),
    onEventClick: (Int) -> Unit = {},
    onMaulanaClick: (Int) -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        HomeHeader(onNotificationsClick = onNotificationsClick)
        QuickActionsSection()
        UpcomingEventsSection(
            events = upcomingEvents,
            onEventClick = onEventClick
        )
        FeaturedMaulanaSection(
            maulanas = featuredMaulanas,
            onMaulanaClick = onMaulanaClick
        )
        RecentActivitySection()
        Spacer(modifier = Modifier.height(Spacing.medium))
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Header
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun HomeHeader(onNotificationsClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PrimaryTeal, PrimaryTealDark)
                )
            )
    ) {
        // Subtle decorative circles
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = 120f,
                center = Offset(size.width * 0.9f, size.height * 0.2f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.04f),
                radius = 80f,
                center = Offset(size.width * 0.1f, size.height * 0.8f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = Spacing.medium, vertical = Spacing.medium)
        ) {
            // Top row: Greeting + Notification bell
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Assalamu Alaikum",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "Welcome Back 👋",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Notification icon
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = AccentOrange
                            ) {
                                Text("3", fontSize = 10.sp, color = Color.White)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.medium))

            // Search bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { }
                        .padding(horizontal = Spacing.medium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = stringResource(R.string.cd_search),
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.small))
                    Text(
                        text = stringResource(R.string.input_search_events),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.small))
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Quick Actions
// ══════════════════════════════════════════════════════════════════════════

private data class QuickAction(
    val icon: ImageVector,
    val label: String,
    val color: Color
)

@Composable
private fun QuickActionsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium, vertical = Spacing.medium)
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(Spacing.medium))

        val actions = listOf(
            QuickAction(Icons.Filled.DateRange, "Events", PrimaryTeal),
            QuickAction(Icons.Filled.Person, "Maulana", AccentOrange),
            QuickAction(Icons.Filled.LocationOn, "Nearby", SecondaryGreen),
            QuickAction(Icons.Filled.Star, "Saved", InfoBlue)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            actions.forEach { action ->
                QuickActionItem(action = action)
            }
        }
    }
}

@Composable
private fun QuickActionItem(action: QuickAction) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(CornerRadius.large))
            .clickable { }
            .padding(Spacing.small)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = action.color.copy(alpha = 0.2f),
                    spotColor = action.color.copy(alpha = 0.2f)
                )
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            action.color,
                            action.color.copy(alpha = 0.8f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.label,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.height(Spacing.small))

        Text(
            text = action.label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Upcoming Events
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun UpcomingEventsSection(
    events: List<EventItem> = emptyList(),
    onEventClick: (Int) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.small)
    ) {
        // Section header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.event_upcoming),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = { }) {
                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelLarge,
                    color = PrimaryTeal
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.small))

        // Horizontal scrolling event cards — driven by ViewModel data
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = Spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            events.forEach { event ->
                EventCard(
                    title = event.title,
                    maulana = event.maulana,
                    location = event.location,
                    date = event.date,
                    time = event.time,
                    isLive = event.isLive,
                    onClick = { onEventClick(event.id) }
                )
            }
        }
    }
}

@Composable
private fun EventCard(
    title: String,
    maulana: String,
    location: String,
    date: String,
    time: String,
    isLive: Boolean,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(280.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = CardShadow,
                spotColor = CardShadow
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // Top accent strip with mosque illustration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(PrimaryTeal, PrimaryTealDark)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Decorative pattern
                Canvas(modifier = Modifier.matchParentSize()) {
                    val w = size.width
                    val h = size.height
                    // Islamic arch pattern
                    drawArc(
                        color = Color.White.copy(alpha = 0.08f),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = true,
                        topLeft = Offset(w * 0.3f, h * 0.1f),
                        size = androidx.compose.ui.geometry.Size(w * 0.4f, h * 0.8f)
                    )
                    drawArc(
                        color = Color.White.copy(alpha = 0.06f),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = true,
                        topLeft = Offset(w * 0.1f, h * 0.3f),
                        size = androidx.compose.ui.geometry.Size(w * 0.2f, h * 0.6f)
                    )
                    drawArc(
                        color = Color.White.copy(alpha = 0.06f),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = true,
                        topLeft = Offset(w * 0.7f, h * 0.3f),
                        size = androidx.compose.ui.geometry.Size(w * 0.2f, h * 0.6f)
                    )
                }

                // Live badge
                if (isLive) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(Spacing.small)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ErrorRed,
                            modifier = Modifier.shadow(2.dp, RoundedCornerShape(12.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                                Text(
                                    "LIVE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }

                // Date chip
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(Spacing.small)
                        .offset(y = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = AccentOrange,
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = date,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.medium)
                    .padding(top = Spacing.small)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(Spacing.small))

                // Maulana row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = PrimaryTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = maulana,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Location row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = AccentOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Time row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = SecondaryGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = time,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Featured Maulana
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun FeaturedMaulanaSection(
    maulanas: List<MaulanaItem> = emptyList(),
    onMaulanaClick: (Int) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium, vertical = Spacing.medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Featured Maulana",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = { }) {
                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelLarge,
                    color = PrimaryTeal
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.small))

        // Horizontal scrolling maulana chips — driven by ViewModel data
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            maulanas.forEach { maulana ->
                MaulanaChip(
                    name = maulana.name,
                    eventCount = "${maulana.totalEvents} Events",
                    isVerified = maulana.isVerified,
                    onClick = { onMaulanaClick(maulana.id) }
                )
            }
        }
    }
}

@Composable
private fun MaulanaChip(
    name: String,
    eventCount: String,
    isVerified: Boolean,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .width(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(Spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar placeholder
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(PrimaryTealLight, PrimaryTeal)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.first().toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(Spacing.small))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name.split(" ").takeLast(2).joinToString(" "),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = stringResource(R.string.cd_verified_badge),
                        tint = VerifiedBadge,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Text(
                text = eventCount,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Recent Activity
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun RecentActivitySection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium)
    ) {
        Text(
            text = "Recent Activity",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(Spacing.medium))

        ActivityItem(
            icon = Icons.Filled.Check,
            iconColor = SuccessGreen,
            title = "Reminder Set",
            subtitle = "Friday Waz Mahfil — Mar 14",
            time = "2 hours ago"
        )

        ActivityItem(
            icon = Icons.Filled.Star,
            iconColor = InfoBlue,
            title = "Event Saved",
            subtitle = "Tafseer Al-Quran — Baitul Mukarram",
            time = "5 hours ago"
        )

        ActivityItem(
            icon = Icons.Filled.Share,
            iconColor = AccentOrange,
            title = "Event Shared",
            subtitle = "Seerah Conference — Chittagong",
            time = "Yesterday"
        )
    }
}

@Composable
private fun ActivityItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    time: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { }
            .padding(vertical = Spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(Spacing.medium))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = time,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Compact Bottom Navigation Bar
// ══════════════════════════════════════════════════════════════════════════

private data class NavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
private fun CompactBottomNavBar(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val items = listOf(
        NavItem("Home", Icons.Filled.Home, Icons.Outlined.Home),
        NavItem("Events", Icons.Filled.DateRange, Icons.Outlined.DateRange),
        NavItem("Maulana", Icons.Filled.Person, Icons.Outlined.Person),
        NavItem("Profile", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                CompactNavItem(
                    item = item,
                    isSelected = selectedIndex == index,
                    onClick = { onTabSelected(index) }
                )
            }
        }
    }
}

@Composable
private fun CompactNavItem(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryTeal else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(250),
        label = "navIconColor"
    )
    val labelColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryTeal else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(250),
        label = "navLabelColor"
    )
    val indicatorAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.12f else 0f,
        animationSpec = tween(250),
        label = "navIndicator"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(PrimaryTeal.copy(alpha = indicatorAlpha))
                .padding(horizontal = 12.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.label,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = labelColor,
            maxLines = 1
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Previews
// ══════════════════════════════════════════════════════════════════════════
@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    MahfilHubTheme {
        HomeContent()
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenDarkPreview() {
    MahfilHubTheme(darkTheme = true) {
        HomeContent()
    }
}

