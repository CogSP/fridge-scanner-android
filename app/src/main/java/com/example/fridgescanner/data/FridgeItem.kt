package com.example.fridgescanner.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class FridgeItem(
    val id: Int,
    val name: String,
    val expirationDate: String,
    val quantity: Int
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