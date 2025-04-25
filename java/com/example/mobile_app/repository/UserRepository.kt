package com.example.mobile_app.repository

import com.example.mobile_app.network.LoginRequest
import com.example.mobile_app.network.LoginResponse
import com.example.mobile_app.network.RetrofitClient
import retrofit2.Call

class UserRepository {
    fun login(username: String, password: String, callback: (LoginResponse?) -> Unit) {
        val request = LoginRequest(username, password)
        RetrofitClient.instance.login(request).enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: retrofit2.Response<LoginResponse>) {
                callback(response.body())
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}
