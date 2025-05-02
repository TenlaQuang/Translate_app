package com.example.mobile_app.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_app.databinding.ItemHistoryBinding
import com.example.mobile_app.model.Translation
import com.example.mobile_app.model.TranslationHistory

class HistoryAdapter : ListAdapter<Translation, HistoryAdapter.ViewHolder>(DiffCallback()) {
    var onFavoriteClick: ((Translation) -> Unit)? = null
    class ViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Translation) {
            Log.d("Adapter", "Item: $item")
            binding.tvSourceLang.text = item.source_lang
            binding.tvTargetLang.text = item.target_lang
            binding.tvinput.text = item.input_text
            binding.tvTranslate.text = item.translated_text
            binding.tvDate.text = item.translated_at
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Translation>() {
        override fun areItemsTheSame(oldItem: Translation, newItem: Translation) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Translation, newItem: Translation) = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.favorite.setOnClickListener {
            onFavoriteClick?.invoke(item)
        }
        holder.bind(getItem(position))
    }
}

