package com.example.fridgescanner.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FridgeRepository {

    private val mockShoppingList = mutableListOf<ShoppingItem>()

    private val mockItems = mutableListOf(
        FridgeItem(
            id = 1,
            name = "Milk",
            expirationDate = "2025-01-05",
            quantity = 2,
            brand = "brand name",
            category = "category",
            allergens = "allergens",
            conservationConditions = "keep it fresh",
            countriesWhereSold = "Italy",
            countriesImported = "USA",
            ownerImported = "Barilla",
            preparation = "Do not boil",
            purchasePlaces = "Colosseum",
            productQuantity = "100",
            productQuantityUnit = "g",
            productType = "food",
            customerService = "www.examples.com",
            imageUrl = "Not Found",
            ingredientsImageEn = "Not Found",
            carbohydrates100g = "20",
            energyKcal100g = "20",
            fat100g = "20",
            fiber100g = "20",
            proteins100g = "20",
            salt100g = "20",
            saturatedFat100g = "20",
            sodium100g = "20",
            sugars100g = "20"
        ),
        FridgeItem(
            id = 2,
            name = "Eggs",
            expirationDate = "2025-01-10",
            quantity = 12,
            brand = "brand name",
            category = "category",
            allergens = "allergens",
            conservationConditions = "keep it fresh",
            countriesWhereSold = "Italy",
            countriesImported = "USA",
            ownerImported = "Barilla",
            preparation = "Do not boil",
            purchasePlaces = "Colosseum",
            productQuantity = "100",
            productQuantityUnit = "g",
            productType = "food",
            customerService = "www.examples.com",
            imageUrl = "Not Found",
            ingredientsImageEn = "Not Found",
            carbohydrates100g = "20",
            energyKcal100g = "20",
            fat100g = "20",
            fiber100g = "20",
            proteins100g = "20",
            salt100g = "20",
            saturatedFat100g = "20",
            sodium100g = "20",
            sugars100g = "20"
        ),
        FridgeItem(
            id = 3,
            name = "Butter",
            expirationDate = "2025-01-15",
            quantity = 1,
            brand = "brand name",
            category = "category",
            allergens = "allergens",
            conservationConditions = "keep it fresh",
            countriesWhereSold = "Italy",
            countriesImported = "USA",
            ownerImported = "Barilla",
            preparation = "Do not boil",
            purchasePlaces = "Colosseum",
            productQuantity = "100",
            productQuantityUnit = "g",
            productType = "food",
            customerService = "www.examples.com",
            imageUrl = "Not Found",
            ingredientsImageEn = "Not Found",
            carbohydrates100g = "20",
            energyKcal100g = "20",
            fat100g = "20",
            fiber100g = "20",
            proteins100g = "20",
            salt100g = "20",
            saturatedFat100g = "20",
            sodium100g = "20",
            sugars100g = "20"
        ),
        FridgeItem(
            id = 4,
            name = "Bread",
            expirationDate = "2024-12-20",
            quantity = 1,
            brand = "brand name",
            category = "category",
            allergens = "allergens",
            conservationConditions = "keep it fresh",
            countriesWhereSold = "Italy",
            countriesImported = "USA",
            ownerImported = "Barilla",
            preparation = "Do not boil",
            purchasePlaces = "Colosseum",
            productQuantity = "100",
            productQuantityUnit = "g",
            productType = "food",
            customerService = "www.examples.com",
            imageUrl = "Not Found",
            ingredientsImageEn = "Not Found",
            carbohydrates100g = "20",
            energyKcal100g = "20",
            fat100g = "20",
            fiber100g = "20",
            proteins100g = "20",
            salt100g = "20",
            saturatedFat100g = "20",
            sodium100g = "20",
            sugars100g = "20"
        )
    )

    suspend fun addOrUpdateFridgeItem(newItem: FridgeItem) {
        withContext(Dispatchers.IO) {
            val existingItem = mockItems.find { it.id == newItem.id }
            println("Existing Item: $existingItem")
            if (existingItem != null) {
                // Update the quantity if the item already exists
                mockItems[mockItems.indexOf(existingItem)] = existingItem.copy(
                    quantity = existingItem.quantity + newItem.quantity
                )
            } else {
                // Add as a new item
                mockItems.add(newItem)
            }
        }
    }


    suspend fun getFridgeItems(): List<FridgeItem> {
        // Simulate network delay
        return withContext(Dispatchers.IO) {
            // In a real scenario, make a network request here
            mockItems
        }
    }

    suspend fun getFridgeItemById(id: Long): FridgeItem? {
        return withContext(Dispatchers.IO) {
            mockItems.find { it.id == id }
        }
    }

    suspend fun deleteItemsByIds(ids: List<Long>) {
        withContext(Dispatchers.IO) {
            // Remove all items whose id is in [ids]
            mockItems.removeAll { it.id in ids }
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