// AuthService.kt
package com.example.fridgescanner.pythonanywhereAPI

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String?,
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class RegisterResponse(
    val success: Boolean,
    val message: String?
)

data class User(
    val username: String,
    val token: String?
)

data class ForgotPasswordResponse(
    val success: Boolean,
    val message: String?
)


data class ResetPasswordRequest(
    val email: String,
    val reset_code: String,
    val new_password: String,
)


data class ResetPasswordResponse(
    val success: Boolean,
    val message: String?
)




interface AuthService {
    @POST("api/user/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/user/create")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/user/forgot")
    suspend fun forgotPassword(@Body request: Map<String, String>): Response<ForgotPasswordResponse>

    @POST("api/user/reset")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<ResetPasswordResponse>
}
