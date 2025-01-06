package com.example.fridgescanner.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FridgeRepository {

    // Mock data
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

}