package com.maeen.mahfilhub

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.maeen.mahfilhub.ui.navigation.AppNavHost
import com.maeen.mahfilhub.ui.navigation.AppRoutes
import com.maeen.mahfilhub.ui.theme.MahfilHubTheme
import com.maeen.mahfilhub.util.LocaleHelper

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
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )

        setContent {
            val navController = rememberNavController()
            val isDarkTheme = isSystemInDarkTheme()

            // Determine if the current screen has a dark background (for status bar icons)
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val hasDarkBackground = currentRoute in listOf(
                AppRoutes.SPLASH,
                AppRoutes.ONBOARDING,
                AppRoutes.LOGIN,
                AppRoutes.REGISTER
            )

            MahfilHubTheme(
                darkTheme = isDarkTheme,
                darkStatusBarIcons = if (hasDarkBackground) false else !isDarkTheme
            ) {
                AppNavHost(
                    navController = navController,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}