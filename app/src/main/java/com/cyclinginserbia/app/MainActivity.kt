package com.cyclinginserbia.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.cyclinginserbia.app.data.local.preferences.AppLanguage
import com.cyclinginserbia.app.data.local.preferences.LocaleManager
import com.cyclinginserbia.app.data.local.preferences.ThemeMode
import com.cyclinginserbia.app.ui.navigation.RootNavigation
import com.cyclinginserbia.app.ui.navigation.RootViewModel
import com.cyclinginserbia.app.ui.theme.CyclingInSerbiaTheme
import com.cyclinginserbia.app.ui.theme.DarkAppColors
import com.cyclinginserbia.app.ui.theme.LightAppColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val rootViewModel: RootViewModel by viewModels()

    // The language this Activity instance was built with. attachBaseContext runs
    // before Hilt injection, so we read the persisted value statically there and
    // remember it to detect a later switch.
    private var appliedLanguage: AppLanguage = AppLanguage.SYSTEM

    override fun attachBaseContext(newBase: Context) {
        appliedLanguage = LocaleManager.currentLanguageBlocking(newBase)
        super.attachBaseContext(LocaleManager.wrap(newBase, appliedLanguage))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Hold the splash screen until RootViewModel decides where to start —
        // we can't show Onboarding-or-Tracks until we've read the persisted
        // completion flag.
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { rootViewModel.initialRoute.value == null }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Recreate the Activity when the user picks a different language so the
        // new locale takes effect (Compose won't re-read resources otherwise).
        //
        // drop(1) skips the StateFlow's current value at subscription time: its
        // seed is SYSTEM and the persisted value loads from DataStore a beat
        // later, so without the drop we'd fire a spurious recreate() at startup
        // whenever the saved language isn't SYSTEM. filter guards the rare case
        // where the post-drop emission still matches what we already applied.
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                rootViewModel.appLanguage
                    .drop(1)
                    .filter { it != appliedLanguage }
                    .collect { recreate() }
            }
        }

        setContent {
            val themeMode by rootViewModel.themeMode.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()
            val isDark = when (themeMode) {
                ThemeMode.SYSTEM -> systemDark
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            val palette = if (isDark) DarkAppColors else LightAppColors

            CyclingInSerbiaTheme(palette = palette) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RootNavigation(rootViewModel = rootViewModel)
                }
            }
        }
    }
}
