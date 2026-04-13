package com.maeen.mahfilhub.ui.screens
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import com.maeen.mahfilhub.ui.theme.*
import kotlinx.coroutines.delay
// ══════════════════════════════════════════════════════════════════════════
// Sample Following Data
// ══════════════════════════════════════════════════════════════════════════
private data class FollowedScholar(
    val id: Int,
    val name: String,
    val title: String,
    val location: String,
    val followedSince: String,
    val upcomingEvents: Int = 0,
    val totalEvents: Int = 0,
    val isVerified: Boolean = false,
    val initial: String = name.first().toString()
)
private val sampleFollowedScholars = listOf(
    FollowedScholar(
        id = 1, name = "Maulana Abdul Karim", title = "Senior Islamic Scholar",
        location = "Dhaka, Bangladesh", followedSince = "Following since Jan 2026",
        upcomingEvents = 3, totalEvents = 45, isVerified = true
    ),
    FollowedScholar(
        id = 2, name = "Maulana Tariq Jameel", title = "International Speaker",
        location = "Lahore, Pakistan", followedSince = "Following since Mar 2025",
        upcomingEvents = 1, totalEvents = 120, isVerified = true
    ),
    FollowedScholar(
        id = 3, name = "Maulana Hassan Ali", title = "Quran Scholar",
        location = "Chittagong, Bangladesh", followedSince = "Following since Jun 2025",
        upcomingEvents = 2, totalEvents = 30, isVerified = true
    ),
    FollowedScholar(
        id = 4, name = "Maulana Ibrahim Khalil", title = "Youth Motivational Speaker",
        location = "Sylhet, Bangladesh", followedSince = "Following since Sep 2025",
        upcomingEvents = 0, totalEvents = 18
    ),
    FollowedScholar(
        id = 5, name = "Qari Muhammad Yusuf", title = "Hafiz & Qari",
        location = "Rajshahi, Bangladesh", followedSince = "Following since Nov 2025",
        upcomingEvents = 1, totalEvents = 22, isVerified = true
    ),
    FollowedScholar(
        id = 6, name = "Mufti Abdul Rahman", title = "Islamic Finance Expert",
        location = "Dhaka, Bangladesh", followedSince = "Following since Dec 2025",
        upcomingEvents = 0, totalEvents = 15
    ),
    FollowedScholar(
        id = 7, name = "Maulana Shah Ahmed", title = "Hadith Scholar",
        location = "Khulna, Bangladesh", followedSince = "Following since Feb 2026",
        upcomingEvents = 2, totalEvents = 55, isVerified = true
    ),
    FollowedScholar(
        id = 8, name = "Maulana Noor Islam", title = "Tafseer Specialist",
        location = "Comilla, Bangladesh", followedSince = "Following since Mar 2026",
        upcomingEvents = 1, totalEvents = 28
    )
)
// ══════════════════════════════════════════════════════════════════════════
// Constants
// ══════════════════════════════════════════════════════════════════════════
private val FollowingExpandedHeaderHeight = 220.dp
private val FollowingToolbarHeight = 56.dp
// ══════════════════════════════════════════════════════════════════════════
// Following Screen
// ══════════════════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowingScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onScholarClick: (Int) -> Unit = {}
) {
    val density = LocalDensity.current
    val statusBarPx = WindowInsets.statusBars.getTop(density).toFloat()
    val toolbarPx = with(density) { FollowingToolbarHeight.toPx() }
    val collapsedPx = statusBarPx + toolbarPx
    val expandedPx = with(density) { FollowingExpandedHeaderHeight.toPx() }
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
        val headerHeightDp = with(density) { (expandedPx - headerOffset).toDp() }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = headerHeightDp + 8.dp, bottom = Spacing.large)
        ) {
            item {
                FollowingStatsBar(
                    totalFollowing = sampleFollowedScholars.size,
                    withUpcoming = sampleFollowedScholars.count { it.upcomingEvents > 0 }
                )
            }
            itemsIndexed(
                items = sampleFollowedScholars,
                key = { _, item -> item.id }
            ) { index, scholar ->
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(index * 50L)
                    visible = true
                }
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(300)) + slideInVertically(
                        initialOffsetY = { it / 4 }, animationSpec = tween(300)
                    )
                ) {
                    FollowedScholarCard(
                        scholar = scholar,
                        onClick = { onScholarClick(scholar.id) }
                    )
                }
            }
        }
        // ── Collapsing Header ───────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeightDp)
                .background(Brush.verticalGradient(listOf(PrimaryTeal, PrimaryTealDark)))
        ) {
            Canvas(
                modifier = Modifier.matchParentSize()
                    .graphicsLayer { alpha = 1f - collapseProgress }
            ) {
                drawCircle(Color.White.copy(alpha = 0.06f), 120f, Offset(size.width * 0.85f, size.height * 0.2f))
                drawCircle(Color.White.copy(alpha = 0.04f), 80f, Offset(size.width * 0.1f, size.height * 0.8f))
                drawCircle(Color.White.copy(alpha = 0.03f), 50f, Offset(size.width * 0.5f, size.height * 0.05f))
            }
            IconButton(
                onClick = onBack,
                modifier = Modifier.statusBarsPadding()
                    .padding(start = Spacing.small, top = 8.dp).size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = lerp(0.15f, 0.0f, collapseProgress)))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White, modifier = Modifier.size(20.dp))
            }
            val titleFontSize = lerp(22f, 18f, collapseProgress).sp
            val titleStartPadding = lerp(16f, 56f, collapseProgress).dp
            val titleTopPadding = with(density) {
                val expandedTop = statusBarPx + 70.dp.toPx()
                val collapsedTop = statusBarPx + 18.dp.toPx()
                lerp(expandedTop, collapsedTop, collapseProgress).toDp()
            }
            Text("Following", fontSize = titleFontSize, fontWeight = FontWeight.Bold,
                color = Color.White, modifier = Modifier.padding(start = titleStartPadding, top = titleTopPadding))
            Text("Scholars you follow for event updates",
                style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 16.dp, top = titleTopPadding + 30.dp)
                    .graphicsLayer { alpha = (1f - collapseProgress * 2f).coerceIn(0f, 1f) })
            Row(
                modifier = Modifier.align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 16.dp)
                    .graphicsLayer { alpha = (1f - collapseProgress * 2.5f).coerceIn(0f, 1f) },
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                FollowingHeaderChip(Icons.Filled.Person, "${sampleFollowedScholars.size} Following")
                FollowingHeaderChip(Icons.Filled.DateRange, "${sampleFollowedScholars.sumOf { it.upcomingEvents }} Upcoming")
            }
        }
    }
}
@Composable
private fun FollowingHeaderChip(icon: ImageVector, label: String) {
    Surface(shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.15f)) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(14.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }
}
@Composable
private fun FollowingStatsBar(totalFollowing: Int, withUpcoming: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.medium, vertical = Spacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$totalFollowing scholars followed", style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (withUpcoming > 0) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(SuccessGreen))
                Text("$withUpcoming with events", style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold, color = SuccessGreen)
            }
        }
    }
}
@Composable
private fun FollowedScholarCard(scholar: FollowedScholar, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = Spacing.medium, vertical = Spacing.extraSmall)
            .shadow(4.dp, RoundedCornerShape(16.dp), ambientColor = CardShadow, spotColor = CardShadow),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onClick
    ) {
        Row(Modifier.fillMaxWidth().padding(Spacing.medium), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(56.dp).clip(CircleShape)
                    .background(Brush.linearGradient(listOf(PrimaryTealLight, PrimaryTeal))),
                contentAlignment = Alignment.Center
            ) {
                Text(scholar.initial, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(Modifier.width(Spacing.medium))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.extraSmall)) {
                    Text(scholar.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false))
                    if (scholar.isVerified) {
                        Icon(Icons.Filled.CheckCircle, "Verified", tint = VerifiedBadge, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(scholar.title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Outlined.LocationOn, null, tint = AccentOrange, modifier = Modifier.size(12.dp))
                    Text(scholar.location, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(Spacing.small))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(scholar.followedSince, style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                        if (scholar.upcomingEvents > 0) {
                            Surface(shape = RoundedCornerShape(8.dp), color = PrimaryTeal.copy(alpha = 0.12f)) {
                                Text("${scholar.upcomingEvents} upcoming", Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                            }
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                            Text("${scholar.totalEvents} total", Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun FollowingScreenPreview() { MahfilHubTheme { FollowingScreen() } }
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun FollowingScreenDarkPreview() { MahfilHubTheme(darkTheme = true) { FollowingScreen() } }
