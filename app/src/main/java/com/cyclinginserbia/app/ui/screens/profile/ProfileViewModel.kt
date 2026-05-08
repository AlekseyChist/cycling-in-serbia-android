package com.cyclinginserbia.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyclinginserbia.app.data.local.preferences.SyncPreferences
import com.cyclinginserbia.app.data.model.Track
import com.cyclinginserbia.app.data.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    repository: TrackRepository,
    preferences: SyncPreferences,
) : ViewModel() {

    /**
     * Tracks the user has favorited, in the order Room returns them
     * (sort_order = curator-defined). The flow is cold-cheap and
     * Singleton-cached upstream, so we can stateIn it eagerly.
     */
    val favorites: StateFlow<List<Track>> = combine(
        repository.observePublishedTracks(),
        preferences.favoriteTrackIds,
    ) { tracks, ids ->
        if (ids.isEmpty()) emptyList() else tracks.filter { it.uuid in ids }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )
}
