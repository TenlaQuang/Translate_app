package com.example.mobile_app.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mobile_app.R

class SplashFragment : Fragment() {

    private lateinit var progressBar: ProgressBar
    private lateinit var login: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)

        progressBar = view.findViewById(R.id.progressBar)
        login = view.findViewById(R.id.login)

        progressBar.visibility = View.VISIBLE
        login.visibility = View.GONE

        Handler().postDelayed({
            progressBar.visibility = View.GONE
            login.visibility = View.VISIBLE
        }, 3000)
        login.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // Đóng Splash luôn
        }
        return view
    }
}
