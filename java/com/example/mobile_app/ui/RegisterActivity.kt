package com.example.mobile_app.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mobile_app.R
import com.example.mobile_app.viewmodel.RegisterViewModel
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)

        registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        val emailEditText = findViewById<TextInputEditText>(R.id.email)
        val passwordEditText = findViewById<TextInputEditText>(R.id.password_sign)
        val confirmPasswordEditText = findViewById<TextInputEditText>(R.id.confirm_password)
        val registerButton = findViewById<Button>(R.id.sign_btn)

        // Bắt sự kiện khi nhấn nút Đăng ký
        registerButton.setOnClickListener {

            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            // Kiểm tra email và mật khẩu xác nhận
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Mật khẩu và mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kiểm tra email có tồn tại không
            registerViewModel.checkEmailExists(email)
        }

        // Lắng nghe kết quả kiểm tra email
        registerViewModel.emailExists.observe(this, { exists ->
            if (exists) {
                Toast.makeText(this, "Email đã tồn tại", Toast.LENGTH_SHORT).show()
            } else {
                // Hiển thị Popup yêu cầu nhập username
                val dialogView = layoutInflater.inflate(R.layout.dialog_username_input, null)
                val usernameInput = dialogView.findViewById<TextInputEditText>(R.id.username_sign)

                val usernameDialog = AlertDialog.Builder(this)
                    .setTitle("Nhập tên người dùng")
                    .setView(dialogView)
                    .setPositiveButton("OK") { _, _ ->
                        // Lấy giá trị username khi người dùng nhấn OK
                        val username = usernameInput.text.toString().trim()

                        if (username.isEmpty()) {
                            Toast.makeText(this, "Vui lòng nhập tên người dùng", Toast.LENGTH_SHORT).show()
                        } else {
                            // Kiểm tra username có tồn tại không
                            registerViewModel.checkUsernameExists(username)
                        }
                    }
                    .setNegativeButton("Hủy", null)
                    .create()
                usernameDialog.show()
            }
        })
        val dialogView = layoutInflater.inflate(R.layout.dialog_username_input, null)
        val usernameInput = dialogView.findViewById<TextInputEditText>(R.id.username_sign)
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val username = usernameInput.text.toString().trim()
        // Lắng nghe kết quả kiểm tra username
        registerViewModel.usernameExists.observe(this, { usernameExists ->
            if (usernameExists) {
                Toast.makeText(this, "Tên người dùng đã tồn tại", Toast.LENGTH_SHORT).show()
            } else {
                // Tiến hành đăng ký nếu email và username đều hợp lệ
                registerViewModel.register(email, password, username)
            }
        })

        // Lắng nghe kết quả đăng ký
        registerViewModel.registerResult.observe(this, { response ->
            if (response != null) {
                if (response.success) {
                    Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                    // Chuyển đến màn hình đăng nhập hoặc màn hình chính
                    finish() // Hoặc bạn có thể chuyển đến MainActivity, tùy vào yêu cầu
                } else {
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }
}








