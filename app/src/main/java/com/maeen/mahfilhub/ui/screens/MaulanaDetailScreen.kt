package com.maeen.mahfilhub.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maeen.mahfilhub.data.model.EventItem
import com.maeen.mahfilhub.data.model.MaulanaItem
import com.maeen.mahfilhub.ui.viewmodel.EventsViewModel
import com.maeen.mahfilhub.ui.viewmodel.MaulanaViewModel
import com.maeen.mahfilhub.ui.theme.*

// ══════════════════════════════════════════════════════════════════════════
// Maulana lookup is now delegated to MaulanaViewModel.maulanaById()
// ══════════════════════════════════════════════════════════════════════════

private fun formatFollowerCount(count: Int): String {
    return if (count >= 1000) String.format(java.util.Locale.US, "%.1fK", count / 1000.0) else "$count"
}

// Get events by this maulana — now delegated to EventsViewModel
// (the old helper referenced the removed sampleEvents global)

// ══════════════════════════════════════════════════════════════════════════
// Maulana Detail Screen
// ══════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaulanaDetailScreen(
    maulanaId: Int,
    modifier: Modifier = Modifier,
    maulanaViewModel: MaulanaViewModel = viewModel(),
    eventsViewModel: EventsViewModel = viewModel(),
    onBack: () -> Unit = {},
    onEventClick: (Int) -> Unit = {}
) {
    val maulana = remember { maulanaViewModel.maulanaById(maulanaId) }

    if (maulana == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Scholar not found", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    val maulanaEvents = remember { eventsViewModel.eventsForMaulana(maulana.name) }
    var isFollowing by remember { mutableStateOf(maulana.isFollowing) }
    val initial = maulana.name.split(" ").lastOrNull()?.take(1) ?: "M"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Hero Header ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                // Gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(PrimaryTeal, PrimaryTealDark)
                            )
                        )
                )

                // Decorative circles
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.06f),
                        radius = 180f,
                        center = Offset(size.width * 0.85f, size.height * 0.25f)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.04f),
                        radius = 130f,
                        center = Offset(size.width * 0.1f, size.height * 0.65f)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.03f),
                        radius = 90f,
                        center = Offset(size.width * 0.5f, size.height * 0.08f)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .statusBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Back + Share
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onBack,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.White.copy(alpha = 0.15f)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        IconButton(
                            onClick = { },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.White.copy(alpha = 0.15f)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(PrimaryTealLight, PrimaryTeal)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initial,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Name + Verified
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = maulana.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (maulana.isVerified) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Verified",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Text(
                        text = maulana.title,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Rating badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AccentOrange.copy(alpha = 0.3f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = AccentOrange,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format(java.util.Locale.US, "%.1f", maulana.rating),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentOrange
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // ── Stats Row ──────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MaulanaDetailStat(
                    modifier = Modifier.weight(1f),
                    value = "${maulana.totalEvents}",
                    label = "Total Events",
                    color = PrimaryTeal
                )
                MaulanaDetailStat(
                    modifier = Modifier.weight(1f),
                    value = "${maulana.upcomingEvents}",
                    label = "Upcoming",
                    color = AccentOrange
                )
                MaulanaDetailStat(
                    modifier = Modifier.weight(1f),
                    value = formatFollowerCount(maulana.followers),
                    label = "Followers",
                    color = InfoBlue
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── About ──────────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "About",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Info rows
                    MaulanaInfoRow(
                        icon = Icons.Outlined.Info,
                        label = "Specialization",
                        value = maulana.specialization,
                        color = AccentOrange
                    )
                    MaulanaInfoRow(
                        icon = Icons.Outlined.LocationOn,
                        label = "Location",
                        value = maulana.location,
                        color = SecondaryGreen
                    )
                    MaulanaInfoRow(
                        icon = Icons.Outlined.DateRange,
                        label = "Events Conducted",
                        value = "${maulana.totalEvents} events",
                        color = PrimaryTeal
                    )
                    MaulanaInfoRow(
                        icon = Icons.Outlined.Person,
                        label = "Followers",
                        value = formatFollowerCount(maulana.followers) + " followers",
                        color = InfoBlue
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "${maulana.name} is a renowned Islamic scholar specializing in ${maulana.specialization}. " +
                                "Based in ${maulana.location}, they have conducted ${maulana.totalEvents} events and have a community of " +
                                "${formatFollowerCount(maulana.followers)} devoted followers. " +
                                "Known for their eloquent delivery and deep knowledge, they continue to inspire " +
                                "and educate communities across the region.",
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Upcoming Events ────────────────────────────────────────
            Text(
                text = "Events by ${maulana.name.split(" ").last()}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (maulanaEvents.isEmpty()) {
                // Empty state
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = null,
                            tint = PrimaryTeal.copy(alpha = 0.5f),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No upcoming events",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                maulanaEvents.forEach { event ->
                    MaulanaEventMiniCard(
                        event = event,
                        onClick = { onEventClick(event.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        // ── Bottom Action Bar ──────────────────────────────────────────
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
                    onClick = {
                        isFollowing = !isFollowing
                        maulanaViewModel.toggleFollow(maulanaId)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = if (isFollowing) ButtonDefaults.outlinedButtonColors(
                        contentColor = PrimaryTeal
                    ) else ButtonDefaults.outlinedButtonColors()
                ) {
                    Icon(
                        imageVector = if (isFollowing) Icons.Filled.Favorite
                        else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (isFollowing) ErrorRed else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isFollowing) "Following" else "Follow",
                        fontWeight = FontWeight.SemiBold
                    )
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
                        imageVector = Icons.Filled.Email,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Contact",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Sub-components
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun MaulanaDetailStat(
    modifier: Modifier = Modifier,
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
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MaulanaInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
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
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun MaulanaEventMiniCard(
    event: EventItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date pill
            val dateParts = event.date.split(" ")
            val day = dateParts.getOrNull(1)?.replace(",", "") ?: ""
            val month = dateParts.getOrNull(0) ?: ""

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(PrimaryTeal.copy(alpha = 0.1f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = day,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTeal
                )
                Text(
                    text = month,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryTeal
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = event.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (event.isLive) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ErrorRed.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "LIVE",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ErrorRed
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = AccentOrange
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.location,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = SecondaryGreen
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.time,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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

// ══════════════════════════════════════════════════════════════════════════
// Previews
// ══════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun MaulanaDetailScreenPreview() {
    MahfilHubTheme {
        MaulanaDetailScreen(maulanaId = 1)
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun MaulanaDetailScreenDarkPreview() {
    MahfilHubTheme(darkTheme = true) {
        MaulanaDetailScreen(maulanaId = 2)
    }
}
