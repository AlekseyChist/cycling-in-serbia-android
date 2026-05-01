package com.cyclinginserbia.app.ui.screens.shops

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyclinginserbia.app.data.model.Shop
import com.cyclinginserbia.app.data.repository.ShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ShopsUiState {
    data object Loading : ShopsUiState
    data class Ready(val shops: List<Shop>) : ShopsUiState
    data class Error(val message: String) : ShopsUiState
}

@HiltViewModel
class ShopsViewModel @Inject constructor(
    private val repository: ShopRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<ShopsUiState>(ShopsUiState.Loading)
    val state: StateFlow<ShopsUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.value = ShopsUiState.Loading
        viewModelScope.launch {
            runCatching { repository.getShops() }
                .onSuccess { _state.value = ShopsUiState.Ready(it) }
                .onFailure { _state.value = ShopsUiState.Error(it.message ?: "Unknown error") }
        }
    }
}
