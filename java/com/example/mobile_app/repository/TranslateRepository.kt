package com.example.mobile_app.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mobile_app.model.TranslateRequest
import com.example.mobile_app.model.TranslateResponse
import com.example.mobile_app.model.Translation
import com.example.mobile_app.network.ApiService
import com.example.mobile_app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TranslateRepository{

    fun translate(text: String, source: String, target: String, user_id: Int): LiveData<Result<String>> {
        val result = MutableLiveData<Result<String>>()

        val request = TranslateRequest(q = text, source = source, target = target, user_id = user_id)

        RetrofitClient.instance.translateText(request).enqueue(object : Callback<TranslateResponse> {
            override fun onResponse(call: Call<TranslateResponse>, response: Response<TranslateResponse>) {
                if (response.isSuccessful) {
                    result.value = Result.success(response.body()?.translatedText ?: "")
                } else {
                    result.value = Result.failure(Throwable("Lỗi phản hồi từ server"))
                }
            }

            override fun onFailure(call: Call<TranslateResponse>, t: Throwable) {
                result.value = Result.failure(t)
            }
        })

        return result
    }

    suspend fun fetchHistory(userId: Int): List<Translation> {
        val history = RetrofitClient.instance.getHistory(userId)
        Log.d("TranslateRepository", "Dữ liệu từ server: $history")
        return history
    }
    suspend fun fetchFavorites(userId: Int): List<Translation> {
        val favorites = RetrofitClient.instance.getFavorites(userId)
        return favorites
    }
}
