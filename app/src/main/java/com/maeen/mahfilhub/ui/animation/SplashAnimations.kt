package com.maeen.mahfilhub.ui.animation

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Modifier

/**
 * Splash screen animation values and utilities.
 */
object SplashAnimations {

    /** Duration the splash stays visible before auto-transitioning (ms) */
    const val SPLASH_DISPLAY_DURATION = 2000L

    /** Logo entrance animation duration (ms) */
    const val LOGO_ANIM_DURATION = 800

    /** Branding text entrance delay relative to logo (ms) */
    const val BRANDING_DELAY = 400

    /** Branding text animation duration (ms) */
    const val BRANDING_ANIM_DURATION = 600

    /**
     * Provides a pulsing scale animation for the splash logo glow effect.
     */
    @Composable
    fun rememberGlowPulse(): State<Float> {
        val infiniteTransition = rememberInfiniteTransition(label = "glow_pulse")
        return infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glow_scale"
        )
    }

    /**
     * Provides a subtle floating animation for the logo.
     */
    @Composable
    fun rememberLogoFloat(): State<Float> {
        val infiniteTransition = rememberInfiniteTransition(label = "logo_float")
        return infiniteTransition.animateFloat(
            initialValue = -6f,
            targetValue = 6f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "float_offset"
        )
    }

    /**
     * Creates the logo entrance animation spec (scale from 0.3 → 1.0 with overshoot).
     */
    fun logoScaleSpec(): AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    /**
     * Creates the logo alpha entrance animation spec.
     */
    fun logoAlphaSpec(): AnimationSpec<Float> = tween(
        durationMillis = LOGO_ANIM_DURATION,
        easing = EaseOut
    )

    /**
     * Creates the branding text entrance animation spec.
     */
    fun brandingAlphaSpec(): AnimationSpec<Float> = tween(
        durationMillis = BRANDING_ANIM_DURATION,
        delayMillis = BRANDING_DELAY,
        easing = EaseOut
    )

    fun brandingSlideSpec(): AnimationSpec<Float> = tween(
        durationMillis = BRANDING_ANIM_DURATION,
        delayMillis = BRANDING_DELAY,
        easing = EaseOutCubic
    )
}

private val EaseInOutSine: Easing = CubicBezierEasing(0.37f, 0f, 0.63f, 1f)
private val EaseOut: Easing = CubicBezierEasing(0f, 0f, 0.2f, 1f)
private val EaseOutCubic: Easing = CubicBezierEasing(0.33f, 1f, 0.68f, 1f)
