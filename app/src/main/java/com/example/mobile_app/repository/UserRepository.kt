package com.example.mobile_app.repository

import android.util.Log
import com.example.mobile_app.R
import com.example.mobile_app.model.CheckRequest
import com.example.mobile_app.model.CheckResponse
import com.example.mobile_app.model.EmailCheckResponse
import com.example.mobile_app.model.EmailRequest
import com.example.mobile_app.model.LoginRequest
import com.example.mobile_app.model.LoginResponse
import com.example.mobile_app.model.PasswordRequest
import com.example.mobile_app.model.RegisterRequest
import com.example.mobile_app.model.RegisterResponse
import com.example.mobile_app.model.User
import com.example.mobile_app.model.UserResponse
import com.example.mobile_app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
    // Đăng ký người dùng
    fun checkEmailExists(email: String, callback: (Boolean, String?) -> Unit) {
        val request = CheckRequest(email = email)
        Log.d("UserRepository", "Sending checkEmail request: $request")
        RetrofitClient.instance.checkEmail(request).enqueue(object : Callback<CheckResponse> {
            override fun onResponse(call: Call<CheckResponse>, response: Response<CheckResponse>) {
                Log.d("UserRepository", "Received checkEmail response: ${response.body()}, code: ${response.code()}")
                if (response.isSuccessful) {
                    val checkResponse = response.body()
                    callback(checkResponse?.exists ?: false, null)
                } else {
                    callback(false, "Failed to check email: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CheckResponse>, t: Throwable) {
                Log.e("UserRepository", "Error checking email: ${t.message}")
                callback(false, "Error checking email: ${t.message}")
            }
        })
    }

    fun checkUsernameExists(username: String, callback: (Boolean, String?) -> Unit) {
        val request = CheckRequest(username = username)
        Log.d("UserRepository", "Sending checkUsername request: $request")
        RetrofitClient.instance.checkUsername(request).enqueue(object : Callback<CheckResponse> {
            override fun onResponse(call: Call<CheckResponse>, response: Response<CheckResponse>) {
                Log.d("UserRepository", "Received checkUsername response: ${response.body()}, code: ${response.code()}")
                if (response.isSuccessful) {
                    val checkResponse = response.body()
                    callback(checkResponse?.exists ?: false, null)
                } else {
                    callback(false, "Failed to check username: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CheckResponse>, t: Throwable) {
                Log.e("UserRepository", "Error checking username: ${t.message}")
                callback(false, "Error checking username: ${t.message}")
            }
        })
    }

    fun register(username: String, password: String, email: String, callback: (RegisterResponse?) -> Unit) {
        Log.d("UserRepository", "Register called with username: $username, email: $email")
        val request = RegisterRequest(username, password, email)
        RetrofitClient.instance.register(request).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                Log.d("UserRepository", "Received register response: ${response.body()}, code: ${response.code()}")
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(RegisterResponse(false, "Failed to register: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.e("UserRepository", "Error registering: ${t.message}")
                callback(RegisterResponse(false, "Error registering: ${t.message}"))
            }
        })
    }
    fun getUser(userId: Int): Call<UserResponse> {
        return RetrofitClient.instance.getUser(userId)
    }

    fun updateEmail(userId: Int, emailRequest: EmailRequest): Call<Void> {
        return RetrofitClient.instance.updateEmail(userId, emailRequest)
    }

    fun changePassword(userId: Int, passwordRequest: PasswordRequest): Call<Void> {
        return RetrofitClient.instance.changePassword(userId, passwordRequest)
    }

    fun checkEmail(email: String): Call<EmailCheckResponse> {
        return RetrofitClient.instance.checkEmail(email)
    }

}
