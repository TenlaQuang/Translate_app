package com.example.mobile_app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobile_app.network.ApiService
import com.example.mobile_app.network.CheckResponse
import com.example.mobile_app.network.RegisterResponse
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

    // Kiểm tra email đã tồn tại chưa
    fun checkEmailExists(email: String) {
        userRepository.checkEmailExists(email) { exists, message ->
            emailExists.postValue(exists) // Gửi kết quả về UI
            if (exists) {
                errorMessage.postValue(message ?: "Email đã tồn tại")
            } else {
                // Nếu email không tồn tại, tiếp tục kiểm tra username
                val username = "some-username"  // Cập nhật từ input username
                checkUsernameExists(username)
            }
        }
    }

    // Kiểm tra username đã tồn tại chưa
    fun checkUsernameExists(username: String) {
        userRepository.checkUsernameExists(username) { exists, message ->
            usernameExists.postValue(exists) // Gửi kết quả về UI
            if (exists) {
                errorMessage.postValue(message ?: "Tên người dùng đã tồn tại")
            } else {
                // Tiến hành đăng ký người dùng nếu cả email và username không tồn tại
                register("some-username", "some-password", "some-email")
            }
        }
    }

    // Đăng ký người dùng mới
    fun register( username: String, password: String,email: String) {
        userRepository.register(username, password, email) { response ->
            registerResult.postValue(response)
        }
    }
}