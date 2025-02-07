// ProductModels.kt
package com.example.fridgescanner.data

data class ProductRequest(
    val id: String
)

data class ProductInformation(
    val id: Long,
    val name: String,
    val brand: String,
    val allergens: String,
    val energy: Double,
    val kcal: Double,
    val charbo: Double,
    val sugar: Double,
    val fibers: Double,
    val fat: Double,
    val saturated_fat: Double,
    val salt: Double,
    val sodium: Double,
    val proteins: Double,
    val vegetarian: Int,
    val vegan: Int,
    val nutriscore: String
)

data class ProductResponse(
    val success: Boolean,
    val product_information: ProductInformation?
)
