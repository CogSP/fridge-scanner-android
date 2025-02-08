package com.example.fridgescanner.data

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class FridgeItem(
    val id: Long,
    val fridge_id: Int,
    val name: String,
    val expiry_date: String,
    val added_at: String,
    val quantity: Int,
    val brand: String,
    //val category: String,
    val allergens: String,
//    val conservationConditions: String,
//    val countriesWhereSold: String,
//    val countriesImported: String,
//    val ownerImported: String,
//    val preparation: String,
//    val purchasePlaces: String,
//    val productQuantity: String,
//    val productQuantityUnit: String,
//    val productType: String,
//    val customerService: String,
//    val imageUrl: String,
//    val ingredientsImageEn: String?,
    val energy: String?, //energyKcal100g
    val kcal: String?, //TODO: Check this one
    val charbo: String?, //carbohydrates100g
    val sugar: String?, //sugars100g
    val fibers: String?, //fiber100g
    val fat: String?, //fat100g
    val saturated_fat: String?, //saturatedFat100g
    val salt: String?, //salt100g
    val sodium: String?, //sodium100g
    val proteins: String?, //proteins100g
    val vegetarian: Int?,
    val vegan: Int?,
    val nutriscore_grade: Char?,
    val conservationConditions: String?,
    val countriesWhereSold: String?,
    val ownerImported: String?,
    val preparation: String?
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun isExpiringSoon(thresholdDays: Long): Boolean {
        return try {
            // Adjust the pattern to match your date string format:
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val expireDate = LocalDate.parse(expiry_date, formatter)
            val today = LocalDate.now()
            val daysUntilExpiration = ChronoUnit.DAYS.between(today, expireDate)

            // Return true if item is within [thresholdDays] from expiring
            daysUntilExpiration in 0..thresholdDays
        } catch (e: Exception) {
            false
        }
    }

    // FridgeItem.kt (for example)
    @RequiresApi(Build.VERSION_CODES.O)
    fun isExpired(): Boolean {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val expireDate = LocalDate.parse(expiry_date, formatter)
            val today = LocalDate.now()
            ChronoUnit.DAYS.between(today, expireDate) < 0
        } catch (e: Exception) {
            false
        }
    }
}