package com.maeen.mahfilhub.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.maeen.mahfilhub.ui.theme.*

private val SettingsExpandedHeaderHeight = 200.dp
private val SettingsToolbarHeight = 56.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onPrivacySecurityClick: () -> Unit = {},
    onLanguageClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onHelpFeedbackClick: () -> Unit = {}
) {
    val density = LocalDensity.current
    val statusBarPx = WindowInsets.statusBars.getTop(density).toFloat()
    val toolbarPx = with(density) { SettingsToolbarHeight.toPx() }
    val collapsedPx = statusBarPx + toolbarPx
    val expandedPx = with(density) { SettingsExpandedHeaderHeight.toPx() }
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

        // Content
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = headerHeightDp + 8.dp)
                .padding(horizontal = Spacing.medium)
                .padding(bottom = Spacing.extraLarge)
        ) {
            Spacer(Modifier.height(Spacing.small))

            // ── General Section ──────────────────────────────────────────
            SettingsSectionTitle("General")
            Spacer(Modifier.height(Spacing.small))
            SettingsMenuGroup(
                items = listOf(
                    SettingsMenuItem(
                        icon = Icons.Outlined.Person,
                        title = "Edit Profile",
                        subtitle = "Update your personal information",
                        color = PrimaryTeal,
                        onClick = onEditProfileClick
                    ),
                    SettingsMenuItem(
                        icon = Icons.Outlined.Notifications,
                        title = "Notifications",
                        subtitle = "Push notifications, sounds, badges",
                        color = AccentOrange,
                        onClick = onNotificationsClick
                    ),
                    SettingsMenuItem(
                        icon = Icons.Outlined.Lock,
                        title = "Privacy & Security",
                        subtitle = "Password, 2FA, data privacy",
                        color = InfoBlue,
                        onClick = onPrivacySecurityClick
                    )
                )
            )

            Spacer(Modifier.height(Spacing.large))

            // ── Appearance Section ───────────────────────────────────────
            SettingsSectionTitle("Appearance")
            Spacer(Modifier.height(Spacing.small))

            var isDarkTheme by remember { mutableStateOf(false) }

            SettingsMenuGroup(
                items = listOf(
                    SettingsMenuItem(
                        icon = Icons.Outlined.Place,
                        title = "Language",
                        subtitle = "English",
                        color = VerifiedBadge,
                        onClick = onLanguageClick
                    ),
                    SettingsMenuItem(
                        icon = Icons.Outlined.Info,
                        title = "Theme",
                        subtitle = if (isDarkTheme) "Dark" else "Light",
                        color = PrimaryTealDark,
                        hasToggle = true,
                        isToggled = isDarkTheme,
                        onToggle = { isDarkTheme = it }
                    ),
                    SettingsMenuItem(
                        icon = Icons.Outlined.LocationOn,
                        title = "Location",
                        subtitle = "Dhaka, Bangladesh",
                        color = SecondaryGreen
                    )
                )
            )

            Spacer(Modifier.height(Spacing.large))

            // ── Content Section ──────────────────────────────────────────
            SettingsSectionTitle("Content")
            Spacer(Modifier.height(Spacing.small))

            var autoplay by remember { mutableStateOf(true) }
            var downloadOverWifi by remember { mutableStateOf(true) }

            SettingsMenuGroup(
                items = listOf(
                    SettingsMenuItem(
                        icon = Icons.Outlined.PlayArrow,
                        title = "Auto-play Audio",
                        subtitle = "Auto-play event audio previews",
                        color = PrimaryTeal,
                        hasToggle = true,
                        isToggled = autoplay,
                        onToggle = { autoplay = it }
                    ),
                    SettingsMenuItem(
                        icon = Icons.Filled.Info,
                        title = "Download over Wi-Fi only",
                        subtitle = "Save mobile data",
                        color = InfoBlue,
                        hasToggle = true,
                        isToggled = downloadOverWifi,
                        onToggle = { downloadOverWifi = it }
                    )
                )
            )

            Spacer(Modifier.height(Spacing.large))

            // ── Support Section ──────────────────────────────────────────
            SettingsSectionTitle("Support")
            Spacer(Modifier.height(Spacing.small))
            SettingsMenuGroup(
                items = listOf(
                    SettingsMenuItem(
                        icon = Icons.Outlined.Info,
                        title = "About",
                        subtitle = "About MahfilHub v1.0",
                        color = PrimaryTeal,
                        onClick = onAboutClick
                    ),
                    SettingsMenuItem(
                        icon = Icons.Outlined.Email,
                        title = "Help & Feedback",
                        subtitle = "Contact us, report issues",
                        color = InfoBlue,
                        onClick = onHelpFeedbackClick
                    ),
                    SettingsMenuItem(
                        icon = Icons.Outlined.Share,
                        title = "Share App",
                        subtitle = "Invite friends to MahfilHub",
                        color = AccentOrange
                    )
                )
            )

            Spacer(Modifier.height(Spacing.large))

            // ── Data & Storage ───────────────────────────────────────────
            SettingsSectionTitle("Data & Storage")
            Spacer(Modifier.height(Spacing.small))
            SettingsMenuGroup(
                items = listOf(
                    SettingsMenuItem(
                        icon = Icons.Outlined.Delete,
                        title = "Clear Cache",
                        subtitle = "Free up storage space",
                        color = ErrorRed
                    ),
                    SettingsMenuItem(
                        icon = Icons.Outlined.Refresh,
                        title = "Sync Data",
                        subtitle = "Last synced: Today, 10:30 AM",
                        color = SecondaryGreen
                    )
                )
            )

            Spacer(Modifier.height(Spacing.large))

            // App version footer
            Text(
                text = "MahfilHub v1.0.0 (Build 2026.04.01)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // ── Collapsing Header ────────────────────────────────────────────
        Box(
            Modifier
                .fillMaxWidth()
                .height(headerHeightDp)
                .background(Brush.verticalGradient(listOf(PrimaryTeal, PrimaryTealDark)))
        ) {
            Canvas(
                Modifier
                    .matchParentSize()
                    .graphicsLayer { alpha = 1f - collapseProgress }
            ) {
                drawCircle(Color.White.copy(alpha = 0.06f), 120f, Offset(size.width * 0.85f, size.height * 0.2f))
                drawCircle(Color.White.copy(alpha = 0.04f), 80f, Offset(size.width * 0.1f, size.height * 0.8f))
            }

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
                    Icons.AutoMirrored.Filled.ArrowBack,
                    "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            val titleFontSize = lerp(22f, 18f, collapseProgress).sp
            val titleStartPadding = lerp(16f, 56f, collapseProgress).dp
            val titleTopPadding = with(density) {
                lerp(statusBarPx + 70.dp.toPx(), statusBarPx + 18.dp.toPx(), collapseProgress).toDp()
            }

            Text(
                "Settings",
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(start = titleStartPadding, top = titleTopPadding)
            )
            Text(
                "Customize your experience",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier
                    .padding(start = 16.dp, top = titleTopPadding + 30.dp)
                    .graphicsLayer { alpha = (1f - collapseProgress * 2f).coerceIn(0f, 1f) }
            )

            // Settings chip
            Row(
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 16.dp)
                    .graphicsLayer { alpha = (1f - collapseProgress * 2.5f).coerceIn(0f, 1f) },
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    Row(
                        Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Filled.Settings, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Text(
                            "App Settings",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Settings Section Title
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 0.5.sp
    )
}

// ══════════════════════════════════════════════════════════════════════════
// Settings Menu
// ══════════════════════════════════════════════════════════════════════════

private data class SettingsMenuItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val color: Color,
    val onClick: (() -> Unit)? = null,
    val hasToggle: Boolean = false,
    val isToggled: Boolean = false,
    val onToggle: ((Boolean) -> Unit)? = null
)

@Composable
private fun SettingsMenuGroup(items: List<SettingsMenuItem>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Column {
            items.forEachIndexed { index, item ->
                SettingsMenuRow(item = item)
                if (index < items.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 68.dp, end = Spacing.medium),
                        thickness = 0.5.dp,
                        color = DividerLight
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsMenuRow(item: SettingsMenuItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (item.onClick != null && !item.hasToggle) {
                    Modifier.clickable { item.onClick.invoke() }
                } else {
                    Modifier
                }
            )
            .padding(horizontal = Spacing.medium, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(item.color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = item.color,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(Spacing.medium))

        // Text content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Toggle or chevron
        if (item.hasToggle) {
            Switch(
                checked = item.isToggled,
                onCheckedChange = item.onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryTeal,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .height(24.dp)
                    .padding(start = Spacing.small)
            )
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
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
private fun SettingsScreenPreview() {
    MahfilHubTheme { SettingsScreen() }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SettingsScreenDarkPreview() {
    MahfilHubTheme(darkTheme = true) { SettingsScreen() }
}
