package com.example.mobile_app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobile_app.model.RegisterResponse
import com.example.mobile_app.network.ApiService
import com.example.mobile_app.repository.UserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {

    private val userRepository = UserRepository()

    val registerResult = MutableLiveData<RegisterResponse?>()
    val emailExists = MutableLiveData<Boolean>()
    val usernameExists = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    private var username: String = ""
    private var password: String = ""
    private var email: String = ""

    // Lưu thông tin người dùng từ UI
    fun setUserDetails(username: String, password: String, email: String) {
        this.username = username
        this.password = password
        this.email = email
    }

    // Kiểm tra email đã tồn tại chưa
    fun checkEmailExists() {
        if (email.isEmpty()) {
            errorMessage.postValue("Email cannot be empty")
            return
        }
        userRepository.checkEmailExists(email) { exists, message ->
            emailExists.postValue(exists)
            if (exists) {
                errorMessage.postValue(message ?: "Email đã tồn tại")
            }
        }
    }

    // Kiểm tra username đã tồn tại chưa
    fun checkUsernameExists() {
        if (username.isEmpty()) {
            errorMessage.postValue("Username cannot be empty")
            return
        }
        userRepository.checkUsernameExists(username) { exists, message ->
            usernameExists.postValue(exists)
            if (exists) {
                errorMessage.postValue(message ?: "Tên người dùng đã tồn tại")
            }
        }
    }

    // Đăng ký người dùng mới
    fun register() {
        if (password.isEmpty()) {
            errorMessage.postValue("Password cannot be empty")
            return
        }
        userRepository.register(username, password, email) { response ->
            registerResult.postValue(response)
        }
    }
}