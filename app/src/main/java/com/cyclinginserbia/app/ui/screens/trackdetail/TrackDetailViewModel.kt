package com.cyclinginserbia.app.ui.screens.trackdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyclinginserbia.app.data.model.Track
import com.cyclinginserbia.app.data.repository.TrackRepository
import com.cyclinginserbia.app.ui.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TrackDetailUiState {
    data object Loading : TrackDetailUiState
    data class Ready(val track: Track, val gpxUrl: String?) : TrackDetailUiState
    data class Error(val message: String) : TrackDetailUiState
}

@HiltViewModel
class TrackDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TrackRepository,
) : ViewModel() {

    private val trackId: String = savedStateHandle[Destination.TrackDetail.ARG_TRACK_ID] ?: ""

    private val _state = MutableStateFlow<TrackDetailUiState>(TrackDetailUiState.Loading)
    val state: StateFlow<TrackDetailUiState> = _state.asStateFlow()

    private val refreshDone = MutableStateFlow(false)

    init {
        observeTrack()
        triggerRefresh()
    }

    private fun observeTrack() {
        viewModelScope.launch {
            combine(
                repository.observeTrack(trackId),
                refreshDone,
            ) { track, done ->
                when {
                    track != null -> {
                        val gpxUrl = track.gpxFileName?.let { repository.gpxPublicUrl(it) }
                        TrackDetailUiState.Ready(track = track, gpxUrl = gpxUrl)
                    }
                    !done -> TrackDetailUiState.Loading
                    else -> TrackDetailUiState.Error("Track not found")
                }
            }.collect { _state.value = it }
        }
    }

    private fun triggerRefresh() {
        viewModelScope.launch {
            runCatching { repository.refreshIfStale() }
            refreshDone.value = true
        }
    }
}
