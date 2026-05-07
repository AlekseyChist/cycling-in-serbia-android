package com.cyclinginserbia.app.ui.screens.regulations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyclinginserbia.app.data.model.RegulationCategory
import com.cyclinginserbia.app.data.repository.RegulationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RegulationsUiState {
    data object Loading : RegulationsUiState
    data class Ready(
        val categories: List<RegulationCategory>,
        val expandedIds: Set<String> = emptySet(),
        val bookmarkedIds: Set<String> = emptySet(),
    ) : RegulationsUiState
    data class Error(val message: String) : RegulationsUiState
}

@HiltViewModel
class RegulationsViewModel @Inject constructor(
    private val repository: RegulationRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<RegulationsUiState>(RegulationsUiState.Loading)
    val state: StateFlow<RegulationsUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.value = RegulationsUiState.Loading
        viewModelScope.launch {
            runCatching { repository.getRegulations() }
                .onSuccess { _state.value = RegulationsUiState.Ready(categories = it) }
                .onFailure { _state.value = RegulationsUiState.Error(it.message ?: "Unknown error") }
        }
    }

    fun toggleExpanded(id: String) = _state.update { current ->
        if (current is RegulationsUiState.Ready) {
            current.copy(expandedIds = current.expandedIds.toggle(id))
        } else current
    }

    fun toggleBookmark(id: String) = _state.update { current ->
        if (current is RegulationsUiState.Ready) {
            current.copy(bookmarkedIds = current.bookmarkedIds.toggle(id))
        } else current
    }

    private fun <T> Set<T>.toggle(item: T): Set<T> =
        if (contains(item)) this - item else this + item
}
