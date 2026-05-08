package com.cyclinginserbia.app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyclinginserbia.app.data.repository.EventRepository
import com.cyclinginserbia.app.data.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val tracks: TrackRepository,
    private val events: EventRepository,
) : ViewModel() {

    // Warm both repositories while the user is reading the onboarding copy,
    // so the first-tab landings (Tracks map, Events list) feel instant.
    init {
        viewModelScope.launch { runCatching { tracks.refreshIfStale() } }
        viewModelScope.launch { runCatching { events.getEvents() } }
    }
}
