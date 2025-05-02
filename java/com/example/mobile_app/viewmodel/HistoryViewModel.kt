package com.example.mobile_app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_app.model.Translation
import com.example.mobile_app.repository.TranslateRepository
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: TranslateRepository) : ViewModel() {

    private val _historyList = MutableLiveData<List<Translation>>()
    val historyList: LiveData<List<Translation>> get() = _historyList

    fun loadHistory(userId: Int) {
        viewModelScope.launch {
            try {
                Log.d("HistoryViewModel", "Loading history for userId: $userId")
                val result = repository.fetchHistory(userId)
                _historyList.postValue(result)
            } catch (e: Exception) {
                Log.e("HistoryViewModel", "Lỗi: ${e.message}")
                _historyList.postValue(emptyList())
            }
        }
    }
}

