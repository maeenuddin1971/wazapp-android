package com.maeen.mahfilhub.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maeen.mahfilhub.R
import com.maeen.mahfilhub.ui.animation.SplashAnimations
import com.maeen.mahfilhub.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

/**
 * Custom Compose Splash Screen with animated logo, branding, and decorative elements.
 * Auto-transitions after [SplashAnimations.SPLASH_DISPLAY_DURATION] ms.
 */
@Composable
fun SplashScreen(
    onTimeout: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Auto-navigate after display duration
    LaunchedEffect(Unit) {
        delay(SplashAnimations.SPLASH_DISPLAY_DURATION)
        onTimeout()
    }

    // --- Entrance animations ---
    var animStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animStarted = true }

    val logoScale by animateFloatAsState(
        targetValue = if (animStarted) 1f else 0.3f,
        animationSpec = SplashAnimations.logoScaleSpec(),
        label = "logo_scale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (animStarted) 1f else 0f,
        animationSpec = SplashAnimations.logoAlphaSpec(),
        label = "logo_alpha"
    )
    val brandingAlpha by animateFloatAsState(
        targetValue = if (animStarted) 1f else 0f,
        animationSpec = SplashAnimations.brandingAlphaSpec(),
        label = "branding_alpha"
    )
    val brandingOffset by animateFloatAsState(
        targetValue = if (animStarted) 0f else 30f,
        animationSpec = SplashAnimations.brandingSlideSpec(),
        label = "branding_offset"
    )

    // --- Continuous animations ---
    val glowPulse by SplashAnimations.rememberGlowPulse()
    val logoFloat by SplashAnimations.rememberLogoFloat()

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
            ),
        contentAlignment = Alignment.Center
    ) {
        // Background decorations
        SplashBackgroundDecorations()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo with glow
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .offset(y = logoFloat.dp),
                contentAlignment = Alignment.Center
            ) {
                // Glow circle
                Canvas(
                    modifier = Modifier
                        .size((140 * glowPulse).dp)
                        .alpha(logoAlpha * 0.6f)
                ) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.White.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        ),
                        radius = size.minDimension / 2
                    )
                }

                // Mosque logo
                SplashMosqueLogo(
                    modifier = Modifier
                        .size(120.dp)
                        .graphicsLayer {
                            scaleX = logoScale
                            scaleY = logoScale
                            alpha = logoAlpha
                        }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App name
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 2.sp,
                modifier = Modifier
                    .alpha(brandingAlpha)
                    .offset(y = brandingOffset.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text = stringResource(R.string.splash_tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier
                    .alpha(brandingAlpha)
                    .offset(y = brandingOffset.dp)
            )
        }
    }
}

@Composable
private fun SplashMosqueLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val centerX = w / 2
        val white = Color.White
        val orange = AccentOrange

        // Central dome
        val domePath = Path().apply {
            moveTo(centerX - w * 0.25f, h * 0.52f)
            cubicTo(
                centerX - w * 0.25f, h * 0.18f,
                centerX + w * 0.25f, h * 0.18f,
                centerX + w * 0.25f, h * 0.52f
            )
            close()
        }
        drawPath(domePath, white)

        // Left minaret
        drawRect(
            color = white,
            topLeft = Offset(centerX - w * 0.38f, h * 0.25f),
            size = androidx.compose.ui.geometry.Size(w * 0.07f, h * 0.45f)
        )
        // Right minaret
        drawRect(
            color = white,
            topLeft = Offset(centerX + w * 0.31f, h * 0.25f),
            size = androidx.compose.ui.geometry.Size(w * 0.07f, h * 0.45f)
        )

        // Base
        drawRect(
            color = white,
            topLeft = Offset(centerX - w * 0.42f, h * 0.68f),
            size = androidx.compose.ui.geometry.Size(w * 0.84f, h * 0.08f)
        )

        // Crescent on dome
        drawCircle(orange, w * 0.045f, Offset(centerX, h * 0.14f))

        // Minaret tops
        drawCircle(orange, w * 0.035f, Offset(centerX - w * 0.345f, h * 0.22f))
        drawCircle(orange, w * 0.035f, Offset(centerX + w * 0.345f, h * 0.22f))

        // Decorative arch
        val archPath = Path().apply {
            moveTo(centerX - w * 0.1f, h * 0.52f)
            cubicTo(
                centerX - w * 0.1f, h * 0.35f,
                centerX + w * 0.1f, h * 0.35f,
                centerX + w * 0.1f, h * 0.52f
            )
        }
        drawPath(archPath, orange.copy(alpha = 0.4f), style = Stroke(width = 3f))
    }
}

@Composable
private fun SplashBackgroundDecorations() {
    val infiniteTransition = rememberInfiniteTransition(label = "splash_bg")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(80000, easing = LinearEasing)
        ),
        label = "bg_rotation"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Top-right star
        rotate(rotation * 0.05f, pivot = Offset(w * 0.85f, h * 0.1f)) {
            drawIslamicStar(Offset(w * 0.85f, h * 0.1f), 70f, Color.White.copy(alpha = 0.06f))
        }

        // Bottom-left star
        rotate(-rotation * 0.04f, pivot = Offset(w * 0.12f, h * 0.88f)) {
            drawIslamicStar(Offset(w * 0.12f, h * 0.88f), 55f, Color.White.copy(alpha = 0.05f))
        }

        // Scattered circles
        drawCircle(Color.White.copy(alpha = 0.04f), 90f, Offset(w * 0.08f, h * 0.35f))
        drawCircle(Color.White.copy(alpha = 0.03f), 130f, Offset(w * 0.92f, h * 0.65f))
        drawCircle(Color.White.copy(alpha = 0.04f), 60f, Offset(w * 0.5f, h * 0.06f))
        drawCircle(Color.White.copy(alpha = 0.03f), 45f, Offset(w * 0.7f, h * 0.92f))
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
