package com.example.mobile_app.ui.fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.mobile_app.R
import com.example.mobile_app.databinding.ActivityHomeBinding
import androidx.appcompat.app.ActionBarDrawerToggle

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Khởi tạo binding
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Gắn toolbar
        setSupportActionBar(binding.toolbar2)

        // Navigation Drawer
        drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar2,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        // Load Fragment mặc định
        loadFragment(TranslateFragment())
        // Bottom Navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_mic -> loadFragment(MicFragment())
                R.id.nav_camera -> loadFragment(CameraFragment())
                R.id.nav_translate -> loadFragment(TranslateFragment())
                R.id.nav_history -> loadFragment(HistoryFragment())
                R.id.nav_favorite -> loadFragment(FavoriteFragment())
                else -> false
            }
        }

        // Nav Drawer actions
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    // TODO: Mở ProfileActivity chẳng hạn
                }
                R.id.nav_logout -> {
                    // TODO: Đăng xuất và chuyển về màn hình Login
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
