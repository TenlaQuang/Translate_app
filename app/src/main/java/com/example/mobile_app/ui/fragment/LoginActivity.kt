package com.example.mobile_app.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobile_app.R
import com.example.mobile_app.model.LoginRequest
import com.example.mobile_app.model.LoginResponse
import com.example.mobile_app.network.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Lấy thông tin username và password từ các EditText
        val usernameEditText = findViewById<TextInputEditText>(R.id.username)
        val passwordEditText = findViewById<TextInputEditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login_btn)
        val signUpButton = findViewById<TextView>(R.id.sign_up)
        signUpButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        loginButton.setOnClickListener {
            // Gửi yêu cầu đăng nhập
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val loginRequest = LoginRequest(username, password)
            Log.d("LoginRequest", "Username: $username, Password: $password")
            RetrofitClient.instance.login(loginRequest)
                .enqueue(object : retrofit2.Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            if (loginResponse?.success == true) {
                                // Lưu user_id vào SharedPreferences
                                val userId = loginResponse.user?.user_id ?: -1  // Lấy user_id từ đối tượng user
                                val user = loginResponse.user
                                val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("username", user?.username ?: "Người dùng")
                                editor.putInt("user_id", userId)
                                editor.apply()

                                // Chuyển hướng đến màn hình chính
                                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                            } else {
                                // Thông báo lỗi
                                Toast.makeText(
                                    this@LoginActivity,
                                    loginResponse?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            // Thông báo lỗi server
                            Toast.makeText(
                                this@LoginActivity,
                                "Lỗi kết nối đến máy chủ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }



                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Log.e("LoginError", "Lỗi kết nối: ${t.message}")
                        Toast.makeText(this@LoginActivity, "Lỗi mạng", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}

