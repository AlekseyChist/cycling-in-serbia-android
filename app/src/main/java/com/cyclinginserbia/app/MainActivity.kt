package com.cyclinginserbia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.cyclinginserbia.app.ui.navigation.RootNavigation
import com.cyclinginserbia.app.ui.navigation.RootViewModel
import com.cyclinginserbia.app.ui.theme.CyclingInSerbiaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val rootViewModel: RootViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Hold the splash screen until RootViewModel decides where to start —
        // we can't show Onboarding-or-Tracks until we've read the persisted
        // completion flag.
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { rootViewModel.initialRoute.value == null }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CyclingInSerbiaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RootNavigation(rootViewModel = rootViewModel)
                }
            }
        }
    }
}
