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

// ══════════════════════════════════════════════════════════════════════════
// Constants
// ══════════════════════════════════════════════════════════════════════════

private val ExpandedHeaderHeight = 220.dp
private val ToolbarHeight = 56.dp

// ══════════════════════════════════════════════════════════════════════════
// Privacy & Security Screen — Collapsing Header
// ══════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySecurityScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    // Toggle states
    var biometricLock by remember { mutableStateOf(false) }
    var twoFactorAuth by remember { mutableStateOf(true) }
    var loginAlerts by remember { mutableStateOf(true) }
    var showOnlineStatus by remember { mutableStateOf(true) }
    var showLastSeen by remember { mutableStateOf(false) }
    var showProfilePhoto by remember { mutableStateOf(true) }
    var dataSharing by remember { mutableStateOf(false) }
    var personalizedAds by remember { mutableStateOf(false) }

    // ── Collapsing header state ────────────────────────────────────────
    val density = LocalDensity.current
    val statusBarPx = WindowInsets.statusBars.getTop(density).toFloat()
    val toolbarPx = with(density) { ToolbarHeight.toPx() }
    val collapsedPx = statusBarPx + toolbarPx
    val expandedPx = with(density) { ExpandedHeaderHeight.toPx() }
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
        // ── Scrollable Content ────────────────────────────────────────
        val headerHeightDp = with(density) { (expandedPx - headerOffset).toDp() }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = headerHeightDp + 8.dp)
                .padding(horizontal = Spacing.medium)
                .padding(bottom = Spacing.extraLarge)
        ) {
            Spacer(modifier = Modifier.height(Spacing.medium))

            // ── Security Section ──────────────────────────────────────
            SecuritySectionLabel(text = "Security")
            Spacer(modifier = Modifier.height(Spacing.small))

            SecurityActionRow(
                icon = Icons.Outlined.Lock,
                title = "Change Password",
                subtitle = "Last changed 30 days ago",
                color = InfoBlue,
                onClick = {}
            )

            SecurityToggleRow(
                icon = Icons.Outlined.Fingerprint,
                title = "Biometric Lock",
                subtitle = "Use fingerprint or face to unlock",
                color = PrimaryTeal,
                checked = biometricLock,
                onCheckedChange = { biometricLock = it }
            )

            SecurityToggleRow(
                icon = Icons.Outlined.Shield,
                title = "Two-Factor Authentication",
                subtitle = "Extra security for your account",
                color = SecondaryGreen,
                checked = twoFactorAuth,
                onCheckedChange = { twoFactorAuth = it }
            )

            SecurityToggleRow(
                icon = Icons.Outlined.NotificationsActive,
                title = "Login Alerts",
                subtitle = "Get notified of new sign-ins",
                color = AccentOrange,
                checked = loginAlerts,
                onCheckedChange = { loginAlerts = it }
            )

            Spacer(modifier = Modifier.height(Spacing.large))

            // ── Privacy Section ───────────────────────────────────────
            SecuritySectionLabel(text = "Privacy")
            Spacer(modifier = Modifier.height(Spacing.small))

            SecurityToggleRow(
                icon = Icons.Outlined.Visibility,
                title = "Online Status",
                subtitle = "Show when you're active",
                color = SecondaryGreen,
                checked = showOnlineStatus,
                onCheckedChange = { showOnlineStatus = it }
            )

            SecurityToggleRow(
                icon = Icons.Outlined.Schedule,
                title = "Last Seen",
                subtitle = "Show your last active time",
                color = InfoBlue,
                checked = showLastSeen,
                onCheckedChange = { showLastSeen = it }
            )

            SecurityToggleRow(
                icon = Icons.Outlined.AccountCircle,
                title = "Profile Photo Visibility",
                subtitle = "Let others see your photo",
                color = PrimaryTeal,
                checked = showProfilePhoto,
                onCheckedChange = { showProfilePhoto = it }
            )

            Spacer(modifier = Modifier.height(Spacing.large))

            // ── Data & Permissions Section ────────────────────────────
            SecuritySectionLabel(text = "Data & Permissions")
            Spacer(modifier = Modifier.height(Spacing.small))

            SecurityToggleRow(
                icon = Icons.Outlined.Share,
                title = "Data Sharing",
                subtitle = "Share usage data to improve app",
                color = AccentOrange,
                checked = dataSharing,
                onCheckedChange = { dataSharing = it }
            )

            SecurityToggleRow(
                icon = Icons.Outlined.Campaign,
                title = "Personalized Content",
                subtitle = "See tailored event recommendations",
                color = PrimaryTealDark,
                checked = personalizedAds,
                onCheckedChange = { personalizedAds = it }
            )

            SecurityActionRow(
                icon = Icons.Outlined.Storage,
                title = "Download My Data",
                subtitle = "Get a copy of your personal data",
                color = InfoBlue,
                onClick = {}
            )

            SecurityActionRow(
                icon = Icons.Outlined.DeleteSweep,
                title = "Clear App Data",
                subtitle = "Remove cached files and preferences",
                color = ErrorRed,
                onClick = {}
            )

            Spacer(modifier = Modifier.height(Spacing.large))

            // ── Sessions Section ──────────────────────────────────────
            SecuritySectionLabel(text = "Active Sessions")
            Spacer(modifier = Modifier.height(Spacing.small))

            ActiveSessionCard(
                device = "Samsung Galaxy S24",
                location = "Dhaka, Bangladesh",
                lastActive = "Active now",
                isCurrent = true
            )

            ActiveSessionCard(
                device = "Chrome on MacOS",
                location = "Dhaka, Bangladesh",
                lastActive = "2 hours ago",
                isCurrent = false
            )

            Spacer(modifier = Modifier.height(Spacing.small))

            OutlinedButton(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = androidx.compose.ui.graphics.SolidColor(ErrorRed.copy(alpha = 0.4f))
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ErrorRed
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.small))
                Text(
                    text = "Sign Out All Other Sessions",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(Spacing.extraLarge))
        }

        // ── Collapsing Header ─────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeightDp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(InfoBlue, Color(0xFF1A3A5C))
                    )
                )
        ) {
            // Decorative circles
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer { alpha = 1f - collapseProgress }
            ) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.06f),
                    radius = 120f,
                    center = Offset(size.width * 0.85f, size.height * 0.2f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.04f),
                    radius = 80f,
                    center = Offset(size.width * 0.1f, size.height * 0.8f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.03f),
                    radius = 50f,
                    center = Offset(size.width * 0.5f, size.height * 0.05f)
                )
            }

            // ── Back button ─────────────────────────────────────────
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

            // ── Animated Title ──────────────────────────────────────
            val titleFontSize = lerp(22f, 18f, collapseProgress).sp
            val titleStartPadding = lerp(16f, 56f, collapseProgress).dp
            val titleTopPadding = with(density) {
                val expandedTop = statusBarPx + with(density) { 70.dp.toPx() }
                val collapsedTop = statusBarPx + with(density) { 18.dp.toPx() }
                lerp(expandedTop, collapsedTop, collapseProgress).toDp()
            }

            Text(
                text = "Privacy & Security",
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .padding(start = titleStartPadding, top = titleTopPadding)
            )

            // ── Subtitle (fades on collapse) ────────────────────────
            Text(
                text = "Manage your account security and privacy settings",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier
                    .padding(start = 16.dp, top = titleTopPadding + 30.dp)
                    .graphicsLayer { alpha = (1f - collapseProgress * 2f).coerceIn(0f, 1f) }
            )

            // ── Shield Icon (fades on collapse) ─────────────────────
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 16.dp)
                    .graphicsLayer { alpha = (1f - collapseProgress * 2.5f).coerceIn(0f, 1f) }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier.size(80.dp)
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Components
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun SecuritySectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
private fun SecurityToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.medium, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(Spacing.medium))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = color,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

@Composable
private fun SecurityActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.medium, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(Spacing.medium))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ActiveSessionCard(
    device: String,
    location: String,
    lastActive: String,
    isCurrent: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.medium, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isCurrent) SecondaryGreen.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (device.contains("Chrome")) Icons.Outlined.Computer
                    else Icons.Outlined.PhoneAndroid,
                    contentDescription = null,
                    tint = if (isCurrent) SecondaryGreen
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(Spacing.medium))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = device,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isCurrent) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Current",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = SecondaryGreen,
                            modifier = Modifier
                                .background(
                                    SecondaryGreen.copy(alpha = 0.1f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "$location · $lastActive",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Previews
// ══════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun PrivacySecurityScreenPreview() {
    MahfilHubTheme {
        PrivacySecurityScreen()
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PrivacySecurityScreenDarkPreview() {
    MahfilHubTheme(darkTheme = true) {
        PrivacySecurityScreen()
    }
}
