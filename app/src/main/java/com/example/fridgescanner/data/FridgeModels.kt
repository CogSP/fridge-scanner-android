// FridgeModels.kt
package com.example.fridgescanner.data

data class CreateFridgeRequest(
    val username: String,
    val name: String,
    val color: String
)

data class CreateFridgeResponse(
    val success: Boolean,
    val message: String?,
    val fridgeId: Int?
)


data class Fridge(
    val id: Long,
    val name: String,
    val color: String, // e.g. a hex string like "#FF0000"
)
