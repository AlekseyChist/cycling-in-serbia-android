package com.cyclinginserbia.app.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyclinginserbia.app.BuildConfig
import com.cyclinginserbia.app.data.local.preferences.OnboardingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val onboardingPreferences: OnboardingPreferences,
) : ViewModel() {

    // null while we're still reading the DataStore; non-null once decided.
    // MainActivity holds the splash screen until this resolves.
    private val _initialRoute = MutableStateFlow<String?>(null)
    val initialRoute: StateFlow<String?> = _initialRoute.asStateFlow()

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
}
