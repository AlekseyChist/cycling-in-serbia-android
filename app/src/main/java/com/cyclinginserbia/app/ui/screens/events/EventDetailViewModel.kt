package com.cyclinginserbia.app.ui.screens.events

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyclinginserbia.app.data.model.Event
import com.cyclinginserbia.app.data.repository.EventRepository
import com.cyclinginserbia.app.ui.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface EventDetailUiState {
    data object Loading : EventDetailUiState
    data class Ready(val event: Event) : EventDetailUiState
    data class Error(val message: String) : EventDetailUiState
}

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: EventRepository,
) : ViewModel() {

    private val eventId: String =
        savedStateHandle[Destination.EventDetail.ARG_EVENT_ID] ?: ""

    private val _state = MutableStateFlow<EventDetailUiState>(EventDetailUiState.Loading)
    val state: StateFlow<EventDetailUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.value = EventDetailUiState.Loading
        viewModelScope.launch {
            runCatching { repository.getEventById(eventId) }
                .onSuccess { event ->
                    _state.value = if (event != null) EventDetailUiState.Ready(event)
                    else EventDetailUiState.Error("Event not found")
                }
                .onFailure { _state.value = EventDetailUiState.Error(it.message ?: "Unknown error") }
        }
    }
}
