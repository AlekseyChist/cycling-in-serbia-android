package com.cyclinginserbia.app.ui.screens.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyclinginserbia.app.data.model.Event
import com.cyclinginserbia.app.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface EventsUiState {
    data object Loading : EventsUiState
    data class Ready(
        val all: List<Event>,
        val visible: List<Event>,
        val query: String,
        val category: EventCategoryFilter,
        val type: EventTypeFilter,
    ) : EventsUiState {
        val hasActiveFilters: Boolean
            get() = query.isNotBlank() ||
                category != EventCategoryFilter.ALL ||
                type != EventTypeFilter.ALL
    }
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
                .onSuccess { events ->
                    _state.value = EventsUiState.Ready(
                        all = events,
                        visible = events,
                        query = "",
                        category = EventCategoryFilter.ALL,
                        type = EventTypeFilter.ALL,
                    )
                }
                .onFailure {
                    _state.value = EventsUiState.Error(it.message ?: "Unknown error")
                }
        }
    }

    fun onQueryChange(query: String) = updateReady { it.copy(query = query) }

    fun onCategoryChange(category: EventCategoryFilter) =
        updateReady { it.copy(category = category) }

    fun onTypeChange(type: EventTypeFilter) = updateReady { it.copy(type = type) }

    fun clearFilters() = updateReady {
        it.copy(
            query = "",
            category = EventCategoryFilter.ALL,
            type = EventTypeFilter.ALL,
        )
    }

    private inline fun updateReady(crossinline transform: (EventsUiState.Ready) -> EventsUiState.Ready) {
        _state.update { current ->
            if (current !is EventsUiState.Ready) return@update current
            val next = transform(current)
            next.copy(visible = next.all.applyFilters(next.query, next.category, next.type))
        }
    }
}
