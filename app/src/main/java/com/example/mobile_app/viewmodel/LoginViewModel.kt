package com.example.mobile_app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobile_app.model.LoginResponse
import com.example.mobile_app.repository.UserRepository

class LoginViewModel : ViewModel() {
    private val repo = UserRepository()
    val loginResult = MutableLiveData<LoginResponse>()

    fun login(username: String, password: String) {
        repo.login(username, password) {
            loginResult.postValue(it)
        }
    }
}