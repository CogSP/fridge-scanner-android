package com.example.fridgescanner.pythonanywhereAPI

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://your-pythonanywhere-url/" // Replace with your backend URL

    val api: FridgeApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FridgeApi::class.java)
    }
}
