package com.example.mobile_app.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mobile_app.R
import com.example.mobile_app.viewmodel.RegisterViewModel
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var registerButton: Button
    private lateinit var backButton: LinearLayout
    private var isRegistering = false // Biến kiểm soát để tránh gọi register nhiều lần
    private val handler = Handler(Looper.getMainLooper())
    private var checkEmailRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Khởi tạo ViewModel
        registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        // Liên kết các view từ layout
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password_sign)
        confirmPasswordEditText = findViewById(R.id.confirm_password)
        registerButton = findViewById(R.id.sign_btn)
        backButton = findViewById(R.id.back)

        // Xử lý sự kiện quay lại
        backButton.setOnClickListener {
            finish()
        }

        // Thêm TextWatcher để kiểm tra email theo thời gian thực
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Hủy runnable cũ nếu có
                checkEmailRunnable?.let { handler.removeCallbacks(it) }
                // Tạo runnable mới với delay 500ms
                checkEmailRunnable = Runnable {
                    val email = s.toString().trim()
                    if (email.isNotEmpty()) {
                        registerViewModel.setUserDetails("", "", email) // Lưu email để kiểm tra
                        registerViewModel.checkEmailExists()
                    }
                }
                handler.postDelayed(checkEmailRunnable!!, 500) // Delay 500ms để debounce
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Lắng nghe kết quả kiểm tra email
        registerViewModel.emailExists.observe(this) { exists ->
            if (exists) {
                registerViewModel.errorMessage.value?.let { errorMsg ->
                    Toast.makeText(this, "$errorMsg. Vui lòng nhập email khác!", Toast.LENGTH_LONG)
                        .show()
                } ?: run {
                    Toast.makeText(
                        this,
                        "Email đã tồn tại. Vui lòng nhập email khác!",
                        Toast.LENGTH_LONG
                    ).show()
                }
                emailEditText.requestFocus() // Focus lại vào trường email
                emailEditText.error = "Email đã tồn tại" // Hiển thị lỗi trực tiếp trên EditText
            } else {
                emailEditText.error = null // Xóa lỗi nếu email hợp lệ
            }
        }

        // Lắng nghe thông báo lỗi từ ViewModel
        registerViewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        // Bắt sự kiện khi nhấn nút Đăng ký
        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            // Kiểm tra các trường nhập liệu
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Mật khẩu và mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Lưu thông tin vào ViewModel (username sẽ được nhập từ dialog)
            registerViewModel.setUserDetails("", password, email)
            // Hiển thị Popup yêu cầu nhập username
            val dialogView = layoutInflater.inflate(R.layout.dialog_username_input, null)
            val usernameInput = dialogView.findViewById<TextInputEditText>(R.id.username_sign)

            val usernameDialog = AlertDialog.Builder(this)
                .setTitle("Nhập tên người dùng")
                .setView(dialogView)
                .setPositiveButton("OK") { _, _ ->
                    val username = usernameInput.text.toString().trim()
                    if (username.isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập tên người dùng", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        // Lưu username và gọi kiểm tra username
                        registerViewModel.setUserDetails(username, password, email)
                        registerViewModel.checkUsernameExists()
                    }
                }
                .setNegativeButton("Hủy", null)
                .create()
            usernameDialog.show()
        }

        // Lắng nghe kết quả kiểm tra username
        registerViewModel.usernameExists.observe(this) { exists ->
            if (exists) {
                registerViewModel.errorMessage.value?.let { errorMsg ->
                    Toast.makeText(
                        this,
                        "$errorMsg. Vui lòng nhập tên người dùng khác!",
                        Toast.LENGTH_LONG
                    ).show()
                } ?: run {
                    Toast.makeText(
                        this,
                        "Tên người dùng đã tồn tại. Vui lòng nhập tên khác!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else if (!isRegistering) {
                isRegistering = true // Đặt cờ để tránh gọi lại
                // Tiến hành đăng ký nếu email và username đều hợp lệ
                registerViewModel.register()
            }
        }

        // Lắng nghe kết quả đăng ký
        registerViewModel.registerResult.observe(this) { response ->
            isRegistering = false // Reset cờ sau khi hoàn thành
            response?.let {
                if (it.success) {
                    Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                    finish() // Quay lại màn hình trước đó
                } else {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            } ?: run {
                Toast.makeText(this, "Lỗi kết nối", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Hủy runnable khi Activity bị hủy để tránh memory leak
        checkEmailRunnable?.let { handler.removeCallbacks(it) }
    }
}






