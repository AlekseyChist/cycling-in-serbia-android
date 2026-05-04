package com.cyclinginserbia.app.ui.screens.shops

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyclinginserbia.app.data.model.Shop
import com.cyclinginserbia.app.data.model.ShopTab
import com.cyclinginserbia.app.data.repository.ShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopsViewModel @Inject constructor(
    private val repository: ShopRepository,
) : ViewModel() {

    private val _allShops = MutableStateFlow<List<Shop>>(emptyList())
    private val _query = MutableStateFlow("")
    private val _tab = MutableStateFlow(ShopTab.ALL)

    val query: StateFlow<String> = _query.asStateFlow()
    val tab: StateFlow<ShopTab> = _tab.asStateFlow()

    val filtered: StateFlow<List<Shop>> = combine(_allShops, _query, _tab) { shops, q, tab ->
        val needle = q.trim().lowercase()
        shops.filter { shop ->
            val matchesTab = tab == ShopTab.ALL || tab in shop.tabs
            val matchesSearch = needle.isEmpty() ||
                shop.name.lowercase().contains(needle) ||
                shop.description.lowercase().contains(needle) ||
                shop.category.lowercase().contains(needle)
            matchesTab && matchesSearch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            _allShops.value = repository.getShops()
        }
    }

    fun onQueryChange(value: String) { _query.value = value }
    fun onTabChange(value: ShopTab) { _tab.value = value }
}
