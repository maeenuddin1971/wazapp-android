package com.maeen.mahfilhub

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
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
import com.maeen.mahfilhub.util.LocaleHelper

/**
 * App navigation screens in order of appearance.
 */
private enum class AppScreen {
    SPLASH,
    ONBOARDING,
    MAIN
}

class MainActivity : ComponentActivity() {

    /**
     * Apply the user's saved locale before the activity's context is created.
     */
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install the system splash screen (brief, before our custom one)
        installSplashScreen()

        super.onCreate(savedInstanceState)
        
        // Use dark style initially so the status bar icons are ALWAYS white from frame 0
        // This matches the splash theme seamlessly preventing any delays/flashes.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )

        setContent {
            var currentScreen by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(AppScreen.SPLASH) }
            val isDarkTheme = isSystemInDarkTheme()
            val hasDarkBackground = currentScreen == AppScreen.SPLASH || currentScreen == AppScreen.ONBOARDING

            MahfilHubTheme(
                darkTheme = isDarkTheme,
                darkStatusBarIcons = if (hasDarkBackground) false else !isDarkTheme
            ) {

                AnimatedContent(
                    targetState = currentScreen,
                    modifier = Modifier.fillMaxSize(),
                    transitionSpec = {
                        // Forward slide navigation in perfect sync (prevents white gap during transition)
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(400)
                        ) togetherWith slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth },
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