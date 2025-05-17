package com.example.mobile_app.model

data class TranslationHistory(
    val originalText: String,
    val translatedText: String,
    val sourceLang: String,
    val targetLang: String
)