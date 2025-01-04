package com.example.fridgescanner.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FridgeRepository {

    // Mock data
    private val mockItems = mutableListOf(
        FridgeItem(id = 1, name = "Milk", expirationDate = "2025-01-05", quantity = 2),
        FridgeItem(id = 2, name = "Eggs", expirationDate = "2025-01-10", quantity = 12),
        FridgeItem(id = 3, name = "Butter", expirationDate = "2025-01-15", quantity = 1),
        FridgeItem(id = 4, name = "Bread", expirationDate = "2024-12-20", quantity = 1)
    )

    suspend fun getFridgeItems(): List<FridgeItem> {
        // Simulate network delay
        return withContext(Dispatchers.IO) {
            // In a real scenario, make a network request here
            mockItems
        }
    }

    suspend fun getFridgeItemById(id: Int): FridgeItem? {
        return withContext(Dispatchers.IO) {
            mockItems.find { it.id == id}
        }
    }

    /**
     * Deletes items whose ids are in [ids].
     * In a real app with a backend or database, you'd do an appropriate delete operation.
     */
    suspend fun deleteItemsByIds(ids: List<Int>) {
        withContext(Dispatchers.IO) {
            // Remove all items whose id is in [ids]
            mockItems.removeAll { it.id in ids }
        }
    }

}