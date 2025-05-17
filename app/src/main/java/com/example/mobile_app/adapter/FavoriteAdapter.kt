package com.example.mobile_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_app.R
import com.example.mobile_app.databinding.ItemFavoriteBinding
import com.example.mobile_app.model.Favorite

class FavoriteAdapter(
    private val onRemoveFavorite: (Favorite) -> Unit
) : ListAdapter<Favorite, FavoriteAdapter.FavoriteViewHolder>(DiffCallback()) {

    inner class FavoriteViewHolder(val binding: ItemFavoriteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Favorite) {
            binding.SourceLang.text = item.source_lang
            binding.InputText.text = item.input_text
            binding.TargetLang.text = item.target_lang
            binding.TranslatedText.text = item.translated_text
            binding.starIC.setImageResource(R.drawable.ic_favorited)
            binding.starIC.setOnClickListener {
                onRemoveFavorite(item)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Favorite>() {
        override fun areItemsTheSame(oldItem: Favorite, newItem: Favorite) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Favorite, newItem: Favorite) = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

