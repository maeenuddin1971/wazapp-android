package com.maeen.mahfilhub.ui.animation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween

/**
 * Reusable screen transition specs for the app.
 * Keeps all transition logic in one place for consistency.
 */
object ScreenTransitions {

    private const val DURATION_MEDIUM = 400
    private const val DURATION_FAST = 300

    /**
     * Slide-left transition: current screen exits left, new screen enters from right.
     * Used for: Splash → Onboarding, Onboarding → Main
     */
    fun slideLeft(): AnimatedContentTransitionScope<Boolean>.() -> ContentTransform = {
        slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(DURATION_MEDIUM)
        ) + fadeIn(
            animationSpec = tween(DURATION_MEDIUM)
        ) togetherWith slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(DURATION_MEDIUM)
        ) + fadeOut(
            animationSpec = tween(DURATION_MEDIUM)
        )
    }

    /**
     * Slide-right transition: current screen exits right, new screen enters from left.
     * Used for: back navigation
     */
    fun slideRight(): AnimatedContentTransitionScope<Boolean>.() -> ContentTransform = {
        slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(DURATION_MEDIUM)
        ) + fadeIn(
            animationSpec = tween(DURATION_MEDIUM)
        ) togetherWith slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(DURATION_MEDIUM)
        ) + fadeOut(
            animationSpec = tween(DURATION_MEDIUM)
        )
    }

    /**
     * Crossfade transition for subtle screen changes.
     */
    fun crossfade(): AnimatedContentTransitionScope<Boolean>.() -> ContentTransform = {
        fadeIn(animationSpec = tween(DURATION_FAST)) togetherWith
            fadeOut(animationSpec = tween(DURATION_FAST))
    }
}

/**
 * Integer-keyed slide-left transition for multi-screen navigation (e.g., enum-based screens).
 */
object MultiScreenTransitions {

    private const val DURATION_MEDIUM = 400

    fun slideLeft(): AnimatedContentTransitionScope<Int>.() -> ContentTransform = {
        slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(DURATION_MEDIUM)
        ) + fadeIn(
            animationSpec = tween(DURATION_MEDIUM)
        ) togetherWith slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(DURATION_MEDIUM)
        ) + fadeOut(
            animationSpec = tween(DURATION_MEDIUM)
        )
    }
}
