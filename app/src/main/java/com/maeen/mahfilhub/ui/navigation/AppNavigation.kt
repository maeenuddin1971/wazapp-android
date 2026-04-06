package com.maeen.mahfilhub.ui.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.maeen.mahfilhub.ui.screens.*
import com.maeen.mahfilhub.util.SessionManager
import kotlinx.serialization.Serializable

// ══════════════════════════════════════════════════════════════════════════
// Navigation Keys (type-safe, serializable)
// ══════════════════════════════════════════════════════════════════════════

sealed interface AppScreen {
    @Serializable
    data object Splash : AppScreen

    @Serializable
    data object Onboarding : AppScreen

    @Serializable
    data object Login : AppScreen

    @Serializable
    data object Register : AppScreen

    @Serializable
    data object Main : AppScreen

    @Serializable
    data class EventDetail(val eventId: Int) : AppScreen

    @Serializable
    data class MaulanaDetail(val maulanaId: Int) : AppScreen

    @Serializable
    data object Notifications : AppScreen

    @Serializable
    data class NotificationDetail(val notificationId: Int) : AppScreen

    @Serializable
    data object EditProfile : AppScreen
}

// ══════════════════════════════════════════════════════════════════════════
// Helper – check if a key represents a dark-background screen
// ══════════════════════════════════════════════════════════════════════════

fun isDarkScreen(key: Any): Boolean = key is AppScreen.Splash ||
    key is AppScreen.Onboarding ||
    key is AppScreen.Login ||
    key is AppScreen.Register ||
    key is AppScreen.Main ||
    key is AppScreen.EventDetail ||
    key is AppScreen.MaulanaDetail ||
    key is AppScreen.Notifications ||
    key is AppScreen.NotificationDetail ||
    key is AppScreen.EditProfile

// ══════════════════════════════════════════════════════════════════════════
// Animation Specs
// ══════════════════════════════════════════════════════════════════════════

private const val ANIM_DURATION = 350
private const val FADE_DURATION = 200

/** Forward: new screen slides in from right, old screen stays mostly in place */
private val slideForwardTransition: ContentTransform =
    (slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(ANIM_DURATION)
    ) + fadeIn(tween(FADE_DURATION)))
        .togetherWith(
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth / 4 },
                animationSpec = tween(ANIM_DURATION)
            )
            // No fadeOut on the old screen — prevents white flash
        )

/** Pop: previous screen slides in from left, current screen slides out to right */
private val slidePopTransition: ContentTransform =
    (slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth / 4 },
        animationSpec = tween(ANIM_DURATION)
    ))
        .togetherWith(
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(ANIM_DURATION)
            )
            // No fadeOut — prevents white flash on back navigation
        )

/** Splash → Onboarding: simple crossfade (no slide) */
private val fadeTransition: ContentTransform =
    fadeIn(tween(400)).togetherWith(fadeOut(tween(400)))

/**
 * Build the metadata map with both forward and pop animation specs.
 */
private fun slideMetadata(): Map<String, Any> =
    NavDisplay.transitionSpec { slideForwardTransition } +
        NavDisplay.popTransitionSpec { slidePopTransition }

private fun fadeMetadata(): Map<String, Any> =
    NavDisplay.transitionSpec { fadeTransition } +
        NavDisplay.popTransitionSpec { fadeTransition }

// ══════════════════════════════════════════════════════════════════════════
// NavDisplay setup
// ══════════════════════════════════════════════════════════════════════════

@Composable
fun AppNavDisplay(
    backStack: SnapshotStateList<Any>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = {
            if (backStack.size > 1) {
                backStack.removeLastOrNull()
            }
        },
        entryProvider = entryProvider {
            entry<AppScreen.Splash>(
                metadata = fadeMetadata()
            ) {
                SplashScreen(
                    onTimeout = {
                        backStack.clear()
                        if (SessionManager.isLoggedIn(context)) {
                            backStack.add(AppScreen.Main)
                        } else {
                            backStack.add(AppScreen.Onboarding)
                        }
                    }
                )
            }

            entry<AppScreen.Onboarding>(
                metadata = slideMetadata()
            ) {
                OnboardingScreen(
                    onFinished = {
                        backStack.clear()
                        backStack.add(AppScreen.Login)
                    }
                )
            }

            entry<AppScreen.Login>(
                metadata = slideMetadata()
            ) {
                LoginScreen(
                    onLoginSuccess = {
                        backStack.clear()
                        backStack.add(AppScreen.Main)
                    },
                    onNavigateToRegister = {
                        backStack.add(AppScreen.Register)
                    },
                    onGuestMode = {
                        SessionManager.loginAsGuest(context)
                        backStack.clear()
                        backStack.add(AppScreen.Main)
                    }
                )
            }

            entry<AppScreen.Register>(
                metadata = slideMetadata()
            ) {
                RegisterScreen(
                    onRegisterSuccess = {
                        SessionManager.login(context, token = "", role = "ROLE_USER", email = "newuser@mahfilhub.com")
                        backStack.clear()
                        backStack.add(AppScreen.Main)
                    },
                    onNavigateToLogin = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<AppScreen.Main>(
                metadata = slideMetadata()
            ) {
                HomeScreen(
                    onEventClick = { eventId ->
                        backStack.add(AppScreen.EventDetail(eventId))
                    },
                    onMaulanaClick = { maulanaId ->
                        backStack.add(AppScreen.MaulanaDetail(maulanaId))
                    },
                    onNotificationsClick = {
                        backStack.add(AppScreen.Notifications)
                    },
                    onEditProfileClick = {
                        backStack.add(AppScreen.EditProfile)
                    },
                    onLogoutClick = {
                        SessionManager.logout(context)
                        backStack.clear()
                        backStack.add(AppScreen.Onboarding)
                    }
                )
            }

            entry<AppScreen.EventDetail>(
                metadata = slideMetadata()
            ) { key ->
                EventDetailScreen(
                    eventId = key.eventId,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<AppScreen.MaulanaDetail>(
                metadata = slideMetadata()
            ) { key ->
                MaulanaDetailScreen(
                    maulanaId = key.maulanaId,
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    onEventClick = { eventId ->
                        backStack.add(AppScreen.EventDetail(eventId))
                    }
                )
            }

            entry<AppScreen.Notifications>(
                metadata = slideMetadata()
            ) {
                NotificationListScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    onNotificationClick = { notificationId ->
                        backStack.add(AppScreen.NotificationDetail(notificationId))
                    }
                )
            }

            entry<AppScreen.NotificationDetail>(
                metadata = slideMetadata()
            ) { key ->
                NotificationDetailScreen(
                    notificationId = key.notificationId,
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    onEventClick = { eventId ->
                        backStack.add(AppScreen.EventDetail(eventId))
                    },
                    onMaulanaClick = { maulanaId ->
                        backStack.add(AppScreen.MaulanaDetail(maulanaId))
                    }
                )
            }

            entry<AppScreen.EditProfile>(
                metadata = slideMetadata()
            ) {
                EditProfileScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    onSave = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}
