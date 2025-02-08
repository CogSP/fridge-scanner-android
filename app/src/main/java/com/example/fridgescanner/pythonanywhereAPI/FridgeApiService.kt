// FridgeApiService.kt
package com.example.fridgescanner.pythonanywhereAPI

import com.example.fridgescanner.data.CreateFridgeRequest
import com.example.fridgescanner.data.CreateFridgeResponse
import com.example.fridgescanner.data.Fridge
import com.example.fridgescanner.data.FridgeItem
import com.example.fridgescanner.data.FridgeItemDetailRequest
import com.example.fridgescanner.data.FridgeItemDetailResponse
import com.example.fridgescanner.data.FridgeItemRemoveRequest
import com.example.fridgescanner.data.FridgeItemRequest
import com.example.fridgescanner.data.FridgeItemResponse
import com.example.fridgescanner.data.GenericResponse
import com.example.fridgescanner.data.ProductRequest
import com.example.fridgescanner.data.ProductResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


// For example, in FridgeApiService.kt or in a separate file
data class FridgeUserRequest(
    val username: String
)

// PythonAnywhere returns a list of dictionaries, where each dictionary has id, name and color of the fridge
data class FridgeUserResponse(
    val success: Boolean,
    val message: String,
    val fridges: List<Fridge>?
)

interface FridgeApiService {

    // New endpoint to get the list of fridges for a user.
    @POST("api/fridges/get")
    suspend fun getFridgesForUser(
        @Body request: FridgeUserRequest
    ): Response<FridgeUserResponse>

    // New endpoint to create a fridge.
    @POST("api/fridge/create")
    suspend fun createFridge(
        @Body request: CreateFridgeRequest
    ): Response<CreateFridgeResponse>

    // Get a product
    @POST("api/product")
    suspend fun getProduct(
        @Body request: ProductRequest
    ): Response<ProductResponse>

    @POST("api/fridgeitems/get")
    suspend fun getFridgeItems(
        @Body request: FridgeItemRequest
    ): Response<FridgeItemResponse>

    @POST("api/fridgeitemdetail/get")
    suspend fun getFridgeItemById(
        @Body request: FridgeItemDetailRequest
    ): Response<FridgeItemDetailResponse>

    @POST("api/fridgeitem/remove")
    suspend fun removeFridgeItem(
        @Body request: FridgeItemRemoveRequest
    ): Response<GenericResponse>
}
