package com.cyclinginserbia.app.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyclinginserbia.app.BuildConfig
import com.cyclinginserbia.app.data.local.preferences.AppLanguage
import com.cyclinginserbia.app.data.local.preferences.LocalePreferences
import com.cyclinginserbia.app.data.local.preferences.OnboardingPreferences
import com.cyclinginserbia.app.data.local.preferences.ThemeMode
import com.cyclinginserbia.app.data.local.preferences.ThemePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val onboardingPreferences: OnboardingPreferences,
    private val themePreferences: ThemePreferences,
    private val localePreferences: LocalePreferences,
) : ViewModel() {

    // null while we're still reading the DataStore; non-null once decided.
    // MainActivity holds the splash screen until this resolves.
    private val _initialRoute = MutableStateFlow<String?>(null)
    val initialRoute: StateFlow<String?> = _initialRoute.asStateFlow()

    // Eagerly mirror the persisted theme mode into a StateFlow so Theme.kt
    // can collectAsStateWithLifecycle without re-subscribing on every recomposition.
    val themeMode: StateFlow<ThemeMode> = themePreferences.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ThemeMode.SYSTEM,
    )

    // Mirror the persisted language so the Profile picker can reflect the
    // current choice and MainActivity can recreate() when it changes.
    val appLanguage: StateFlow<AppLanguage> = localePreferences.appLanguage.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AppLanguage.SYSTEM,
    )

    init {
        viewModelScope.launch {
            val lastCompleted = onboardingPreferences.lastCompletedVersionCode.first()
            _initialRoute.value = if (lastCompleted == BuildConfig.VERSION_CODE) {
                Destination.Tracks.route
            } else {
                Destination.Onboarding.route
            }
        }
    }

    fun markOnboardingCompleted() {
        viewModelScope.launch {
            onboardingPreferences.markCompleted(BuildConfig.VERSION_CODE)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { themePreferences.setThemeMode(mode) }
    }

    fun setAppLanguage(language: AppLanguage) {
        viewModelScope.launch { localePreferences.setAppLanguage(language) }
    }
}
