package com.example.mobile_app.model

data class PasswordRequest(
    val oldPassword: String,
    val newPassword: String
)