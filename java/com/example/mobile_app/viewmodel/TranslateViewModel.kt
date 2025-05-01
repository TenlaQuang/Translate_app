package com.example.mobile_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobile_app.network.ApiService
import com.example.mobile_app.model.TranslateRequest
import com.example.mobile_app.model.TranslateResponse
import com.example.mobile_app.repository.TranslateRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TranslateViewModel : ViewModel() {
    private val translateRepository = TranslateRepository()

    // LiveData để quan sát kết quả dịch
    val translatedText: LiveData<String> get() = _translatedText
    private val _translatedText = MutableLiveData<String>()

    // LiveData để quan sát lỗi
    val error: LiveData<String> get() = _error
    private val _error = MutableLiveData<String>()
    private val repository = TranslateRepository()

    fun translateText(text: String, source: String, target: String) {
        repository.translate(text, source, target).observeForever { result ->
            result.onSuccess {
                _translatedText.value = it
            }
            result.onFailure {
                _error.value = it.message ?: "Lỗi không xác định"
            }
        }
    }
}
