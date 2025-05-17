package com.example.mobile_app.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobile_app.R
import com.example.mobile_app.adapter.FavoriteAdapter
import com.example.mobile_app.adapter.HistoryAdapter
import com.example.mobile_app.databinding.FragmentFavoriteBinding
import com.example.mobile_app.model.Favorite
import com.example.mobile_app.repository.TranslateRepository
import com.example.mobile_app.viewmodel.FavoriteViewModel
import com.example.mobile_app.viewmodel.ViewModelFactoryFavorite

class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var adapter: FavoriteAdapter
    private lateinit var viewModel: FavoriteViewModel
    private var userId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lấy userId từ SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = sharedPref.getInt("user_id", -1)
        Log.d("FavoriteFragment", "userId = $userId")

        // Khởi tạo ViewModel
        val repository = TranslateRepository()
        viewModel = ViewModelProvider(this, ViewModelFactoryFavorite(repository))[FavoriteViewModel::class.java]

        // Thiết lập RecyclerView với adapter có callback xóa favorite
        adapter = FavoriteAdapter { favoriteItem ->
            showConfirmDeleteDialog(favoriteItem)
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Quan sát danh sách yêu thích
        viewModel.favoriteList.observe(viewLifecycleOwner) { favorites ->
            adapter.submitList(favorites)
        }

        // Nếu đã đăng nhập thì load dữ liệu
        if (userId != -1) {
            viewModel.loadFavorites(userId)
        } else {
            Toast.makeText(requireContext(), "Chưa đăng nhập", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showConfirmDeleteDialog(favorite: Favorite) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận")
            .setMessage("Bạn có chắc muốn xóa khỏi yêu thích?")
            .setPositiveButton("Có") { _, _ ->
                viewModel.removeFavorite(favorite.user_id, favorite.translation_id) {
                    Toast.makeText(requireContext(), "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show()
                    viewModel.loadFavorites(userId)
                }
            }
            .setNegativeButton("Không", null)
            .show()
    }
}


