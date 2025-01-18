package com.example.fridgescanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgescanner.data.FridgeItem
import com.example.fridgescanner.data.FridgeRepository
import com.example.fridgescanner.data.ShoppingItem
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

    private val _darkModeEnabled = MutableStateFlow(false)
    val darkModeEnabled: StateFlow<Boolean> = _darkModeEnabled

    fun setDarkMode(enabled: Boolean) {
        _darkModeEnabled.value = enabled
    }

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

    // Backing property for shopping list
    private val _shoppingList = MutableStateFlow<List<ShoppingItem>>(emptyList())
    val shoppingList: StateFlow<List<ShoppingItem>> = _shoppingList


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
        fetchShoppingList()
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

    fun fetchFridgeItemById(id: Long) {
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

    fun deleteFridgeItems(itemIds: List<Long>) {
        viewModelScope.launch {
            repository.deleteItemsByIds(itemIds)
            fetchFridgeItems()
        }
    }


    fun addToShoppingList(item: String) {
        viewModelScope.launch {
            try {
                repository.addOrIncrementShoppingItem(item) // Call a corresponding repository function
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add item to shopping list."
            }
        }
    }



    fun deleteItemFromShoppingList(item: String) {
        viewModelScope.launch {
            repository.removeShoppingItem(item)
            fetchShoppingList() // refresh
        }
    }


    // Suppose you store them in a repository or a local list
    fun addOrIncrementShoppingItem(itemName: String) {
        repository.addOrIncrementShoppingItem(itemName)
        fetchShoppingList() // re-fetch to update the flow
    }

    // For removing an item entirely
    fun removeShoppingItem(itemName: String) {
        repository.removeShoppingItem(itemName)
        fetchShoppingList()
    }

    // For clearing all items
    fun clearShoppingList() {
        repository.clearShoppingList()
        fetchShoppingList()
    }

    // Get the updated list from repository
    fun fetchShoppingList() {
        viewModelScope.launch {
            // retrieve from repository
            val items = repository.getShoppingList()
            _shoppingList.value = items
        }
    }
}