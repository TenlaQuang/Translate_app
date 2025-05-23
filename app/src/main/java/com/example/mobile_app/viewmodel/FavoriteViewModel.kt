package com.example.mobile_app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_app.model.Favorite
import com.example.mobile_app.model.FavoriteRequest
import com.example.mobile_app.model.Translation
import com.example.mobile_app.repository.TranslateRepository
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: TranslateRepository) : ViewModel() {

    private val _favoriteList = MutableLiveData<List<Favorite>>()
    val favoriteList: LiveData<List<Favorite>> = _favoriteList

    fun loadFavorites(userId: Int) {
        viewModelScope.launch {
            try {
                val favorites = repository.fetchFavorites(userId)
                _favoriteList.value = favorites
            } catch (e: Exception) {
                Log.e("FavoriteViewModel", "Lỗi tải favorite: ${e.message}", e)
            }
        }
    }

    fun removeFavorite(userId: Int, translationId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.removeFavorite(userId, translationId)
                onSuccess()
            } catch (e: Exception) {
                Log.e("FavoriteViewModel", "Lỗi xoá favorite: ${e.message}")
            }
        }
    }

}

