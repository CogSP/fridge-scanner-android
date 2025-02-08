package com.example.fridgescanner.data

// FridgeItemModels.kt
data class FridgeItemResponse(
    val success: Boolean,
    val items: List<FridgeItem>?
)

data class FridgeItemRequest(
    val fridgeid: Int
)

data class FridgeItemDetailRequest(
    val itemid: Long,
)

data class FridgeItemDetailResponse(
    val success: Boolean,
    val item: FridgeItem?
)
