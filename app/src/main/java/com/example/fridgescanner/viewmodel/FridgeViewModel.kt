package com.example.fridgescanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgescanner.data.FridgeItem
import com.example.fridgescanner.data.FridgeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FridgeViewModel(private val repository: FridgeRepository) : ViewModel() {

    lateinit var lastScannedCode: Any
    var name: String = ""

    // Backing property for fridge items
    private val _fridgeItems = MutableStateFlow<List<FridgeItem>>(emptyList())
    val fridgeItems: StateFlow<List<FridgeItem>> = _fridgeItems.asStateFlow()

    // Backing property for loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Backing property for error messages
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _fridgeItemDetail = MutableStateFlow<FridgeItem?>(null)
    val fridgeItemDetail: StateFlow<FridgeItem?> = _fridgeItemDetail

    // Backing property for search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _expirationThreshold = MutableStateFlow(3L) // default to 3
    val expirationThreshold: StateFlow<Long> = _expirationThreshold.asStateFlow()

    fun setExpirationThreshold(days: Long) {
        _expirationThreshold.value = days
    }

    // Combined flow for filtered fridge items based on search query
    val filteredFridgeItems: StateFlow<List<FridgeItem>> = combine(
        _fridgeItems,
        _searchQuery
    ) { items, query ->
        if (query.isBlank()) {
            items
        } else {
            items.filter { it.name.contains(query, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    init {
        fetchFridgeItems()
    }

    fun fetchFridgeItems() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val items = repository.getFridgeItems()
                _fridgeItems.value = items.toList()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load fridge items."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchFridgeItemById(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val item = repository.getFridgeItemById(id)
                _fridgeItemDetail.value = item
                if (item == null) {
                    _errorMessage.value = "Item not found."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load item details"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addOrUpdateFridgeItem(newItem: FridgeItem) {
        viewModelScope.launch {
            repository.addOrUpdateFridgeItem(newItem)
            fetchFridgeItems()
        }
    }

    fun deleteFridgeItems(itemIds: List<Int>) {
        viewModelScope.launch {
            repository.deleteItemsByIds(itemIds)
            fetchFridgeItems()
        }
    }

}