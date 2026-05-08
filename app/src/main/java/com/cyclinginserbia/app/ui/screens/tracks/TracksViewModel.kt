package com.cyclinginserbia.app.ui.screens.tracks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyclinginserbia.app.data.local.preferences.SyncPreferences
import com.cyclinginserbia.app.data.model.Track
import com.cyclinginserbia.app.data.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TracksUiState(
    val tracks: List<Track> = emptyList(),
    val isInitialLoading: Boolean = true,
    val isSyncing: Boolean = false,
    val syncError: Throwable? = null,
    val query: String = "",
    val difficulty: DifficultyFilter = DifficultyFilter.ALL,
    val surface: SurfaceFilter = SurfaceFilter.ALL,
    val rideType: RideTypeFilter = RideTypeFilter.ALL,
    val region: String? = null,
    val favoritesOnly: Boolean = false,
    val favoriteIds: Set<String> = emptySet(),
    val focusedIds: Set<String> = emptySet(),
) {
    val visible: List<Track>
        get() = tracks.applyTrackFilters(
            query = query,
            difficulty = difficulty,
            surface = surface,
            rideType = rideType,
            region = region,
            favoritesOnly = favoritesOnly,
            favoriteIds = favoriteIds,
        )

    val sheetTracks: List<Track>
        get() = if (focusedIds.isEmpty()) visible
        else visible.filter { it.uuid in focusedIds }

    val isFocused: Boolean get() = focusedIds.isNotEmpty()

    val hasActiveFilters: Boolean
        get() = query.isNotBlank() ||
            difficulty != DifficultyFilter.ALL ||
            surface != SurfaceFilter.ALL ||
            rideType != RideTypeFilter.ALL ||
            region != null ||
            favoritesOnly
}

@HiltViewModel
class TracksViewModel @Inject constructor(
    private val repository: TrackRepository,
    private val preferences: SyncPreferences,
) : ViewModel() {

    private val _state = MutableStateFlow(TracksUiState())
    val state: StateFlow<TracksUiState> = _state.asStateFlow()

    init {
        observeTracks()
        observeFavorites()
        sync()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            preferences.favoriteTrackIds.collect { ids ->
                _state.update { it.copy(favoriteIds = ids) }
            }
        }
    }

    private fun observeTracks() {
        viewModelScope.launch {
            repository.observePublishedTracks().collect { tracks ->
                _state.update {
                    it.copy(
                        tracks = tracks,
                        isInitialLoading = it.isInitialLoading && tracks.isEmpty(),
                    )
                }
            }
        }
    }

    // Always re-fetch on Tracks-tab entry: Room delivers cached data instantly
    // via observePublishedTracks(), so the UI never blocks; the network call
    // runs in parallel and Room emits fresh rows when it completes. The 6h
    // TTL on refreshIfStale was masking server-side data fixes for users who
    // came back within the same day.
    fun sync() {
        viewModelScope.launch {
            _state.update { it.copy(isSyncing = true, syncError = null) }
            runCatching { repository.refresh() }
                .onSuccess {
                    _state.update {
                        it.copy(isSyncing = false, isInitialLoading = false, syncError = null)
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(isSyncing = false, isInitialLoading = false, syncError = error)
                    }
                }
        }
    }

    fun onQueryChange(query: String) =
        _state.update { it.copy(query = query, focusedIds = emptySet()) }

    fun onDifficultyChange(difficulty: DifficultyFilter) =
        _state.update { it.copy(difficulty = difficulty, focusedIds = emptySet()) }

    fun onSurfaceChange(surface: SurfaceFilter) =
        _state.update { it.copy(surface = surface, focusedIds = emptySet()) }

    fun onRideTypeChange(rideType: RideTypeFilter) =
        _state.update { it.copy(rideType = rideType, focusedIds = emptySet()) }

    fun onRegionChange(region: String?) =
        _state.update { it.copy(region = region, focusedIds = emptySet()) }

    fun onToggleFavoritesOnly() =
        _state.update { it.copy(favoritesOnly = !it.favoritesOnly, focusedIds = emptySet()) }

    fun onToggleFavorite(uuid: String) {
        viewModelScope.launch { preferences.toggleFavoriteTrack(uuid) }
    }

    fun onFocusTracks(ids: Set<String>) = _state.update { it.copy(focusedIds = ids) }

    fun clearFocus() = _state.update {
        if (it.focusedIds.isEmpty()) it else it.copy(focusedIds = emptySet())
    }

    fun clearFilters() = _state.update {
        it.copy(
            query = "",
            difficulty = DifficultyFilter.ALL,
            surface = SurfaceFilter.ALL,
            rideType = RideTypeFilter.ALL,
            region = null,
            favoritesOnly = false,
            focusedIds = emptySet(),
        )
    }
}
