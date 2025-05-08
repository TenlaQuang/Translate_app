package com.example.mobile_app.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.mobile_app.R
import com.example.mobile_app.repository.UserRepository
import com.example.mobile_app.viewmodel.ProfileViewModel
import com.example.mobile_app.viewmodel.ProfileViewModelFactory

class ProfileActivity : AppCompatActivity() {
    private lateinit var tvUsername: TextView
    private lateinit var btnEmail: Button
    private lateinit var btnChangePassword: Button
    private lateinit var tvEmail: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize ViewModel
        val viewModel: ProfileViewModel by viewModels {
            ProfileViewModelFactory(UserRepository())
        }

        // Initialize views
        tvUsername = findViewById(R.id.username_text)
        tvEmail = findViewById(R.id.email_text)
        btnEmail = findViewById(R.id.btnEmail)
        btnChangePassword = findViewById(R.id.btnChangePassword)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }


        // Get user_id from SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Load and display user profile immediately
        viewModel.loadUserProfile(userId)

        // Observe ViewModel data
        viewModel.user.observe(this) { user ->
            user?.let {
                tvUsername.text = it.username
                tvEmail.text = it.email ?: "No email set"
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }

        viewModel.emailUpdateSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Email updated successfully", Toast.LENGTH_SHORT).show()
                viewModel.loadUserProfile(userId) // Refresh profile
            }
        }

        viewModel.passwordUpdateSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
            }
        }

        // Email button click
        btnEmail.setOnClickListener {
            showEmailPopup(userId, viewModel)
        }

        // Change password button click
        btnChangePassword.setOnClickListener {
            showOldPasswordPopup(userId, viewModel)
        }
    }

    private fun showEmailPopup(userId: Int, viewModel: ProfileViewModel) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_email, null)
        val etEmail = dialogView.findViewById<EditText>(R.id.etEmail)

        builder.setView(dialogView)
            .setTitle("Update Email")
            .setPositiveButton("Save") { _, _ ->
                val email = etEmail.text.toString().trim()
                if (email.isNotEmpty()) {
                    viewModel.updateEmail(userId, email)
                } else {
                    Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showOldPasswordPopup(userId: Int, viewModel: ProfileViewModel) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_password, null)
        val etPassword = dialogView.findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = dialogView.findViewById<EditText>(R.id.etConfirmPassword)
        etPassword.hint = "Enter old password"
        etConfirmPassword.visibility = android.view.View.GONE

        builder.setView(dialogView)
            .setTitle("Verify Old Password")
            .setPositiveButton("Verify") { _, _ ->
                val oldPassword = etPassword.text.toString().trim()
                if (oldPassword.isNotEmpty()) {
                    showNewPasswordPopup(userId, oldPassword, viewModel)
                } else {
                    Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showNewPasswordPopup(userId: Int, oldPassword: String, viewModel: ProfileViewModel) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_password, null)
        val etPassword = dialogView.findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = dialogView.findViewById<EditText>(R.id.etConfirmPassword)
        etPassword.hint = "Enter new password"
        etConfirmPassword.visibility = android.view.View.VISIBLE

        builder.setView(dialogView)
            .setTitle("Set New Password")
            .setPositiveButton("Save") { _, _ ->
                val newPassword = etPassword.text.toString().trim()
                val confirmPassword = etConfirmPassword.text.toString().trim()
                if (newPassword.isNotEmpty() && confirmPassword.isNotEmpty()) {
                    if (newPassword == confirmPassword) {
                        if (newPassword.length >= 6) {
                            viewModel.changePassword(userId, oldPassword, newPassword)
                        } else {
                            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}

