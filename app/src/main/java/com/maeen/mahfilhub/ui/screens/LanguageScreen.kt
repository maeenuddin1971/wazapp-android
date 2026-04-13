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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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

private data class LanguageOption(val code: String, val name: String, val nativeName: String)

private val languages = listOf(
    LanguageOption("en", "English", "English"),
    LanguageOption("bn", "Bengali", "\u09AC\u09BE\u0982\u09B2\u09BE"),
    LanguageOption("ar", "Arabic", "\u0627\u0644\u0639\u0631\u0628\u064A\u0629"),
    LanguageOption("ur", "Urdu", "\u0627\u0631\u062F\u0648"),
    LanguageOption("hi", "Hindi", "\u0939\u093F\u0928\u094D\u0926\u0940"),
    LanguageOption("ms", "Malay", "Bahasa Melayu"),
    LanguageOption("id", "Indonesian", "Bahasa Indonesia"),
    LanguageOption("tr", "Turkish", "T\u00fcrk\u00e7e")
)

private val LangExpandedHeaderHeight = 200.dp
private val LangToolbarHeight = 56.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    var selectedLang by remember { mutableStateOf("en") }
    val density = LocalDensity.current
    val statusBarPx = WindowInsets.statusBars.getTop(density).toFloat()
    val toolbarPx = with(density) { LangToolbarHeight.toPx() }
    val collapsedPx = statusBarPx + toolbarPx
    val expandedPx = with(density) { LangExpandedHeaderHeight.toPx() }
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
                .padding(top = headerHeightDp + 8.dp)
                .padding(horizontal = Spacing.medium)
                .padding(bottom = Spacing.extraLarge)
        ) {
            Spacer(Modifier.height(Spacing.medium))
            Text("Select Language", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.5.sp)
            Spacer(Modifier.height(Spacing.small))
            languages.forEach { lang ->
                val isSelected = lang.code == selectedLang
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 1.dp
                ) {
                    Row(
                        Modifier.fillMaxWidth().clickable { selectedLang = lang.code }
                            .padding(horizontal = Spacing.medium, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) PrimaryTeal.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(lang.code.uppercase(), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold,
                                color = if (isSelected) PrimaryTeal else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(Modifier.width(Spacing.medium))
                        Column(Modifier.weight(1f)) {
                            Text(lang.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                            Text(lang.nativeName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (isSelected) {
                            Icon(Icons.Filled.Check, "Selected", tint = PrimaryTeal, modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }

        // Collapsing Header
        Box(Modifier.fillMaxWidth().height(headerHeightDp).background(Brush.verticalGradient(listOf(VerifiedBadge, Color(0xFF0D47A1))))) {
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
            Text("Language", fontSize = titleFontSize, fontWeight = FontWeight.Bold, color = Color.White,
                modifier = Modifier.padding(start = titleStartPadding, top = titleTopPadding))
            Text("Choose your preferred language", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 16.dp, top = titleTopPadding + 30.dp)
                    .graphicsLayer { alpha = (1f - collapseProgress * 2f).coerceIn(0f, 1f) })
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LanguageScreenPreview() {
    MahfilHubTheme { LanguageScreen() }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun LanguageScreenDarkPreview() {
    MahfilHubTheme(darkTheme = true) { LanguageScreen() }
}

