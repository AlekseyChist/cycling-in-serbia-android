package com.cyclinginserbia.app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyclinginserbia.app.data.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: TrackRepository,
) : ViewModel() {

    init {
        viewModelScope.launch {
            runCatching { repository.refreshIfStale() }
        }
    }
}
