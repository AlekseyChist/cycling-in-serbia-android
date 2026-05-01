package com.cyclinginserbia.app.ui.screens.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyclinginserbia.app.data.model.Event
import com.cyclinginserbia.app.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface EventsUiState {
    data object Loading : EventsUiState
    data class Ready(val events: List<Event>) : EventsUiState
    data class Error(val message: String) : EventsUiState
}

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val repository: EventRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<EventsUiState>(EventsUiState.Loading)
    val state: StateFlow<EventsUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.value = EventsUiState.Loading
        viewModelScope.launch {
            runCatching { repository.getEvents() }
                .onSuccess { _state.value = EventsUiState.Ready(it) }
                .onFailure { _state.value = EventsUiState.Error(it.message ?: "Unknown error") }
        }
    }
}
