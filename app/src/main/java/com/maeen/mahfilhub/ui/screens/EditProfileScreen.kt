package com.maeen.mahfilhub.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.maeen.mahfilhub.ui.theme.*

// ══════════════════════════════════════════════════════════════════════════
// Constants
// ══════════════════════════════════════════════════════════════════════════

private val ExpandedHeaderHeight = 260.dp
private val ToolbarHeight = 56.dp

// ══════════════════════════════════════════════════════════════════════════
// Edit Profile Screen — Collapsing Header
// ══════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    // Form state
    var fullName by remember { mutableStateOf("Abdullah Ahmed") }
    var email by remember { mutableStateOf("abdullah.ahmed@email.com") }
    var phone by remember { mutableStateOf("+880 1712-345678") }
    var location by remember { mutableStateOf("Dhaka, Bangladesh") }
    var bio by remember { mutableStateOf("A passionate follower of Islamic knowledge. Love attending mahfils and connecting with scholars.") }
    var isSaving by remember { mutableStateOf(false) }

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
        // ── Scrollable Form ───────────────────────────────────────────
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

            // ── Personal Information Section ──────────────────────────
            SectionLabel(text = "Personal Information")
            Spacer(modifier = Modifier.height(Spacing.small))

            EditProfileField(
                value = fullName,
                onValueChange = { fullName = it },
                label = "Full Name",
                icon = Icons.Outlined.Person,
                iconColor = PrimaryTeal
            )

            EditProfileField(
                value = email,
                onValueChange = { email = it },
                label = "Email Address",
                icon = Icons.Outlined.Email,
                iconColor = InfoBlue,
                keyboardType = KeyboardType.Email
            )

            EditProfileField(
                value = phone,
                onValueChange = { phone = it },
                label = "Phone Number",
                icon = Icons.Outlined.Phone,
                iconColor = SecondaryGreen,
                keyboardType = KeyboardType.Phone
            )

            EditProfileField(
                value = location,
                onValueChange = { location = it },
                label = "Location",
                icon = Icons.Outlined.LocationOn,
                iconColor = AccentOrange
            )

            Spacer(modifier = Modifier.height(Spacing.large))

            // ── Bio Section ──────────────────────────────────────────
            SectionLabel(text = "About Me")
            Spacer(modifier = Modifier.height(Spacing.small))

            EditProfileField(
                value = bio,
                onValueChange = { bio = it },
                label = "Bio",
                icon = Icons.Outlined.Info,
                iconColor = PrimaryTealDark,
                singleLine = false,
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(Spacing.large))

            // ── Preferences Section ──────────────────────────────────
            SectionLabel(text = "Preferences")
            Spacer(modifier = Modifier.height(Spacing.small))

            PreferenceToggleRow(
                icon = Icons.Outlined.Notifications,
                title = "Email Notifications",
                subtitle = "Receive event updates via email",
                color = AccentOrange,
                initialChecked = true
            )

            PreferenceToggleRow(
                icon = Icons.Outlined.Place,
                title = "Location Services",
                subtitle = "Show nearby events & mosques",
                color = SecondaryGreen,
                initialChecked = true
            )

            PreferenceToggleRow(
                icon = Icons.Outlined.Visibility,
                title = "Public Profile",
                subtitle = "Let others see your profile",
                color = InfoBlue,
                initialChecked = false
            )

            Spacer(modifier = Modifier.height(Spacing.extraLarge))

            // ── Save Button ──────────────────────────────────────────
            Button(
                onClick = {
                    isSaving = true
                    // Simulate save
                    onSave()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryTeal
                ),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(Spacing.small))
                }
                Text(
                    text = if (isSaving) "Saving…" else "Save Changes",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(Spacing.medium))

            // ── Delete Account ────────────────────────────────────────
            OutlinedButton(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = androidx.compose.ui.graphics.SolidColor(ErrorRed.copy(alpha = 0.4f))
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ErrorRed
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.small))
                Text(
                    text = "Delete Account",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // ── Collapsing Header ─────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeightDp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PrimaryTeal, PrimaryTealDark)
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
                    radius = 130f,
                    center = Offset(size.width * 0.85f, size.height * 0.15f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.04f),
                    radius = 90f,
                    center = Offset(size.width * 0.1f, size.height * 0.85f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.03f),
                    radius = 60f,
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
                val expandedTop = statusBarPx + with(density) { 90.dp.toPx() }
                val collapsedTop = statusBarPx + with(density) { 18.dp.toPx() }
                lerp(expandedTop, collapsedTop, collapseProgress).toDp()
            }

            Text(
                text = "Edit Profile",
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .padding(start = titleStartPadding, top = titleTopPadding)
            )

            // ── Avatar (fades out on collapse) ──────────────────────
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .graphicsLayer { alpha = (1f - collapseProgress * 2.5f).coerceIn(0f, 1f) }
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .shadow(4.dp, CircleShape)
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
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    // Camera edit badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = (-2).dp, y = (-2).dp)
                            .size(26.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(AccentOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Change photo",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Components
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun SectionLabel(text: String) {
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
private fun EditProfileField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    iconColor: Color,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    maxLines: Int = 1
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall
                )
            },
            leadingIcon = {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            maxLines = maxLines,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryTeal,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                cursorColor = PrimaryTeal
            )
        )
    }
}

@Composable
private fun PreferenceToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    initialChecked: Boolean = false
) {
    var checked by remember { mutableStateOf(initialChecked) }

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
                onCheckedChange = { checked = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryTeal,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Previews
// ══════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun EditProfileScreenPreview() {
    MahfilHubTheme {
        EditProfileScreen()
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun EditProfileScreenDarkPreview() {
    MahfilHubTheme(darkTheme = true) {
        EditProfileScreen()
    }
}
