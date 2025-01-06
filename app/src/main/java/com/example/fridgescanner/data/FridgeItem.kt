package com.example.fridgescanner.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class FridgeItem(
    val id: Long,
    val name: String,
    val expirationDate: String,
    val quantity: Int,
    val brand: String,
    val category: String,
    val allergens: String,
    val conservationConditions: String,
    val countriesWhereSold: String,
    val countriesImported: String,
    val ownerImported: String,
    val preparation: String,
    val purchasePlaces: String,
    val productQuantity: String,
    val productQuantityUnit: String,
    val productType: String,
    val customerService: String,
    val imageUrl: String,
    val ingredientsImageEn: String?,
    val carbohydrates100g: String?,
    val energyKcal100g: String?,
    val fat100g: String?,
    val fiber100g: String?,
    val proteins100g: String?,
    val salt100g: String?,
    val saturatedFat100g: String?,
    val sodium100g: String?,
    val sugars100g: String?
) {
    fun isExpiringSoon(thresholdDays: Long): Boolean {
        return try {
            // Adjust the pattern to match your date string format:
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val expireDate = LocalDate.parse(expirationDate, formatter)
            val today = LocalDate.now()
            val daysUntilExpiration = ChronoUnit.DAYS.between(today, expireDate)

            // Return true if item is within [thresholdDays] from expiring
            daysUntilExpiration in 0..thresholdDays
        } catch (e: Exception) {
            false
        }
    }

    // FridgeItem.kt (for example)
    fun isExpired(): Boolean {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val expireDate = LocalDate.parse(expirationDate, formatter)
            val today = LocalDate.now()
            ChronoUnit.DAYS.between(today, expireDate) < 0
        } catch (e: Exception) {
            false
        }
    }
}