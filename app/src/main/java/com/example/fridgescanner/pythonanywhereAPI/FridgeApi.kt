package com.example.fridgescanner.pythonanywhereAPI

import com.example.fridgescanner.data.FridgeItem
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


// this is the interface for PythonAnywhere
interface FridgeApi {
    @GET("fridge_items")
    suspend fun getFridgeItems(): List<FridgeItem>

    @POST("fridge_items")
    suspend fun addFridgeItem(@Body item: FridgeItem): FridgeItem

    // ... other endpoints for updating and deleting items
}