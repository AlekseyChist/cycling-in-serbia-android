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

sealed interface TracksUiState {
    data object Loading : TracksUiState
    data class Ready(
        val all: List<Track>,
        val visible: List<Track>,
        val query: String,
        val difficulty: DifficultyFilter,
        val surface: SurfaceFilter,
        val selectedId: String?,
    ) : TracksUiState {
        val hasActiveFilters: Boolean
            get() = query.isNotBlank() ||
                difficulty != DifficultyFilter.ALL ||
                surface != SurfaceFilter.ALL
    }
    data class Error(val message: String) : TracksUiState
}

@HiltViewModel
class TracksViewModel @Inject constructor(
    private val repository: TrackRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<TracksUiState>(TracksUiState.Loading)
    val state: StateFlow<TracksUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.value = TracksUiState.Loading
        viewModelScope.launch {
            runCatching { repository.getPublishedTracks() }
                .onSuccess { tracks ->
                    _state.value = TracksUiState.Ready(
                        all = tracks,
                        visible = tracks,
                        query = "",
                        difficulty = DifficultyFilter.ALL,
                        surface = SurfaceFilter.ALL,
                        selectedId = null,
                    )
                }
                .onFailure {
                    _state.value = TracksUiState.Error(it.message ?: "Unknown error")
                }
        }
    }

    fun onQueryChange(query: String) = updateReady { it.copy(query = query) }

    fun onDifficultyChange(difficulty: DifficultyFilter) =
        updateReady { it.copy(difficulty = difficulty) }

    fun onSurfaceChange(surface: SurfaceFilter) = updateReady { it.copy(surface = surface) }

    fun onTrackSelect(id: String?) = updateReady { it.copy(selectedId = id) }

    fun clearFilters() = updateReady {
        it.copy(
            query = "",
            difficulty = DifficultyFilter.ALL,
            surface = SurfaceFilter.ALL,
        )
    }

    private inline fun updateReady(crossinline transform: (TracksUiState.Ready) -> TracksUiState.Ready) {
        _state.update { current ->
            if (current !is TracksUiState.Ready) return@update current
            val next = transform(current)
            next.copy(
                visible = next.all.applyTrackFilters(next.query, next.difficulty, next.surface),
            )
        }
    }
}
