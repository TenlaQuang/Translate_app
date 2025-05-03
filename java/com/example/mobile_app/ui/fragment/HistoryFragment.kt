package com.example.mobile_app.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobile_app.databinding.FragmentHistoryBinding
import com.example.mobile_app.adapter.HistoryAdapter
import com.example.mobile_app.model.FavoriteRequest
import com.example.mobile_app.model.Translation
import com.example.mobile_app.repository.TranslateRepository
import com.example.mobile_app.viewmodel.FavoriteViewModel
import com.example.mobile_app.viewmodel.HistoryViewModel
import com.example.mobile_app.viewmodel.ViewModelFactory
import com.example.mobile_app.viewmodel.ViewModelFactoryFavorite
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HistoryViewModel
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var adapter: HistoryAdapter
    private var currentHistoryList: List<Translation> = emptyList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HistoryAdapter()
        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHistory.adapter = adapter

        val sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)
        Log.d("HistoryFragment", "userId = $userId")

        val repository = TranslateRepository()
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HistoryViewModel::class.java]
        favoriteViewModel = ViewModelProvider(this, ViewModelFactoryFavorite(repository))[FavoriteViewModel::class.java]

        if (userId != -1) {
            viewModel.loadHistory(userId)
            favoriteViewModel.loadFavorites(userId)
        }

        viewModel.historyList.observe(viewLifecycleOwner) { historyList ->
            favoriteViewModel.favoriteList.observe(viewLifecycleOwner) { favoriteList ->
                val favoriteIds = favoriteList.map { it.id }
                val updatedList = historyList.map { item ->
                    item.copy(is_favorite = if (favoriteIds.contains(item.id)) 1 else 0)
                }
                currentHistoryList = updatedList
                adapter.submitList(updatedList)
            }
        }

        adapter.onFavoriteClick = { item ->
            if (userId != -1) {
                if (item.is_favorite == 1) {
                    viewModel.removeFavorite(userId, item.id)
                    item.is_favorite = 0
                } else {
                    viewModel.addFavorite(
                        FavoriteRequest(
                            user_id = userId,
                            translation_id = item.id, // truyền ID của bản dịch gốc
                            input_text = item.input_text,
                            translated_text = item.translated_text,
                            source_lang = item.source_lang,
                            target_lang = item.target_lang
                        )
                    )

                    item.is_favorite = 1
                }

                // Tìm và cập nhật lại danh sách tạm
                val updatedList = currentHistoryList.map {
                    if (it.id == item.id) item else it
                }
                currentHistoryList = updatedList
                adapter.submitList(updatedList)

                // (Không cần notifyItemChanged nữa vì submitList đã gọi DiffUtil)
                viewModel.loadHistory(userId)
                favoriteViewModel.loadFavorites(userId)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


