package com.example.mobile_app.model

data class TranslateRequest(
    val q: String,
    val source: String,
    val target: String
)