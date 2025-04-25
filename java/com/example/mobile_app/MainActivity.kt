package com.example.mobile_app


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.mobile_app.ui.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var login: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ các thành phần UI
        progressBar = findViewById(R.id.progressBar)
        login = findViewById(R.id.login)

        // Ẩn các thành phần không cần thiết ban đầu
        progressBar.visibility = ProgressBar.VISIBLE
        login.visibility = Button.GONE

        // Dùng Handler để delay và thay đổi UI sau một thời gian
        Handler().postDelayed({
            // Ẩn progressBar và hiển thị nút Login
            progressBar.visibility = ProgressBar.GONE
            login.visibility = Button.VISIBLE
        }, 3000) // Thời gian delay 3 giây (3000 ms)

        // Khi người dùng nhấn vào nút Login
        login.setOnClickListener {
            // Điều hướng sang LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Hoàn tất MainActivity
        }
    }
}
