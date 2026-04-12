package com.maeen.mahfilhub.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maeen.mahfilhub.R
import com.maeen.mahfilhub.ui.theme.*

// ──────────────────────────────────────────────────────────────────────────
// Profile Screen
// ──────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onNotificationsClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onPrivacySecurityClick: () -> Unit = {},
    onSavedEventsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = Spacing.medium)
    ) {
        // ── Profile Header ───────────────────────────────────────────
        item {
            ProfileHeader()
        }

        // ── Stats Row ────────────────────────────────────────────────
        item {
            ProfileStatsRow()
        }

        // ── Account Section ──────────────────────────────────────────
        item {
            ProfileSectionTitle("Account")
        }
        item {
            ProfileMenuGroup(
                items = listOf(
                    ProfileMenuItem(
                        icon = Icons.Outlined.Person,
                        title = "Edit Profile",
                        subtitle = "Update your information",
                        color = PrimaryTeal,
                        onClick = onEditProfileClick
                    ),
                    ProfileMenuItem(
                        icon = Icons.Outlined.Notifications,
                        title = stringResource(R.string.settings_notifications),
                        subtitle = "Manage notification preferences",
                        color = AccentOrange,
                        onClick = onNotificationsClick
                    ),
                    ProfileMenuItem(
                        icon = Icons.Outlined.Lock,
                        title = "Privacy & Security",
                        subtitle = "Password, account security",
                        color = InfoBlue,
                        onClick = onPrivacySecurityClick
                    )
                )
            )
        }

        // ── Activity Section ─────────────────────────────────────────
        item {
            ProfileSectionTitle("My Activity")
        }
        item {
            ProfileMenuGroup(
                items = listOf(
                    ProfileMenuItem(
                        icon = Icons.Outlined.FavoriteBorder,
                        title = "Saved Events",
                        subtitle = "12 events saved",
                        color = ErrorRed,
                        badge = "12",
                        onClick = onSavedEventsClick
                    ),
                    ProfileMenuItem(
                        icon = Icons.Outlined.Person,
                        title = "Following",
                        subtitle = "8 scholars followed",
                        color = PrimaryTeal,
                        badge = "8"
                    ),
                    ProfileMenuItem(
                        icon = Icons.Filled.DateRange,
                        title = "My Reminders",
                        subtitle = "3 upcoming reminders",
                        color = SecondaryGreen,
                        badge = "3"
                    ),
                    ProfileMenuItem(
                        icon = Icons.Filled.Star,
                        title = "Event History",
                        subtitle = "Events you attended",
                        color = AccentOrange
                    )
                )
            )
        }

        // ── Preferences Section ──────────────────────────────────────
        item {
            ProfileSectionTitle("Preferences")
        }
        item {
            ProfileMenuGroup(
                items = listOf(
                    ProfileMenuItem(
                        icon = Icons.Outlined.Place,
                        title = stringResource(R.string.settings_language),
                        subtitle = "English",
                        color = VerifiedBadge
                    ),
                    ProfileMenuItem(
                        icon = Icons.Outlined.Info,
                        title = stringResource(R.string.settings_theme),
                        subtitle = "System default",
                        color = PrimaryTealDark
                    ),
                    ProfileMenuItem(
                        icon = Icons.Outlined.LocationOn,
                        title = "Location",
                        subtitle = "Dhaka, Bangladesh",
                        color = SecondaryGreen
                    )
                )
            )
        }

        // ── Support Section ──────────────────────────────────────────
        item {
            ProfileSectionTitle("Support")
        }
        item {
            ProfileMenuGroup(
                items = listOf(
                    ProfileMenuItem(
                        icon = Icons.Outlined.Info,
                        title = stringResource(R.string.settings_about),
                        subtitle = "About MahfilHub v1.0",
                        color = PrimaryTeal
                    ),
                    ProfileMenuItem(
                        icon = Icons.Outlined.Email,
                        title = "Help & Feedback",
                        subtitle = "Contact us, report issues",
                        color = InfoBlue
                    ),
                    ProfileMenuItem(
                        icon = Icons.Outlined.Share,
                        title = "Share App",
                        subtitle = "Invite friends to MahfilHub",
                        color = AccentOrange
                    )
                )
            )
        }

        // ── Logout Button ────────────────────────────────────────────
        item {
            LogoutButton(onLogoutClick = onLogoutClick)
        }

        // ── App Version ──────────────────────────────────────────────
        item {
            Text(
                text = "MahfilHub v1.0.0",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.medium),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Profile Header
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun ProfileHeader() {
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
                radius = 130f,
                center = Offset(size.width * 0.85f, size.height * 0.2f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.04f),
                radius = 90f,
                center = Offset(size.width * 0.15f, size.height * 0.8f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.03f),
                radius = 60f,
                center = Offset(size.width * 0.5f, size.height * 0.05f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = Spacing.medium)
                .padding(top = Spacing.medium, bottom = Spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Settings icon (top-right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.nav_profile),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = stringResource(R.string.nav_settings),
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.large))

            // Avatar
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(PrimaryTealLight, PrimaryTeal)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Camera edit badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-2).dp, y = (-2).dp)
                        .size(28.dp)
                        .shadow(2.dp, CircleShape)
                        .clip(CircleShape)
                        .background(AccentOrange),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit photo",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.medium))

            // Name & email
            Text(
                text = "Abdullah Ahmed",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(Spacing.extraSmall))
            Text(
                text = "abdullah.ahmed@email.com",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(Spacing.small))

            // Member badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = AccentOrange.copy(alpha = 0.2f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = AccentOrange,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Premium Member",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AccentOrange
                    )
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Stats Row
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun ProfileStatsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium, vertical = Spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
    ) {
        ProfileStatCard(
            value = "24",
            label = "Attended",
            icon = Icons.Filled.Check,
            color = SuccessGreen,
            modifier = Modifier.weight(1f)
        )
        ProfileStatCard(
            value = "12",
            label = "Saved",
            icon = Icons.Filled.Favorite,
            color = ErrorRed,
            modifier = Modifier.weight(1f)
        )
        ProfileStatCard(
            value = "8",
            label = "Following",
            icon = Icons.Filled.Person,
            color = InfoBlue,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ProfileStatCard(
    value: String,
    label: String,
    icon: ImageVector,
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
// Section Title
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium)
            .padding(top = Spacing.medium, bottom = Spacing.small),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 0.5.sp
    )
}

// ══════════════════════════════════════════════════════════════════════════
// Menu Group
// ══════════════════════════════════════════════════════════════════════════

private data class ProfileMenuItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val color: Color,
    val badge: String? = null,
    val onClick: (() -> Unit)? = null
)

@Composable
private fun ProfileMenuGroup(items: List<ProfileMenuItem>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Column {
            items.forEachIndexed { index, item ->
                ProfileMenuRow(item = item)
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
private fun ProfileMenuRow(item: ProfileMenuItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { item.onClick?.invoke() }
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

        // Badge or chevron
        if (item.badge != null) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = item.color.copy(alpha = 0.12f)
            ) {
                Text(
                    text = item.badge,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = item.color
                )
            }
            Spacer(modifier = Modifier.width(Spacing.small))
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Logout Button
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun LogoutButton(onLogoutClick: () -> Unit = {}) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(
                    text = "Logout",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Are you sure you want to logout? You'll need to sign in again to access your account.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        onLogoutClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    OutlinedButton(
        onClick = { showDialog = true },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium, vertical = Spacing.medium)
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
            brush = SolidColor(ErrorRed.copy(alpha = 0.5f))
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = ErrorRed
        )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(Spacing.small))
        Text(
            text = stringResource(R.string.auth_logout),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Previews
// ══════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    MahfilHubTheme {
        ProfileScreen()
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ProfileScreenDarkPreview() {
    MahfilHubTheme(darkTheme = true) {
        ProfileScreen()
    }
}
