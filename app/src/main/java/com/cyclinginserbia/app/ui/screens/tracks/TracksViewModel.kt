package com.cyclinginserbia.app.ui.screens.tracks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyclinginserbia.app.data.model.Track
import com.cyclinginserbia.app.data.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TracksUiState {
    data object Loading : TracksUiState
    data class Ready(val tracks: List<Track>) : TracksUiState
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
                .onSuccess { _state.value = TracksUiState.Ready(it) }
                .onFailure { _state.value = TracksUiState.Error(it.message ?: "Unknown error") }
        }
    }
}
