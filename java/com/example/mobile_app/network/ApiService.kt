package com.example.mobile_app.network


import com.example.mobile_app.model.CheckResponse
import com.example.mobile_app.model.FavoriteRequest
import com.example.mobile_app.model.FavoriteResponse
import com.example.mobile_app.model.LoginRequest
import com.example.mobile_app.model.LoginResponse
import com.example.mobile_app.model.RegisterRequest
import com.example.mobile_app.model.RegisterResponse
import com.example.mobile_app.model.TranslateRequest
import com.example.mobile_app.model.TranslateResponse
import com.example.mobile_app.model.Translation
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


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
    @GET("/api/history/{userId}")
    suspend fun getHistory(@Path("userId") userId: Int): List<Translation>
    @POST("/api/favorite")
    fun addFavorite(@Body favoriteRequest: FavoriteRequest): Call<FavoriteResponse>
    @GET("/api/favorites/{userId}")
    fun getFavorites(@Path("userId") userId: Int): Call<List<Translation>>

}