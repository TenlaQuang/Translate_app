package com.example.mobile_app.model

data class FavoriteRequest(
    val user_id: Int,
    val translation_id: Int,
    val input_text: String,
    val translated_text: String,
    val source_lang: String,
    val target_lang: String
)

