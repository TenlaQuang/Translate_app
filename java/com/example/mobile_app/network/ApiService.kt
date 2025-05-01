package com.example.mobile_app.network


import com.example.mobile_app.model.LoginRequest
import com.example.mobile_app.model.LoginResponse
import com.example.mobile_app.model.TranslateRequest
import com.example.mobile_app.model.TranslateResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class RegisterRequest(val username: String, val password: String,val email: String)
data class RegisterResponse(val success: Boolean, val message: String)
data class CheckResponse(val exists: Boolean, val message: String)

interface ApiService {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
    @POST("register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>
    @POST("/check-email")
    fun checkEmailExists(@Body email: String): Call<CheckResponse>
    @POST("/check-username")
    fun checkUsernameExists(@Body username: String): Call<CheckResponse>
    @POST("/api/translate")
    fun translateText(@Body request: TranslateRequest): Call<TranslateResponse>
}