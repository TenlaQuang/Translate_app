package com.example.mobile_app.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_app.R
import com.example.mobile_app.databinding.ItemHistoryBinding
import com.example.mobile_app.model.Translation
import com.example.mobile_app.model.TranslationHistory

class HistoryAdapter : ListAdapter<Translation, HistoryAdapter.ViewHolder>(DiffCallback()) {

    var onFavoriteClick: ((Translation) -> Unit)? = null

    inner class ViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Translation) {
            binding.tvSourceLang.text = item.source_lang
            binding.tvTargetLang.text = item.target_lang
            binding.tvinput.text = item.input_text
            binding.tvTranslate.text = item.translated_text
            binding.tvDate.text = item.translated_at

            // Gắn icon favorite đúng theo trạng thái
            binding.starIcon.setImageResource(
                if (item.is_favorite == 1) R.drawable.ic_star else R.drawable.ic_favorite
            )

            // Gắn sự kiện click ngôi sao
            binding.starIcon.setOnClickListener {
                if (item.is_favorite == 0) {
                    AlertDialog.Builder(binding.root.context)
                        .setMessage("Add to favorite?")
                        .setPositiveButton("yes") { _, _ -> onFavoriteClick?.invoke(item) }
                        .setNegativeButton("no", null)
                        .show()
                }else{

                }
            }
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
        holder.bind(getItem(position))  // Không nên gọi item ở đây nữa
    }
}

