package com.example.mobile_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mobile_app.model.EmailCheckResponse
import com.example.mobile_app.model.EmailRequest
import com.example.mobile_app.model.PasswordRequest
import com.example.mobile_app.model.UserResponse
import com.example.mobile_app.network.RetrofitClient
import com.example.mobile_app.repository.UserRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileViewModel(private val repository: UserRepository) : ViewModel() {
    private val _user = MutableLiveData<UserResponse?>()
    val user: LiveData<UserResponse?> = _user

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _emailUpdateSuccess = MutableLiveData<Boolean>()
    val emailUpdateSuccess: LiveData<Boolean> = _emailUpdateSuccess

    private val _passwordUpdateSuccess = MutableLiveData<Boolean>()
    val passwordUpdateSuccess: LiveData<Boolean> = _passwordUpdateSuccess

    fun loadUserProfile(userId: Int) {
        repository.getUser(userId).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    _user.postValue(response.body())
                } else {
                    _error.postValue("Failed to load profile")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                _error.postValue(t.message)
            }
        })
    }

    fun updateEmail(userId: Int, email: String) {
        if (!isValidEmail(email)) {
            _error.postValue("Invalid email format")
            return
        }
        repository.checkEmail(email).enqueue(object : Callback<EmailCheckResponse> {
            override fun onResponse(call: Call<EmailCheckResponse>, response: Response<EmailCheckResponse>) {
                if (response.isSuccessful) {
                    val exists = response.body()?.exists ?: true
                    println("Email check response for $email: exists=$exists")
                    if (!exists) {
                        repository.updateEmail(userId, EmailRequest(email)).enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    _emailUpdateSuccess.postValue(true)
                                    loadUserProfile(userId) // Refresh profile
                                } else {
                                    _error.postValue("Failed to update email")
                                }
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                _error.postValue(t.message)
                            }
                        })
                    } else {
                        _error.postValue("Email already exists")
                    }
                } else {
                    _error.postValue("Failed to check email")
                }
            }

            override fun onFailure(call: Call<EmailCheckResponse>, t: Throwable) {
                _error.postValue(t.message)
            }
        })
    }

    fun changePassword(userId: Int, oldPassword: String, newPassword: String) {
        repository.changePassword(userId, PasswordRequest(oldPassword, newPassword)).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    _passwordUpdateSuccess.postValue(true)
                } else {
                    _error.postValue("Failed to change password. Old password may be incorrect.")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _error.postValue(t.message)
            }
        })
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}


