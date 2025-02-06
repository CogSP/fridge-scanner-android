// ApiClient.kt
package com.example.fridgescanner.pythonanywhereAPI

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://filowastaken.pythonanywhere.com/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

    val fridgeApiService: FridgeApiService by lazy {
        retrofit.create(FridgeApiService::class.java)
    }
}
