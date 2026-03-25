package com.maeen.mahfilhub.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.maeen.mahfilhub.ui.screens.*
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
}

// ══════════════════════════════════════════════════════════════════════════
// Helper – check if a key represents a dark-background screen
// ══════════════════════════════════════════════════════════════════════════

fun isDarkScreen(key: Any): Boolean = key is AppScreen.Splash ||
    key is AppScreen.Onboarding ||
    key is AppScreen.Login ||
    key is AppScreen.Register

// ══════════════════════════════════════════════════════════════════════════
// NavDisplay setup
// ══════════════════════════════════════════════════════════════════════════

@Composable
fun AppNavDisplay(
    backStack: SnapshotStateList<Any>,
    modifier: Modifier = Modifier
) {
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = {
            if (backStack.size > 1) {
                backStack.removeLastOrNull()
            }
        },
        entryProvider = entryProvider {
            entry<AppScreen.Splash> {
                SplashScreen(
                    onTimeout = {
                        backStack.clear()
                        backStack.add(AppScreen.Onboarding)
                    }
                )
            }

            entry<AppScreen.Onboarding> {
                OnboardingScreen(
                    onFinished = {
                        backStack.clear()
                        backStack.add(AppScreen.Login)
                    }
                )
            }

            entry<AppScreen.Login> {
                LoginScreen(
                    onLoginSuccess = {
                        backStack.clear()
                        backStack.add(AppScreen.Main)
                    },
                    onNavigateToRegister = {
                        backStack.add(AppScreen.Register)
                    },
                    onGuestMode = {
                        backStack.clear()
                        backStack.add(AppScreen.Main)
                    }
                )
            }

            entry<AppScreen.Register> {
                RegisterScreen(
                    onRegisterSuccess = {
                        backStack.clear()
                        backStack.add(AppScreen.Main)
                    },
                    onNavigateToLogin = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<AppScreen.Main> {
                HomeScreen(
                    onEventClick = { eventId ->
                        backStack.add(AppScreen.EventDetail(eventId))
                    },
                    onMaulanaClick = { maulanaId ->
                        backStack.add(AppScreen.MaulanaDetail(maulanaId))
                    }
                )
            }

            entry<AppScreen.EventDetail> { key ->
                EventDetailScreen(
                    eventId = key.eventId,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<AppScreen.MaulanaDetail> { key ->
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
        }
    )
}
