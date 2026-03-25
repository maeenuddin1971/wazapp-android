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
import com.maeen.mahfilhub.ui.navigation.AppNavDisplay
import com.maeen.mahfilhub.ui.navigation.AppScreen
import com.maeen.mahfilhub.ui.navigation.isDarkScreen
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
            // Nav3: own the back stack as a plain list
            val backStack = remember { mutableStateListOf<Any>(AppScreen.Splash) }
            val isDarkTheme = isSystemInDarkTheme()

            // Determine if the current top screen has a dark background
            val currentKey = backStack.lastOrNull()
            val hasDarkBackground = currentKey != null && isDarkScreen(currentKey)

            MahfilHubTheme(
                darkTheme = isDarkTheme,
                darkStatusBarIcons = if (hasDarkBackground) false else !isDarkTheme
            ) {
                AppNavDisplay(
                    backStack = backStack,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}