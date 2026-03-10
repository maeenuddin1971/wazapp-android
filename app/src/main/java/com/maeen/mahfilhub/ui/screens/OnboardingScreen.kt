package com.maeen.mahfilhub.ui.screens

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maeen.mahfilhub.R
import com.maeen.mahfilhub.ui.theme.*
import com.maeen.mahfilhub.util.LocaleHelper
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

data class OnboardingPage(
    val titleResId: Int,
    val descriptionResId: Int,
    val iconContent: @Composable () -> Unit
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentLang = remember { mutableStateOf(LocaleHelper.getLanguage(context)) }

    val pages = listOf(
        OnboardingPage(
            titleResId = R.string.onboarding_title_1,
            descriptionResId = R.string.onboarding_desc_1,
            iconContent = { OnboardingMosqueIcon() }
        ),
        OnboardingPage(
            titleResId = R.string.onboarding_title_2,
            descriptionResId = R.string.onboarding_desc_2,
            iconContent = { OnboardingCommunityIcon() }
        ),
        OnboardingPage(
            titleResId = R.string.onboarding_title_3,
            descriptionResId = R.string.onboarding_desc_3,
            iconContent = { OnboardingReminderIcon() }
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        PrimaryTeal,
                        PrimaryTealDark,
                        Color(0xFF004D40)
                    )
                )
            )
    ) {
        // Decorative background elements
        OnboardingBackgroundDecorations()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar: Skip (left) + Language toggle (right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.small, vertical = Spacing.small),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Skip button — top left
                if (pagerState.currentPage < pages.size - 1) {
                    TextButton(onClick = onFinished) {
                        Text(
                            text = stringResource(R.string.action_skip),
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(72.dp))
                }

                // Language toggle — top right
                LanguageToggle(
                    currentLanguage = currentLang.value,
                    onToggle = {
                        val newLang = LocaleHelper.toggleLanguage(context)
                        currentLang.value = newLang
                        // Recreate the activity to apply the new locale
                        (context as? Activity)?.recreate()
                    }
                )
            }

            // Pager content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                OnboardingPageContent(
                    page = pages[page],
                    pageIndex = page
                )
            }

            // Bottom section: indicators + buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.extraLarge, vertical = Spacing.large),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.large)
            ) {
                // Page indicators
                PagerIndicator(
                    pageCount = pages.size,
                    currentPage = pagerState.currentPage
                )

                // Action button
                AnimatedContent(
                    targetState = pagerState.currentPage == pages.size - 1,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300))
                    },
                    label = "button_transition"
                ) { isLastPage ->
                    if (isLastPage) {
                        // "Get Started" button on last page
                        Button(
                            onClick = onFinished,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(28.dp),
                                    ambientColor = AccentOrange.copy(alpha = 0.3f),
                                    spotColor = AccentOrange.copy(alpha = 0.3f)
                                ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentOrange,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.onboarding_get_started),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        // "Next" button on other pages
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.2f),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(28.dp),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = false).copy(
                                brush = SolidColor(Color.White.copy(alpha = 0.4f))
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.action_next),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.small))
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    pageIndex: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "page_$pageIndex")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_$pageIndex"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon area with floating animation
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(y = (floatAnim * 12 - 6).dp),
            contentAlignment = Alignment.Center
        ) {
            // Glow circle behind icon
            Canvas(modifier = Modifier.size(200.dp)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.15f),
                            Color.White.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    ),
                    radius = size.minDimension / 2
                )
            }

            // Icon content
            page.iconContent()
        }

        Spacer(modifier = Modifier.height(Spacing.huge))

        // Title
        Text(
            text = stringResource(page.titleResId),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Spacing.medium))

        // Description
        Text(
            text = stringResource(page.descriptionResId),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.medium),
            lineHeight = 24.sp
        )
    }
}

@Composable
private fun PagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            val width by animateDpAsState(
                targetValue = if (isSelected) 32.dp else 10.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "indicator_width"
            )
            val color by animateColorAsState(
                targetValue = if (isSelected) AccentOrange else Color.White.copy(alpha = 0.4f),
                animationSpec = tween(300),
                label = "indicator_color"
            )

            Box(
                modifier = Modifier
                    .height(10.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
private fun OnboardingBackgroundDecorations() {
    val infiniteTransition = rememberInfiniteTransition(label = "bg_decorations")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Large geometric pattern - top right
        rotate(rotation * 0.1f, pivot = Offset(w * 0.85f, h * 0.12f)) {
            drawIslamicStar(
                center = Offset(w * 0.85f, h * 0.12f),
                radius = 60f,
                color = Color.White.copy(alpha = 0.06f)
            )
        }

        // Medium geometric pattern - bottom left
        rotate(-rotation * 0.08f, pivot = Offset(w * 0.15f, h * 0.85f)) {
            drawIslamicStar(
                center = Offset(w * 0.15f, h * 0.85f),
                radius = 45f,
                color = Color.White.copy(alpha = 0.05f)
            )
        }

        // Small circles scattered
        drawCircle(
            color = Color.White.copy(alpha = 0.04f),
            radius = 80f,
            center = Offset(w * 0.1f, h * 0.3f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.03f),
            radius = 120f,
            center = Offset(w * 0.9f, h * 0.6f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.04f),
            radius = 50f,
            center = Offset(w * 0.5f, h * 0.08f)
        )
    }
}

private fun DrawScope.drawIslamicStar(
    center: Offset,
    radius: Float,
    color: Color
) {
    val points = 8
    val path = Path()
    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) radius else radius * 0.5f
        val angle = Math.toRadians((i * 360.0 / (points * 2)) - 90)
        val x = center.x + r * cos(angle).toFloat()
        val y = center.y + r * sin(angle).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color)
    drawPath(path, color.copy(alpha = color.alpha * 0.5f), style = Stroke(width = 2f))
}

// ---- Onboarding Icon Composables ----

@Composable
private fun OnboardingMosqueIcon() {
    Canvas(modifier = Modifier.size(140.dp)) {
        val w = size.width
        val h = size.height
        val centerX = w / 2
        val white = Color.White
        val orange = AccentOrange

        // Main dome
        val domePath = Path().apply {
            moveTo(centerX - w * 0.25f, h * 0.5f)
            cubicTo(
                centerX - w * 0.25f, h * 0.2f,
                centerX + w * 0.25f, h * 0.2f,
                centerX + w * 0.25f, h * 0.5f
            )
            close()
        }
        drawPath(domePath, white.copy(alpha = 0.9f))

        // Left minaret
        drawRect(
            color = white.copy(alpha = 0.85f),
            topLeft = Offset(centerX - w * 0.38f, h * 0.25f),
            size = androidx.compose.ui.geometry.Size(w * 0.06f, h * 0.45f)
        )
        // Right minaret
        drawRect(
            color = white.copy(alpha = 0.85f),
            topLeft = Offset(centerX + w * 0.32f, h * 0.25f),
            size = androidx.compose.ui.geometry.Size(w * 0.06f, h * 0.45f)
        )

        // Base
        drawRect(
            color = white.copy(alpha = 0.9f),
            topLeft = Offset(centerX - w * 0.42f, h * 0.68f),
            size = androidx.compose.ui.geometry.Size(w * 0.84f, h * 0.07f)
        )

        // Crescent on dome
        drawCircle(
            color = orange,
            radius = w * 0.04f,
            center = Offset(centerX, h * 0.16f)
        )

        // Minaret tops
        drawCircle(orange, w * 0.03f, Offset(centerX - w * 0.35f, h * 0.22f))
        drawCircle(orange, w * 0.03f, Offset(centerX + w * 0.35f, h * 0.22f))

        // Decorative arch in dome
        val archPath = Path().apply {
            moveTo(centerX - w * 0.08f, h * 0.5f)
            cubicTo(
                centerX - w * 0.08f, h * 0.35f,
                centerX + w * 0.08f, h * 0.35f,
                centerX + w * 0.08f, h * 0.5f
            )
        }
        drawPath(archPath, orange.copy(alpha = 0.4f), style = Stroke(width = 3f))
    }
}

@Composable
private fun OnboardingCommunityIcon() {
    Canvas(modifier = Modifier.size(140.dp)) {
        val w = size.width
        val h = size.height
        val centerX = w / 2
        val white = Color.White
        val orange = AccentOrange

        // Center person (larger)
        drawCircle(white.copy(alpha = 0.9f), w * 0.08f, Offset(centerX, h * 0.3f))
        drawRoundRect(
            color = white.copy(alpha = 0.85f),
            topLeft = Offset(centerX - w * 0.1f, h * 0.42f),
            size = androidx.compose.ui.geometry.Size(w * 0.2f, h * 0.2f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.05f)
        )

        // Left person
        drawCircle(white.copy(alpha = 0.7f), w * 0.06f, Offset(centerX - w * 0.25f, h * 0.36f))
        drawRoundRect(
            color = white.copy(alpha = 0.65f),
            topLeft = Offset(centerX - w * 0.33f, h * 0.46f),
            size = androidx.compose.ui.geometry.Size(w * 0.16f, h * 0.16f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.04f)
        )

        // Right person
        drawCircle(white.copy(alpha = 0.7f), w * 0.06f, Offset(centerX + w * 0.25f, h * 0.36f))
        drawRoundRect(
            color = white.copy(alpha = 0.65f),
            topLeft = Offset(centerX + w * 0.17f, h * 0.46f),
            size = androidx.compose.ui.geometry.Size(w * 0.16f, h * 0.16f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.04f)
        )

        // Connection lines
        drawLine(
            color = orange.copy(alpha = 0.5f),
            start = Offset(centerX - w * 0.12f, h * 0.4f),
            end = Offset(centerX - w * 0.18f, h * 0.4f),
            strokeWidth = 3f
        )
        drawLine(
            color = orange.copy(alpha = 0.5f),
            start = Offset(centerX + w * 0.12f, h * 0.4f),
            end = Offset(centerX + w * 0.18f, h * 0.4f),
            strokeWidth = 3f
        )

        // Heart / connection symbol
        drawCircle(orange.copy(alpha = 0.6f), w * 0.04f, Offset(centerX, h * 0.68f))
        drawCircle(white.copy(alpha = 0.4f), w * 0.06f, Offset(centerX, h * 0.68f), style = Stroke(2f))
    }
}

@Composable
private fun OnboardingReminderIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "bell")
    val bellSwing by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bell_swing"
    )

    Canvas(modifier = Modifier.size(140.dp)) {
        val w = size.width
        val h = size.height
        val centerX = w / 2
        val white = Color.White
        val orange = AccentOrange

        rotate(bellSwing, pivot = Offset(centerX, h * 0.2f)) {
            // Bell body
            val bellPath = Path().apply {
                moveTo(centerX - w * 0.2f, h * 0.55f)
                cubicTo(
                    centerX - w * 0.2f, h * 0.25f,
                    centerX - w * 0.08f, h * 0.2f,
                    centerX, h * 0.2f
                )
                cubicTo(
                    centerX + w * 0.08f, h * 0.2f,
                    centerX + w * 0.2f, h * 0.25f,
                    centerX + w * 0.2f, h * 0.55f
                )
                lineTo(centerX + w * 0.25f, h * 0.58f)
                lineTo(centerX - w * 0.25f, h * 0.58f)
                close()
            }
            drawPath(bellPath, white.copy(alpha = 0.9f))

            // Bell top
            drawCircle(orange, w * 0.035f, Offset(centerX, h * 0.18f))

            // Bell clapper
            drawCircle(orange.copy(alpha = 0.8f), w * 0.04f, Offset(centerX, h * 0.6f))
        }

        // Sound waves - left
        drawArc(
            color = orange.copy(alpha = 0.4f),
            startAngle = 150f,
            sweepAngle = 60f,
            useCenter = false,
            topLeft = Offset(centerX - w * 0.42f, h * 0.28f),
            size = androidx.compose.ui.geometry.Size(w * 0.18f, h * 0.24f),
            style = Stroke(width = 3f)
        )
        drawArc(
            color = orange.copy(alpha = 0.25f),
            startAngle = 150f,
            sweepAngle = 60f,
            useCenter = false,
            topLeft = Offset(centerX - w * 0.5f, h * 0.22f),
            size = androidx.compose.ui.geometry.Size(w * 0.24f, h * 0.32f),
            style = Stroke(width = 2f)
        )

        // Sound waves - right
        drawArc(
            color = orange.copy(alpha = 0.4f),
            startAngle = -30f,
            sweepAngle = 60f,
            useCenter = false,
            topLeft = Offset(centerX + w * 0.24f, h * 0.28f),
            size = androidx.compose.ui.geometry.Size(w * 0.18f, h * 0.24f),
            style = Stroke(width = 3f)
        )
        drawArc(
            color = orange.copy(alpha = 0.25f),
            startAngle = -30f,
            sweepAngle = 60f,
            useCenter = false,
            topLeft = Offset(centerX + w * 0.26f, h * 0.22f),
            size = androidx.compose.ui.geometry.Size(w * 0.24f, h * 0.32f),
            style = Stroke(width = 2f)
        )

        // Calendar icon below bell
        drawRoundRect(
            color = white.copy(alpha = 0.3f),
            topLeft = Offset(centerX - w * 0.12f, h * 0.7f),
            size = androidx.compose.ui.geometry.Size(w * 0.24f, h * 0.2f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.02f)
        )
        drawRect(
            color = orange.copy(alpha = 0.5f),
            topLeft = Offset(centerX - w * 0.12f, h * 0.7f),
            size = androidx.compose.ui.geometry.Size(w * 0.24f, h * 0.05f)
        )
        // Calendar check mark
        val checkPath = Path().apply {
            moveTo(centerX - w * 0.04f, h * 0.82f)
            lineTo(centerX - w * 0.01f, h * 0.85f)
            lineTo(centerX + w * 0.06f, h * 0.78f)
        }
        drawPath(checkPath, orange.copy(alpha = 0.7f), style = Stroke(width = 3f))
    }
}

// ---- Language Toggle ----

@Composable
private fun LanguageToggle(
    currentLanguage: String,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEnglish = currentLanguage == LocaleHelper.LANG_ENGLISH

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.5.dp,
                color = Color.White.copy(alpha = 0.4f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onToggle() }
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // English option
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isEnglish) AccentOrange else Color.Transparent
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "English",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isEnglish) FontWeight.Bold else FontWeight.Normal,
                color = if (isEnglish) Color.White else Color.White.copy(alpha = 0.6f)
            )
        }

        // Bangla option
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (!isEnglish) AccentOrange else Color.Transparent
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "বাংলা",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (!isEnglish) FontWeight.Bold else FontWeight.Normal,
                color = if (!isEnglish) Color.White else Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

private val EaseInOutSine: Easing = CubicBezierEasing(0.37f, 0f, 0.63f, 1f)
