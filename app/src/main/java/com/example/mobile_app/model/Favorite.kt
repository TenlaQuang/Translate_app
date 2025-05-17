package com.example.mobile_app.model

data class Favorite(
    val id: Int, // id tự tăng của bảng favorites
    val translation_id: Int,
    val user_id: Int,
    val input_text: String,
    val translated_text: String,
    val source_lang: String,
    val target_lang: String,
    val created_at: String
)
