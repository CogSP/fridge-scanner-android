package com.example.fridgescanner.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgescanner.data.CreateFridgeRequest
import com.example.fridgescanner.data.CreateFridgeResponse
import com.example.fridgescanner.data.Fridge
import com.example.fridgescanner.data.FridgeItem
import com.example.fridgescanner.data.FridgeRepository
import com.example.fridgescanner.data.ShoppingItem
import com.example.fridgescanner.pythonanywhereAPI.ApiClient
import com.example.fridgescanner.pythonanywhereAPI.AuthService
import com.example.fridgescanner.pythonanywhereAPI.FridgeUserRequest
import com.example.fridgescanner.pythonanywhereAPI.LoginRequest
import com.example.fridgescanner.pythonanywhereAPI.RegisterRequest
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

class FridgeViewModel(private val repository: FridgeRepository, private val authService: AuthService) : ViewModel() {

    private val _darkModeEnabled = MutableStateFlow(false)
    val darkModeEnabled: StateFlow<Boolean> = _darkModeEnabled

    fun setDarkMode(enabled: Boolean) {
        _darkModeEnabled.value = enabled
    }

    // Add a new state for the list of fridges:
    private val _fridges = MutableStateFlow<List<Fridge>>(emptyList())
    val fridges: StateFlow<List<Fridge>> = _fridges.asStateFlow()


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

//    init {
//        fetchFridgeItems()
//        fetchShoppingList()
//    }

    // Fetch the fridges for the logged-in user:
// FridgeViewModel.kt
    fun fetchFridgesForUser() {
        viewModelScope.launch {
            try {
                // 'name' holds the logged-in username (make sure it is set after login)
                val request = FridgeUserRequest(username = name)
                val response = repository.getFridgesForUser(request)
                // Extract the list of fridges (or use an empty list if null).
                _fridges.value = response.fridges ?: emptyList()
            } catch (e: Exception) {
                Log.e("FridgeViewModel", "Error fetching fridges", e)
            }
        }
    }


    // Function to create a new fridge.
    fun createFridge(fridgeName: String, fridgeColor: String, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val request = CreateFridgeRequest(username = name, name = fridgeName, color = fridgeColor)
                val response: CreateFridgeResponse = repository.createFridge(request)
                if (response.success && response.fridgeId != null) {
                    callback(true, response.message)
                    // Refresh the list of fridges
                    fetchFridgesForUser()
                } else {
                    callback(false, response.message ?: "Creation failed")
                }
            } catch (e: Exception) {
                callback(false, e.localizedMessage)
            }
        }
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


    fun registerUser(username: String, email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                // Build the request body.
                val request = RegisterRequest(username, email, password)
                // Make the network call.
                val response = authService.registerUser(request)
                if (response.isSuccessful) {
                    // Registration succeeded. The response body may contain additional info.
                    onResult(true, response.body()?.message)
                } else {
                    // The server returned an error.
                    onResult(false, "Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                // An exception occurred (e.g., network error).
                onResult(false, "Exception: ${e.localizedMessage}")
            }
        }
    }

    fun loginUser(username: String, password: String, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = authService.loginUser(LoginRequest(username, password))
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("MyTag", "ciaoooo sono in loginUser, body = $body")

                    if (body?.success == true) {
                        // Update the ViewModel with the logged-in user's name.
                        name = username
                        callback(true, body.message)
                    } else {
                        callback(false, body?.message ?: "Login failed")
                    }
                } else {
                    callback(false, "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                callback(false, e.localizedMessage)
            }
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