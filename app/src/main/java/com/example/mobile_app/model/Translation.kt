package com.example.mobile_app.model

data class Translation(
    val id: Int,
    val input_text: String,
    val source_lang: String,
    val target_lang: String,
    val translated_text: String,
    val translated_at: String,
    var is_favorite: Int = 0
)
