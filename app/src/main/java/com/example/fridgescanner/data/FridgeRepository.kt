package com.example.fridgescanner.data

import android.util.Log
import com.example.fridgescanner.pythonanywhereAPI.ApiClient
import com.example.fridgescanner.pythonanywhereAPI.FridgeUserRequest
import com.example.fridgescanner.pythonanywhereAPI.FridgeUserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FridgeRepository {

    private val mockShoppingList = mutableListOf<ShoppingItem>()

//    suspend fun addOrUpdateFridgeItem(newItem: FridgeItem) {
//        withContext(Dispatchers.IO) {
//            val existingItem = mockItems.find { it.id == newItem.id }
//            println("Existing Item: $existingItem")
//            if (existingItem != null) {
//                // Update the quantity if the item already exists
//                mockItems[mockItems.indexOf(existingItem)] = existingItem.copy(
//                    quantity = existingItem.quantity + newItem.quantity
//                )
//            } else {
//                // Add as a new item
//                mockItems.add(newItem)
//            }
//        }
//    }


    // Function to retrieve fridges for a user from the backend.
    suspend fun getFridgesForUser(request: FridgeUserRequest): FridgeUserResponse {
        return withContext(Dispatchers.IO) {
            val response = ApiClient.fridgeApiService.getFridgesForUser(request)
            if (response.isSuccessful) {
                response.body() ?: FridgeUserResponse(
                    success = false,
                    message = "No response body",
                    fridges = emptyList()
                )
            } else {
                FridgeUserResponse(
                    success = false,
                    message = "Error: ${response.code()}",
                    fridges = emptyList()
                )
            }
        }
    }


    // Function to create a new fridge.
    suspend fun createFridge(request: CreateFridgeRequest): CreateFridgeResponse {
        return withContext(Dispatchers.IO) {
            val response = ApiClient.fridgeApiService.createFridge(request)
            if (response.isSuccessful) response.body() ?: CreateFridgeResponse(false, "Unknown error", null)
            else CreateFridgeResponse(false, "Error: ${response.code()}", null)
        }
    }

    suspend fun getFridgeItems(fridgeId: Int): List<FridgeItem> {
        return withContext(Dispatchers.IO) {
            try {
                val response = ApiClient.fridgeApiService.getFridgeItems(
                    FridgeItemRequest(fridgeid = fridgeId)
                )
                Log.d("FridgeRepository", "[in getFridgeItems] Response: $response")
                if (response.isSuccessful) {
                    val items = response.body()?.items ?: emptyList()
                    Log.d("FridgeRepository", "[in getFridgeItems] Fetched items: $items")
                    items
                } else {
                    Log.e("FridgeRepository", "[in getFridgeItems] Error code: ${response.code()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("FridgeRepository", "[in getFridgeItems] Exception: ${e.localizedMessage}")
                emptyList()
            }
        }
    }

    suspend fun getFridgeItemById(itemId: Long, fridgeId: Int): FridgeItem? {
        return withContext(Dispatchers.IO) {
            try {
                val response = ApiClient.fridgeApiService.getFridgeItemById(
                    FridgeItemDetailRequest(itemid = itemId)
                )
                Log.d("FridgeRepository", "[in getFridgeItemById] Response: $response")
                if (response.isSuccessful) {
                    response.body()?.item
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }



//    suspend fun getFridgeItemById(id: Long): FridgeItem? {
//        return withContext(Dispatchers.IO) {
//            mockItems.find { it.id == id }
//        }
//    }

//    suspend fun deleteItemsByIds(ids: List<Long>) {
//        withContext(Dispatchers.IO) {
//            // Remove all items whose id is in [ids]
//            mockItems.removeAll { it.id in ids }
//        }
//    }

    suspend fun deleteFridgeItems(itemIds: List<Long>, fridgeId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                itemIds.forEach { id ->
                    val response = ApiClient.fridgeApiService.removeFridgeItem(
                        FridgeItemRemoveRequest(itemid = id, fridgeid = fridgeId)
                    )
                    if (!response.isSuccessful || response.body()?.success != true) {
                        // You might log the error here and/or return false.
                        throw Exception("Failed to delete item with id $id")
                    }
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }



    fun addOrIncrementShoppingItem(itemName: String) {
        val existingItem = mockShoppingList.find { it.name.equals(itemName, ignoreCase = true) }
        if (existingItem != null) {
            // Replace the old item with a new copy that has incremented quantity
            val newItem = existingItem.copy(quantity = existingItem.quantity + 1)
            mockShoppingList[mockShoppingList.indexOf(existingItem)] = newItem
        } else {
            // Otherwise add a new item
            mockShoppingList.add(ShoppingItem(name = itemName, quantity = 1))
        }
    }

    suspend fun removeFridgeItem(itemId: Long, fridgeId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = ApiClient.fridgeApiService.removeFridgeItem(
                    FridgeItemRemoveRequest(itemid = itemId, fridgeid = fridgeId)
                )
                response.isSuccessful && (response.body()?.success == true)
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }


    fun removeShoppingItem(itemName: String) {
        mockShoppingList.removeAll { it.name.equals(itemName, ignoreCase = true) }
    }

    fun getShoppingList(): List<ShoppingItem> {
        return mockShoppingList.toList() // or a copy
    }

    fun clearShoppingList() {
        mockShoppingList.clear()
    }


}