package com.maeen.mahfilhub.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.maeen.mahfilhub.data.model.ReminderItem
import com.maeen.mahfilhub.ui.theme.*
import com.maeen.mahfilhub.ui.viewmodel.MyRemindersViewModel
import kotlinx.coroutines.delay

private val RemindersExpandedHeaderHeight = 220.dp
private val RemindersToolbarHeight = 56.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRemindersScreen(
    modifier: Modifier = Modifier,
    myRemindersViewModel: MyRemindersViewModel = viewModel(),
    onBack: () -> Unit = {},
    onEventClick: (Int) -> Unit = {}
) {
    val state by myRemindersViewModel.uiState.collectAsState()
    val density = LocalDensity.current
    val statusBarPx = WindowInsets.statusBars.getTop(density).toFloat()
    val toolbarPx = with(density) { RemindersToolbarHeight.toPx() }
    val collapsedPx = statusBarPx + toolbarPx
    val expandedPx = with(density) { RemindersExpandedHeaderHeight.toPx() }
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
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(top = headerHeightDp + 8.dp, bottom = Spacing.large)) {
            item {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = Spacing.medium, vertical = Spacing.medium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${state.totalReminders} active reminders",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(SecondaryGreen))
                        Text("All active", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = SecondaryGreen)
                    }
                }
            }
            itemsIndexed(state.reminders, key = { _, item -> item.id }) { index, reminder ->
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { delay(index * 50L); visible = true }
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(300)) + slideInVertically(initialOffsetY = { it / 4 }, animationSpec = tween(300))
                ) {
                    ReminderCard(reminder = reminder, onClick = { onEventClick(reminder.id) })
                }
            }
        }

        // Collapsing Header
        Box(
            Modifier.fillMaxWidth().height(headerHeightDp)
                .background(Brush.verticalGradient(listOf(SecondaryGreen, SecondaryGreenDark)))
        ) {
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
            Text("My Reminders", fontSize = titleFontSize, fontWeight = FontWeight.Bold, color = Color.White,
                modifier = Modifier.padding(start = titleStartPadding, top = titleTopPadding))
            Text("Never miss an important event", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 16.dp, top = titleTopPadding + 30.dp)
                    .graphicsLayer { alpha = (1f - collapseProgress * 2f).coerceIn(0f, 1f) })
            Row(
                Modifier.align(Alignment.BottomStart).padding(start = 16.dp, bottom = 16.dp)
                    .graphicsLayer { alpha = (1f - collapseProgress * 2.5f).coerceIn(0f, 1f) },
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                Surface(shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.15f)) {
                    Row(
                        Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Filled.Notifications, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Text("${state.totalReminders} Reminders", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderCard(reminder: ReminderItem, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.medium, vertical = Spacing.extraSmall)
            .shadow(4.dp, RoundedCornerShape(16.dp), ambientColor = CardShadow, spotColor = CardShadow),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onClick
    ) {
        Column {
            Box(Modifier.fillMaxWidth().height(6.dp).background(Brush.horizontalGradient(listOf(SecondaryGreen, SecondaryGreenDark))))
            Row(Modifier.fillMaxWidth().padding(Spacing.medium), verticalAlignment = Alignment.Top) {
                Surface(shape = RoundedCornerShape(12.dp), color = SecondaryGreen.copy(alpha = 0.1f)) {
                    Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${reminder.daysUntil}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SecondaryGreen)
                        Text("days", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = SecondaryGreen)
                    }
                }
                Spacer(Modifier.width(Spacing.medium))
                Column(Modifier.weight(1f)) {
                    Text(reminder.eventTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Person, null, tint = PrimaryTeal, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(reminder.maulana, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.LocationOn, null, tint = AccentOrange, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(reminder.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.DateRange, null, tint = InfoBlue, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("${reminder.date} \u00b7 ${reminder.time}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.height(Spacing.small))
                    Surface(shape = RoundedCornerShape(8.dp), color = AccentOrange.copy(alpha = 0.12f)) {
                        Row(
                            Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Outlined.Notifications, null, tint = AccentOrange, modifier = Modifier.size(12.dp))
                            Text(reminder.reminderTime, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AccentOrange)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyRemindersScreenPreview() {
    MahfilHubTheme { MyRemindersScreen() }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun MyRemindersScreenDarkPreview() {
    MahfilHubTheme(darkTheme = true) { MyRemindersScreen() }
}
