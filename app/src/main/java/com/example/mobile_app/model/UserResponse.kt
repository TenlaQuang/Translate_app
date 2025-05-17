package com.example.mobile_app.model

data class UserResponse(
    val id: Int, // Thay `id` bằng `user_id` từ API
    val username: String,
    val email: String?
)