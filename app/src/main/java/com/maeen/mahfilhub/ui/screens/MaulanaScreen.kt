package com.maeen.mahfilhub.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maeen.mahfilhub.R
import com.maeen.mahfilhub.ui.theme.*

// ──────────────────────────────────────────────────────────────────────────
// Sample Maulana Data
// ──────────────────────────────────────────────────────────────────────────

data class MaulanaItem(
    val id: Int,
    val name: String,
    val title: String,
    val specialization: String,
    val location: String,
    val totalEvents: Int,
    val upcomingEvents: Int,
    val followers: Int,
    val rating: Float,
    val isVerified: Boolean = false,
    val isFollowing: Boolean = false,
    val category: String = "All"
)

private val sampleMaulanas = listOf(
    MaulanaItem(1, "Maulana Abdul Karim", "Senior Scholar", "Tafseer & Hadith", "Dhaka, Bangladesh", 120, 3, 4520, 4.9f, isVerified = true, category = "Popular"),
    MaulanaItem(2, "Maulana Tariq Jameel", "International Speaker", "Dawah & Islah", "Lahore, Pakistan", 85, 2, 12800, 4.8f, isVerified = true, category = "Popular"),
    MaulanaItem(3, "Maulana Hassan Ali", "Quran Teacher", "Tafseer Al-Quran", "Chittagong, Bangladesh", 64, 1, 2150, 4.7f, isVerified = false, category = "Popular"),
    MaulanaItem(4, "Maulana Ibrahim Khalil", "Youth Mentor", "Youth & Contemporary Issues", "Sylhet, Bangladesh", 42, 2, 1800, 4.6f, isVerified = false, category = "New"),
    MaulanaItem(5, "Qari Muhammad Yusuf", "Hafiz & Qari", "Quran Recitation & Tajweed", "Rajshahi, Bangladesh", 35, 1, 980, 4.9f, isVerified = true, category = "New"),
    MaulanaItem(6, "Mufti Abdul Rahman", "Islamic Finance Expert", "Fiqh & Islamic Finance", "Dhaka, Bangladesh", 28, 0, 1450, 4.5f, isVerified = true, category = "Popular"),
    MaulanaItem(7, "Maulana Shah Ahmed", "Community Leader", "Seerah & History", "Khulna, Bangladesh", 55, 2, 3200, 4.7f, isVerified = false, category = "Popular"),
    MaulanaItem(8, "Maulana Noor Islam", "Spiritual Guide", "Tasawwuf & Zikr", "Comilla, Bangladesh", 30, 1, 890, 4.4f, isVerified = false, category = "New"),
    MaulanaItem(9, "Maulana Fazlur Rahman", "Hadith Scholar", "Sahih Bukhari & Muslim", "Barisal, Bangladesh", 48, 0, 2600, 4.8f, isVerified = true, category = "Popular"),
    MaulanaItem(10, "Maulana Yusuf Ali", "Education Specialist", "Islamic Education & Tarbiyah", "Rangpur, Bangladesh", 22, 1, 720, 4.3f, isVerified = false, category = "New")
)

// ──────────────────────────────────────────────────────────────────────────
// Maulana Screen
// ──────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaulanaScreen(
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    val filters = listOf("All", "Popular", "New", "Verified")

    val filteredMaulanas = remember(selectedFilter, searchQuery) {
        sampleMaulanas.filter { maulana ->
            val matchesFilter = when (selectedFilter) {
                "All" -> true
                "Verified" -> maulana.isVerified
                else -> maulana.category == selectedFilter
            }
            val matchesSearch = searchQuery.isEmpty() ||
                maulana.name.contains(searchQuery, ignoreCase = true) ||
                maulana.specialization.contains(searchQuery, ignoreCase = true) ||
                maulana.location.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesSearch
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = Spacing.small)
    ) {
        // ── Header ───────────────────────────────────────────────────
        item {
            MaulanaHeader(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it }
            )
        }

        // ── Stats Row ────────────────────────────────────────────────
        item {
            MaulanaStatsRow()
        }

        // ── Filter Chips ─────────────────────────────────────────────
        item {
            MaulanaFilterChips(
                filters = filters,
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )
        }

        // ── Results Count ────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.medium)
                    .padding(bottom = Spacing.small),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredMaulanas.size} scholars found",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${sampleMaulanas.count { it.isVerified }} Verified",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = VerifiedBadge
                )
            }
        }

        // ── Maulana Cards ────────────────────────────────────────────
        if (filteredMaulanas.isEmpty()) {
            item {
                EmptyMaulanaPlaceholder()
            }
        } else {
            items(filteredMaulanas, key = { it.id }) { maulana ->
                MaulanaProfileCard(
                    maulana = maulana,
                    modifier = Modifier.padding(horizontal = Spacing.medium)
                )
                Spacer(modifier = Modifier.height(Spacing.medium))
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Maulana Header
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun MaulanaHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PrimaryTeal, PrimaryTealDark)
                )
            )
    ) {
        // Decorative elements
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.06f),
                radius = 110f,
                center = Offset(size.width * 0.9f, size.height * 0.25f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.04f),
                radius = 80f,
                center = Offset(size.width * 0.1f, size.height * 0.75f)
            )
            // Additional small decorative circle
            drawCircle(
                color = Color.White.copy(alpha = 0.03f),
                radius = 50f,
                center = Offset(size.width * 0.5f, size.height * 0.1f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = Spacing.medium, vertical = Spacing.medium)
        ) {
            // Title row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.nav_maulana),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Find renowned Islamic scholars",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                // Sort icon
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Filled.List,
                        contentDescription = "Sort",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.medium))

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                placeholder = {
                    Text(
                        "Search scholars…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = stringResource(R.string.cd_close),
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedBorderColor = Color.White.copy(alpha = 0.4f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedContainerColor = Color.White.copy(alpha = 0.1f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f)
                ),
                textStyle = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(Spacing.small))
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Stats Row
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun MaulanaStatsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium, vertical = Spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
    ) {
        StatCard(
            value = "${sampleMaulanas.size}",
            label = "Total",
            icon = Icons.Filled.Person,
            color = PrimaryTeal,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            value = "${sampleMaulanas.count { it.isVerified }}",
            label = "Verified",
            icon = Icons.Filled.CheckCircle,
            color = VerifiedBadge,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            value = "${sampleMaulanas.sumOf { it.upcomingEvents }}",
            label = "Upcoming",
            icon = Icons.Filled.DateRange,
            color = AccentOrange,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(Spacing.medium),
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
            Spacer(modifier = Modifier.height(Spacing.small))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Filter Chips
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun MaulanaFilterChips(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = Spacing.medium)
            .padding(bottom = Spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        filters.forEach { filter ->
            val isSelected = filter == selectedFilter
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) PrimaryTeal else MaterialTheme.colorScheme.surface,
                label = "chip_bg"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                label = "chip_text"
            )

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onFilterSelected(filter) },
                shape = RoundedCornerShape(20.dp),
                color = bgColor,
                shadowElevation = if (isSelected) 2.dp else 0.dp,
                border = if (!isSelected) {
                    ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        brush = SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    )
                } else null
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Filter-specific icon
                    if (filter == "Verified") {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = textColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = filter,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = textColor
                    )
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Maulana Profile Card
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun MaulanaProfileCard(
    maulana: MaulanaItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
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
            // Top gradient accent
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                PrimaryTeal,
                                PrimaryTealDark
                            )
                        )
                    )
            ) {
                // Decorative pattern
                Canvas(modifier = Modifier.matchParentSize()) {
                    val w = size.width
                    val h = size.height

                    // Islamic star-like pattern
                    for (i in 0..4) {
                        val cx = w * (0.15f + i * 0.2f)
                        drawCircle(
                            color = Color.White.copy(alpha = 0.04f),
                            radius = 20f,
                            center = Offset(cx, h * 0.5f)
                        )
                    }
                }

                // Verified + rating badges
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = Spacing.medium),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (maulana.isVerified) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = stringResource(R.string.cd_verified_badge),
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    stringResource(R.string.maulana_verified),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Rating
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AccentOrange
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                "${maulana.rating}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Main content row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.medium),
                verticalAlignment = Alignment.Top
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .offset(y = (-24).dp)
                        .shadow(6.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(PrimaryTealLight, PrimaryTeal)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = maulana.name.split(" ").takeLast(1).firstOrNull()?.first()?.toString() ?: "M",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(Spacing.medium))

                // Details column
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = maulana.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = maulana.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryTeal,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Specialization
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = AccentOrange,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = maulana.specialization,
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
                            tint = SecondaryGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = maulana.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.medium)
                    .padding(top = 0.dp, bottom = Spacing.small),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MaulanaStatItem(value = "${maulana.totalEvents}", label = "Events", color = PrimaryTeal)
                VerticalDivider(color = DividerLight, modifier = Modifier.height(36.dp))
                MaulanaStatItem(value = "${maulana.upcomingEvents}", label = "Upcoming", color = AccentOrange)
                VerticalDivider(color = DividerLight, modifier = Modifier.height(36.dp))
                MaulanaStatItem(value = formatFollowers(maulana.followers), label = "Followers", color = InfoBlue)
            }

            // Action Buttons Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.medium, vertical = Spacing.small),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                // View Profile Button
                Button(
                    onClick = { },
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryTeal
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.action_view_details),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Follow Button
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        brush = SolidColor(PrimaryTeal)
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = if (maulana.isFollowing) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = PrimaryTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (maulana.isFollowing) "Following" else "Follow",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryTeal
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.small))
        }
    }
}

@Composable
private fun MaulanaStatItem(
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = Spacing.small)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatFollowers(count: Int): String {
    return when {
        count >= 1000 -> String.format("%.1fK", count / 1000.0)
        else -> "$count"
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Empty State
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun EmptyMaulanaPlaceholder() {
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
                    .background(PrimaryTeal.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = PrimaryTeal,
                    modifier = Modifier.size(40.dp)
                )
            }

            Text(
                text = stringResource(R.string.empty_no_results),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(R.string.empty_no_results_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Previews
// ══════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MaulanaScreenPreview() {
    MahfilHubTheme {
        MaulanaScreen()
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun MaulanaScreenDarkPreview() {
    MahfilHubTheme(darkTheme = true) {
        MaulanaScreen()
    }
}
