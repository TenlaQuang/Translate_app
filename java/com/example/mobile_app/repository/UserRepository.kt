package com.example.mobile_app.repository

import com.example.mobile_app.model.CheckResponse
import com.example.mobile_app.model.EmailCheckResponse
import com.example.mobile_app.model.EmailRequest
import com.example.mobile_app.model.LoginRequest
import com.example.mobile_app.model.LoginResponse
import com.example.mobile_app.model.PasswordRequest
import com.example.mobile_app.model.RegisterRequest
import com.example.mobile_app.model.RegisterResponse
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
    fun register(username: String, password: String, email: String, callback: (RegisterResponse?) -> Unit) {
        val registerRequest = RegisterRequest(username, password, email)
        RetrofitClient.instance.register(registerRequest).enqueue(object : retrofit2.Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: retrofit2.Response<RegisterResponse>) {
                callback(response.body())
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    // Kiểm tra email có tồn tại không
    fun checkEmailExists(email: String, callback: (Boolean, String?) -> Unit) {
        RetrofitClient.instance.checkEmailExists(email).enqueue(object : retrofit2.Callback<CheckResponse> {
            override fun onResponse(call: Call<CheckResponse>, response: retrofit2.Response<CheckResponse>) {
                if (response.isSuccessful) {
                    val exists = response.body()?.exists ?: false
                    callback(exists, response.body()?.message) // Trả về kết quả và thông báo
                } else {
                    // Nếu trả về lỗi từ backend, lấy thông báo lỗi từ server
                    val errorMessage = response.errorBody()?.string() ?: "Lỗi khi kiểm tra email"
                    callback(false, errorMessage)
                }
            }

            override fun onFailure(call: Call<CheckResponse>, t: Throwable) {
                callback(false, t.message) // Nếu lỗi kết nối
            }
        })
    }

    // Kiểm tra username có tồn tại không
    fun checkUsernameExists(username: String, callback: (Boolean, String?) -> Unit) {
        RetrofitClient.instance.checkUsernameExists(username).enqueue(object : retrofit2.Callback<CheckResponse> {
            override fun onResponse(call: Call<CheckResponse>, response: retrofit2.Response<CheckResponse>) {
                if (response.isSuccessful) {
                    val exists = response.body()?.exists ?: false
                    callback(exists, response.body()?.message) // Trả về kết quả và thông báo
                } else {
                    // Nếu trả về lỗi từ backend, lấy thông báo lỗi từ server
                    val errorMessage = response.errorBody()?.string() ?: "Lỗi khi kiểm tra tên người dùng"
                    callback(false, errorMessage)
                }
            }

            override fun onFailure(call: Call<CheckResponse>, t: Throwable) {
                callback(false, t.message) // Nếu lỗi kết nối
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
