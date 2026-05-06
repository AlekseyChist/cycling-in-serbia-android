package com.cyclinginserbia.app.ui.screens.tracks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val focusedIds: Set<String> = emptySet(),
) {
    val visible: List<Track>
        get() = tracks.applyTrackFilters(
            query = query,
            difficulty = difficulty,
            surface = surface,
            rideType = rideType,
        )

    val sheetTracks: List<Track>
        get() = if (focusedIds.isEmpty()) visible
        else visible.filter { it.uuid in focusedIds }

    val isFocused: Boolean get() = focusedIds.isNotEmpty()

    val hasActiveFilters: Boolean
        get() = query.isNotBlank() ||
            difficulty != DifficultyFilter.ALL ||
            surface != SurfaceFilter.ALL ||
            rideType != RideTypeFilter.ALL
}

@HiltViewModel
class TracksViewModel @Inject constructor(
    private val repository: TrackRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(TracksUiState())
    val state: StateFlow<TracksUiState> = _state.asStateFlow()

    init {
        observeTracks()
        sync()
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

    fun sync() {
        viewModelScope.launch {
            _state.update { it.copy(isSyncing = true, syncError = null) }
            runCatching { repository.refreshIfStale() }
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
            focusedIds = emptySet(),
        )
    }
}
