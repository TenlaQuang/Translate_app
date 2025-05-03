package com.example.mobile_app.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobile_app.R
import com.example.mobile_app.adapter.HistoryAdapter
import com.example.mobile_app.databinding.FragmentFavoriteBinding
import com.example.mobile_app.repository.TranslateRepository
import com.example.mobile_app.viewmodel.FavoriteViewModel
import com.example.mobile_app.viewmodel.ViewModelFactoryFavorite

class FavoriteFragment : Fragment() {

    private lateinit var viewModel: FavoriteViewModel
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        val repository = TranslateRepository()
        viewModel = ViewModelProvider(this, ViewModelFactoryFavorite(repository))[FavoriteViewModel::class.java]

        adapter = HistoryAdapter()
        binding.recyclerViewFavorite.adapter = adapter
        binding.recyclerViewFavorite.layoutManager = LinearLayoutManager(requireContext())

        val userId = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE).getInt("user_id", -1)
        viewModel.loadFavorites(userId)

        viewModel.favoriteList.observe(viewLifecycleOwner) { favorites ->
            val updated = favorites.map { it.copy(is_favorite = 1) }
            adapter.submitList(updated)
        }

        return binding.root
    }
}

