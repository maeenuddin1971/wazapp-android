package com.maeen.mahfilhub.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.maeen.mahfilhub.ui.screens.*

// ══════════════════════════════════════════════════════════════════════════
// Route constants
// ══════════════════════════════════════════════════════════════════════════

object AppRoutes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
    const val EVENT_DETAIL = "event_detail/{eventId}"
    const val MAULANA_DETAIL = "maulana_detail/{maulanaId}"

    fun eventDetail(eventId: Int) = "event_detail/$eventId"
    fun maulanaDetail(maulanaId: Int) = "maulana_detail/$maulanaId"
}

// ══════════════════════════════════════════════════════════════════════════
// NavHost
// ══════════════════════════════════════════════════════════════════════════

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.SPLASH,
        modifier = modifier,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(350)
            ) + fadeIn(animationSpec = tween(350))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it / 3 },
                animationSpec = tween(350)
            ) + fadeOut(animationSpec = tween(200))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it / 3 },
                animationSpec = tween(350)
            ) + fadeIn(animationSpec = tween(350))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(350)
            ) + fadeOut(animationSpec = tween(200))
        }
    ) {
        // ── Splash ─────────────────────────────────────────────────────
        composable(
            route = AppRoutes.SPLASH,
            enterTransition = { fadeIn(tween(0)) },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(400)
                )
            }
        ) {
            SplashScreen(
                onTimeout = {
                    navController.navigate(AppRoutes.ONBOARDING) {
                        popUpTo(AppRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // ── Onboarding ─────────────────────────────────────────────────
        composable(
            route = AppRoutes.ONBOARDING,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(400)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(400)
                )
            }
        ) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        // ── Login ──────────────────────────────────────────────────────
        composable(AppRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppRoutes.MAIN) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(AppRoutes.REGISTER)
                },
                onGuestMode = {
                    navController.navigate(AppRoutes.MAIN) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // ── Register ───────────────────────────────────────────────────
        composable(AppRoutes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(AppRoutes.MAIN) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // ── Main (Home with bottom nav) ────────────────────────────────
        composable(
            route = AppRoutes.MAIN,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(400)
                )
            }
        ) {
            HomeScreen(
                onEventClick = { eventId ->
                    navController.navigate(AppRoutes.eventDetail(eventId))
                },
                onMaulanaClick = { maulanaId ->
                    navController.navigate(AppRoutes.maulanaDetail(maulanaId))
                }
            )
        }

        // ── Event Detail ───────────────────────────────────────────────
        composable(
            route = AppRoutes.EVENT_DETAIL,
            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getInt("eventId") ?: 0
            EventDetailScreen(
                eventId = eventId,
                onBack = { navController.popBackStack() }
            )
        }

        // ── Maulana Detail ─────────────────────────────────────────────
        composable(
            route = AppRoutes.MAULANA_DETAIL,
            arguments = listOf(navArgument("maulanaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val maulanaId = backStackEntry.arguments?.getInt("maulanaId") ?: 0
            MaulanaDetailScreen(
                maulanaId = maulanaId,
                onBack = { navController.popBackStack() },
                onEventClick = { eventId ->
                    navController.navigate(AppRoutes.eventDetail(eventId))
                }
            )
        }
    }
}
