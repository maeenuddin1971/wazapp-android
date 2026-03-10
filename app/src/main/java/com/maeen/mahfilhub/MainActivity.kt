package com.maeen.mahfilhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.maeen.mahfilhub.ui.screens.DesignSystemDemoScreen
import com.maeen.mahfilhub.ui.screens.OnboardingScreen
import com.maeen.mahfilhub.ui.screens.SplashScreen
import com.maeen.mahfilhub.ui.theme.MahfilHubTheme

/**
 * App navigation screens in order of appearance.
 */
private enum class AppScreen {
    SPLASH,
    ONBOARDING,
    MAIN
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install the system splash screen (brief, before our custom one)
        val systemSplash = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MahfilHubTheme {
                var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }

                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        // All screen transitions slide left (forward navigation)
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(400)
                        ) + fadeIn(
                            animationSpec = tween(400)
                        ) togetherWith slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(400)
                        ) + fadeOut(
                            animationSpec = tween(400)
                        )
                    },
                    label = "screen_navigation"
                ) { screen ->
                    when (screen) {
                        AppScreen.SPLASH -> {
                            SplashScreen(
                                onTimeout = { currentScreen = AppScreen.ONBOARDING }
                            )
                        }
                        AppScreen.ONBOARDING -> {
                            OnboardingScreen(
                                onFinished = { currentScreen = AppScreen.MAIN }
                            )
                        }
                        AppScreen.MAIN -> {
                            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                                DesignSystemDemoScreen(
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}