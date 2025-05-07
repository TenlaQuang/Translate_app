package com.example.mobile_app.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.mobile_app.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Thiết lập toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Profile"

        // Lấy thông tin người dùng từ SharedPreferences
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "Chưa đăng nhập")
        val userId = sharedPref.getInt("user_id", -1)

        // Hiển thị thông tin lên giao diện
        binding.tvUsername.text = "Tên: $username"
        binding.tvUserId.text = "ID: $userId"
    }

    // Xử lý nút back trên toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // Quay lại màn hình trước
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
