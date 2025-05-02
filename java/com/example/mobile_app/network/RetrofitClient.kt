package com.example.mobile_app.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.9:3000"

    // Khởi tạo Retrofit chỉ khi cần
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Phương thức dùng để login, gọi API từ ở ngoài
    val instance: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
