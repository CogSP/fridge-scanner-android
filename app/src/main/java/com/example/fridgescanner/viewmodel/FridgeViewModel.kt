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
import com.example.fridgescanner.pythonanywhereAPI.FridgeApiService
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

class FridgeViewModel(
    private val repository: FridgeRepository,
    private val authService: AuthService,
    private val fridgeApiService: FridgeApiService
) : ViewModel() {

    private val _darkModeEnabled = MutableStateFlow(false)
    val darkModeEnabled: StateFlow<Boolean> = _darkModeEnabled

    private val _ownerEmail = MutableStateFlow<String?>(null)
    val ownerEmail: StateFlow<String?> = _ownerEmail.asStateFlow()


    private val _ownerName = MutableStateFlow<String?>(null)
    val ownerName: StateFlow<String?> = _ownerName.asStateFlow()


    // Add a state to hold the list of shared users (as email strings).
    private val _sharedUsers = MutableStateFlow<List<String>>(emptyList())
    val sharedUsers: StateFlow<List<String>> = _sharedUsers.asStateFlow()

    // In your ViewModel (FridgeViewModel):
    private val _selectedExpiryDate = MutableStateFlow("")
    val selectedExpiryDate: StateFlow<String> = _selectedExpiryDate.asStateFlow()

    fun setSelectedExpiryDate(date: String) {
        _selectedExpiryDate.value = date
    }

    // FridgeViewModel.kt (add these inside your ViewModel class)
    private val _currentFridgeId = MutableStateFlow<String?>(null)
    val currentFridgeId: StateFlow<String?> = _currentFridgeId.asStateFlow()

    fun setCurrentFridgeId(id: String) {
        _currentFridgeId.value = id
        fetchOwnerEmail(id)
    }

    fun clearCurrentFridgeId() {
        _currentFridgeId.value = null
    }

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
    fun createFridge(fridgeName: String, fridgeColor: String, callback: (Boolean, String?, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val request = CreateFridgeRequest(username = name, name = fridgeName, color = fridgeColor)
                val response: CreateFridgeResponse = repository.createFridge(request)
                Log.d("FridgeViewModel", "Response from server: $response")
                if (response.success && response.fridgeId != null) {
                    callback(true, response.message, response.fridgeId.toString())
                    // Refresh the list of fridges
                    fetchFridgesForUser()
                } else {
                    callback(false, response.message ?: "Creation failed", null)
                }
            } catch (e: Exception) {
                callback(false, e.localizedMessage, null)
            }
        }
    }

    fun fetchFridgeItemsForCurrentFridge() {
        viewModelScope.launch {
            // Assuming currentFridgeId is a String; convert it to an Int.
            val fridgeId = currentFridgeId.value?.toIntOrNull()
            Log.d("FridgeViewModel", "Fetching items for fridge ID: $fridgeId")
            if (fridgeId != null) {
                _fridgeItems.value = repository.getFridgeItems(fridgeId)
                Log.d("FridgeViewModel", "Fetched items: ${_fridgeItems.value}")
            } else {
                _fridgeItems.value = emptyList()
            }
        }
    }


//    fun fetchFridgeItems() {
//        viewModelScope.launch {
//            _isLoading.value = true
//            _errorMessage.value = null
//            try {
//                val items = repository.getFridgeItems()
//                _fridgeItems.value = items.toList()
//            } catch (e: Exception) {
//                _errorMessage.value = "Failed to load fridge items."
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }

    fun fetchFridgeItemById(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val fridgeId = currentFridgeId.value?.toIntOrNull()
            if (fridgeId != null) {
                val item = repository.getFridgeItemById(id, fridgeId)
                _fridgeItemDetail.value = item
                Log.d("FridgeViewModel", "Fetched item PORCACCIO: $item")
                if (item == null) {
                    _errorMessage.value = "Item not found."
                }
            } else {
                _errorMessage.value = "Invalid fridge id."
            }
            _isLoading.value = false
        }
    }


    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

//    fun addOrUpdateFridgeItem(newItem: FridgeItem) {
//        viewModelScope.launch {
//            repository.addOrUpdateFridgeItem(newItem)
//            fetchFridgeItems()
//        }
//    }

    fun deleteFridgeItems(itemIds: List<Long>) {
        viewModelScope.launch {
            // Convert the current fridge ID (assumed to be stored as a String) to an Int.
            val fridgeId = currentFridgeId.value?.toIntOrNull()
            if (fridgeId == null) {
                _errorMessage.value = "Invalid fridge id."
                return@launch
            }
            val success = repository.deleteFridgeItems(itemIds, fridgeId)
            if (!success) {
                _errorMessage.value = "Failed to delete selected items."
            }
            // Refresh the fridge items for the current fridge.
            fetchFridgeItemsForCurrentFridge()
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

    fun removeFridgeItem(itemId: Long) {
        viewModelScope.launch {
            // Ensure the current fridge ID is valid.
            val fridgeId = currentFridgeId.value?.toIntOrNull()
            if (fridgeId != null) {
                val success = repository.removeFridgeItem(itemId, fridgeId)
                if (!success) {
                    _errorMessage.value = "Failed to remove item"
                }
                // Refresh the list of items for the current fridge.
                fetchFridgeItemsForCurrentFridge()
            } else {
                _errorMessage.value = "Invalid fridge id"
            }
        }
    }

    fun forgotPassword(emailOrUsername: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val request = mapOf("email" to emailOrUsername)
                val response = authService.forgotPassword(request)
                if (response.isSuccessful) {
                    onResult(true, response.body()?.message)
                } else {
                    onResult(false, "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                onResult(false, e.localizedMessage)
            }
        }
    }

    fun resetPassword(
        email: String,
        resetCode: String,
        newPassword: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Create an instance of the ResetPasswordRequest.
                val request = com.example.fridgescanner.pythonanywhereAPI.ResetPasswordRequest(
                    email = email,
                    reset_code = resetCode,
                    new_password = newPassword
                )
                // Call the API with the typed request.
                val response = authService.resetPassword(request)
                if (response.isSuccessful) {
                    // Assuming your API returns a ResetPasswordResponse object.
                    onResult(true, response.body()?.message)
                } else {
                    onResult(false, "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                onResult(false, e.localizedMessage)
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

    fun shareFridge(
        email: String,
        fridgeId: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Create an instance of ShareFridgeRequest.
                val request = com.example.fridgescanner.pythonanywhereAPI.ShareFridgeRequest(
                    email = email,
                    fridge_id = fridgeId,
                    username = name
                )
                // Call the API endpoint.
                val response = fridgeApiService.shareFridge(request)
                if (response.isSuccessful) {
                    // Assuming your API returns a ShareFridgeResponse.
                    onResult(true, response.body()?.message)
                } else {
                    onResult(false, "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                onResult(false, e.localizedMessage)
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

    fun logout() {
        // Clear the logged-in user's name.
        name = ""
        // Clear the current fridge selection.
        clearCurrentFridgeId()
        // Optionally clear other user-specific data.
        _fridges.value = emptyList()
        _fridgeItems.value = emptyList()
        _shoppingList.value = emptyList()
        _errorMessage.value = null
        _searchQuery.value = ""
        // You might also want to clear dark mode or other preferences if needed.
        // For example: _darkModeEnabled.value = false
        Log.d("FridgeViewModel", "User has been logged out.")
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

    // Function to fetch the fridge members from your PythonAnywhere backend.
    fun fetchFridgeMembers(fridgeId: String) {
        viewModelScope.launch {
            try {
                val response = fridgeApiService.getFridgeMembers(fridgeId)
                if (response.isSuccessful) {
                    // Assuming your API returns a FridgeMembersResponse with a 'members' list.
                    val members = response.body()?.members ?: emptyList()
                    _sharedUsers.value = members
                } else {
                    Log.e("FridgeViewModel", "Error fetching fridge members: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("FridgeViewModel", "Exception fetching fridge members: ${e.localizedMessage}")
            }
        }
    }

    fun fetchOwnerEmail(fridgeId: String) {
        viewModelScope.launch {
            try {
                // Assuming your FridgeApiService has a function getFridgeOwner that takes a fridgeId.
                val response = fridgeApiService.getFridgeOwner(fridgeId)
                if (response.isSuccessful) {
                    // Assuming the response body contains a field 'owner_email'
                    _ownerName.value = response.body()?.owner_name
                    _ownerEmail.value = response.body()?.owner_email
                } else {
                    Log.e("FridgeViewModel", "Error fetching owner email: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("FridgeViewModel", "Exception fetching owner email: ${e.localizedMessage}")
            }
        }
    }


    fun deleteFridges(fridgeIds: List<String>, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                // Create a request object with the list of fridge IDs.
                val request = com.example.fridgescanner.pythonanywhereAPI.DeleteFridgesRequest(fridge_ids = fridgeIds)
                // Call the API endpoint.
                val response = fridgeApiService.deleteFridges(request)
                if (response.isSuccessful) {
                    // On success, call the callback and refresh the list of fridges.
                    onResult(true, response.body()?.message)
                    fetchFridgesForUser()
                } else {
                    onResult(false, "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                onResult(false, e.localizedMessage)
            }
        }
    }



}