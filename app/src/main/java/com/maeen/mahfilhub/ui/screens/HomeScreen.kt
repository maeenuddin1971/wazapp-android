package com.maeen.mahfilhub.ui.screens


import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maeen.mahfilhub.R
import com.maeen.mahfilhub.ui.theme.*



// ──────────────────────────────────────────────────────────────────────────
// Home Screen - Main shell with bottom navigation
// ──────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            HomeBottomNavBar(
                selectedIndex = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        // Switch between screens based on selected tab
        when (selectedTab) {
            0 -> HomeContent(modifier = Modifier.padding(innerPadding))
            1 -> EventsScreen(modifier = Modifier.padding(innerPadding))
            // TODO: 2 -> MaulanaScreen()
            // TODO: 3 -> ProfileScreen()
            else -> HomeContent(modifier = Modifier.padding(innerPadding))
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Home Content (Tab 0)
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun HomeContent(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        HomeHeader()
        QuickActionsSection()
        UpcomingEventsSection()
        FeaturedMaulanaSection()
        RecentActivitySection()
        Spacer(modifier = Modifier.height(Spacing.medium))
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Header
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun HomeHeader() {
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
                    onClick = { },
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
private fun UpcomingEventsSection() {
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

        // Horizontal scrolling event cards
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = Spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            EventCard(
                title = "Friday Waz Mahfil",
                maulana = "Maulana Abdul Karim",
                location = "Dhaka Central Mosque",
                date = "Mar 14, 2026",
                time = "After Jummah",
                isLive = false
            )
            EventCard(
                title = "Tafseer Al-Quran",
                maulana = "Maulana Tariq Jameel",
                location = "Baitul Mukarram",
                date = "Mar 15, 2026",
                time = "After Maghrib",
                isLive = true
            )
            EventCard(
                title = "Seerah Conference",
                maulana = "Maulana Hassan",
                location = "Chittagong Grand Masjid",
                date = "Mar 18, 2026",
                time = "10:00 AM",
                isLive = false
            )
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
    isLive: Boolean
) {
    Card(
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
private fun FeaturedMaulanaSection() {
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

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            MaulanaChip("Maulana Abdul Karim", "120 Events", true)
            MaulanaChip("Maulana Tariq Jameel", "85 Events", true)
            MaulanaChip("Maulana Hassan Ali", "64 Events", false)
            MaulanaChip("Maulana Ibrahim", "42 Events", false)
        }
    }
}

@Composable
private fun MaulanaChip(
    name: String,
    eventCount: String,
    isVerified: Boolean
) {
    Surface(
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
// Bottom Navigation Bar
// ══════════════════════════════════════════════════════════════════════════

private data class NavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
private fun HomeBottomNavBar(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val items = listOf(
        NavItem("Home", Icons.Filled.Home, Icons.Outlined.Home),
        NavItem("Events", Icons.Filled.DateRange, Icons.Outlined.DateRange),
        NavItem("Maulana", Icons.Filled.Person, Icons.Outlined.Person),
        NavItem("Profile", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = if (selectedIndex == index) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryTeal,
                    selectedTextColor = PrimaryTeal,
                    indicatorColor = PrimaryTeal.copy(alpha = 0.12f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
