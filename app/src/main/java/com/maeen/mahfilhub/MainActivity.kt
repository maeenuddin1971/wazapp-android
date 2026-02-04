package com.maeen.mahfilhub

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.padding
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import com.maeen.mahfilhub.ui.screens.DesignSystemDemoScreen
import com.maeen.mahfilhub.ui.theme.MahfilHubTheme

class MainActivity : ComponentActivity() {
    private var keepSplashScreen = true
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen BEFORE super.onCreate()
        val splashScreen = installSplashScreen()
        
        // Keep splash screen visible while loading
        splashScreen.setKeepOnScreenCondition { keepSplashScreen }
        
        // Custom exit animation - Smooth Continuous Burst
        splashScreen.setOnExitAnimationListener { splashScreenView: SplashScreenViewProvider ->
            val splashView = splashScreenView.view
            val iconView = splashScreenView.iconView
            
            // Single, continuous explosive scale from 1.0 to 20.0
            // Duration: 700ms total for a smooth but high-speed feel
            val burstX = ObjectAnimator.ofFloat(iconView, View.SCALE_X, 1f, 20f).setDuration(700)
            val burstY = ObjectAnimator.ofFloat(iconView, View.SCALE_Y, 1f, 20f).setDuration(700)
            
            // Using a single AccelerateInterpolator removes all "breaks" in motion
            burstX.interpolator = android.view.animation.AccelerateInterpolator(2f)
            burstY.interpolator = android.view.animation.AccelerateInterpolator(2f)
            
            // Fade out the logo and background smoothly over the same duration
            val iconFade = ObjectAnimator.ofFloat(iconView, View.ALPHA, 1f, 0f).setDuration(700)
            val backgroundFade = ObjectAnimator.ofFloat(splashView, View.ALPHA, 1f, 0f).setDuration(700)
            
            backgroundFade.doOnEnd { 
                splashScreenView.remove() 
            }
            
            // Start everything together for zero-latency, perfectly synced motion
            burstX.start()
            burstY.start()
            iconFade.start()
            backgroundFade.start()
        }
        
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Signal ready as soon as the app starts to trigger the full sequence above
        keepSplashScreen = false
        
        setContent {
            MahfilHubTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DesignSystemDemoScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Theme")
@Composable
fun DesignSystemPreviewLight() {
    MahfilHubTheme(darkTheme = false) {
        DesignSystemDemoScreen()
    }
}

@Preview(showBackground = true, name = "Dark Theme")
@Composable
fun DesignSystemPreviewDark() {
    MahfilHubTheme(darkTheme = true) {
        DesignSystemDemoScreen()
    }
}