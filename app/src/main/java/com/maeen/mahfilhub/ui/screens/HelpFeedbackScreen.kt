package com.maeen.mahfilhub.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Chat
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.maeen.mahfilhub.ui.theme.*

private val HelpExpandedHeaderHeight = 220.dp
private val HelpToolbarHeight = 56.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpFeedbackScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val density = LocalDensity.current
    val statusBarPx = WindowInsets.statusBars.getTop(density).toFloat()
    val toolbarPx = with(density) { HelpToolbarHeight.toPx() }
    val collapsedPx = statusBarPx + toolbarPx
    val expandedPx = with(density) { HelpExpandedHeaderHeight.toPx() }
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

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).nestedScroll(nestedScrollConnection)) {
        val headerHeightDp = with(density) { (expandedPx - headerOffset).toDp() }
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(top = headerHeightDp + 8.dp).padding(horizontal = Spacing.medium).padding(bottom = Spacing.extraLarge)
        ) {
            Spacer(Modifier.height(Spacing.medium))

            Text("Get Help", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.5.sp)
            Spacer(Modifier.height(Spacing.small))

            HelpActionRow(Icons.Outlined.Email, "Email Support", "Get help via email", InfoBlue) {}
            HelpActionRow(Icons.Outlined.Phone, "Call Support", "+880 1700-000000", PrimaryTeal) {}
            HelpActionRow(Icons.AutoMirrored.Outlined.Chat, "Live Chat", "Chat with our support team", SecondaryGreen) {}

            Spacer(Modifier.height(Spacing.large))
            Text("FAQ", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.5.sp)
            Spacer(Modifier.height(Spacing.small))

            HelpActionRow(Icons.Outlined.Info, "How to find events?", "Browse or search for events near you", AccentOrange) {}
            HelpActionRow(Icons.Outlined.FavoriteBorder, "How to save events?", "Tap the heart icon on any event", ErrorRed) {}
            HelpActionRow(Icons.Outlined.Notifications, "How do reminders work?", "Set reminders for upcoming events", SecondaryGreen) {}
            HelpActionRow(Icons.Outlined.Person, "How to follow scholars?", "Visit a scholar's profile and tap Follow", PrimaryTeal) {}

            Spacer(Modifier.height(Spacing.large))
            Text("Feedback", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.5.sp)
            Spacer(Modifier.height(Spacing.small))

            HelpActionRow(Icons.Outlined.Star, "Rate the App", "Leave a review on Play Store", AccentOrange) {}
            HelpActionRow(Icons.Outlined.Warning, "Report a Bug", "Help us fix issues", ErrorRed) {}
            HelpActionRow(Icons.Outlined.Lightbulb, "Suggest a Feature", "Share your ideas with us", InfoBlue) {}
        }

        // Collapsing Header
        Box(Modifier.fillMaxWidth().height(headerHeightDp).background(Brush.verticalGradient(listOf(InfoBlue, Color(0xFF1A3A5C))))) {
            Canvas(Modifier.matchParentSize().graphicsLayer { alpha = 1f - collapseProgress }) {
                drawCircle(Color.White.copy(alpha = 0.06f), 120f, Offset(size.width * 0.85f, size.height * 0.2f))
                drawCircle(Color.White.copy(alpha = 0.04f), 80f, Offset(size.width * 0.1f, size.height * 0.8f))
            }
            IconButton(
                onClick = onBack,
                modifier = Modifier.statusBarsPadding().padding(start = Spacing.small, top = 8.dp).size(40.dp)
                    .clip(CircleShape).background(Color.White.copy(alpha = lerp(0.15f, 0.0f, collapseProgress)))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White, modifier = Modifier.size(20.dp))
            }
            val titleFontSize = lerp(22f, 18f, collapseProgress).sp
            val titleStartPadding = lerp(16f, 56f, collapseProgress).dp
            val titleTopPadding = with(density) { lerp(statusBarPx + 70.dp.toPx(), statusBarPx + 18.dp.toPx(), collapseProgress).toDp() }
            Text("Help & Feedback", fontSize = titleFontSize, fontWeight = FontWeight.Bold, color = Color.White,
                modifier = Modifier.padding(start = titleStartPadding, top = titleTopPadding))
            Text("Contact us, report issues", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 16.dp, top = titleTopPadding + 30.dp)
                    .graphicsLayer { alpha = (1f - collapseProgress * 2f).coerceIn(0f, 1f) })
        }
    }
}

@Composable
private fun HelpActionRow(icon: ImageVector, title: String, subtitle: String, color: Color, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp) {
        Row(Modifier.fillMaxWidth().padding(horizontal = Spacing.medium, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(Spacing.medium))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HelpFeedbackScreenPreview() {
    MahfilHubTheme { HelpFeedbackScreen() }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun HelpFeedbackScreenDarkPreview() {
    MahfilHubTheme(darkTheme = true) { HelpFeedbackScreen() }
}




