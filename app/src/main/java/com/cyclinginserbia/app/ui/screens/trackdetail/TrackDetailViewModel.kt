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
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TrackDetailUiState {
    data object Loading : TrackDetailUiState
    data class Ready(val track: Track) : TrackDetailUiState
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

    init { load() }

    fun load() {
        _state.value = TrackDetailUiState.Loading
        viewModelScope.launch {
            runCatching { repository.getTrackByLegacyId(trackId) }
                .onSuccess { track ->
                    _state.value = if (track != null) TrackDetailUiState.Ready(track)
                    else TrackDetailUiState.Error("Track not found")
                }
                .onFailure { _state.value = TrackDetailUiState.Error(it.message ?: "Unknown error") }
        }
    }
}
