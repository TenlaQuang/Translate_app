package com.example.mobile_app.ui.fragment

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.mobile_app.databinding.FragmentTranslateBinding
import com.example.mobile_app.viewmodel.TranslateViewModel

class TranslateFragment : Fragment() {
    private var _binding: FragmentTranslateBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TranslateViewModel by viewModels()

    private val languages = arrayOf("English", "Spanish", "French", "German")
    private val langCodeMap = mapOf(
        "English" to "en",
        "Spanish" to "es",
        "French" to "fr",
        "German" to "de"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTranslateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSourceLanguage.adapter = adapter
        binding.spinnerTargetLanguage.adapter = adapter

        // Hoán đổi
        binding.btnSwapLanguages.setOnClickListener {
            val sourcePos = binding.spinnerSourceLanguage.selectedItemPosition
            val targetPos = binding.spinnerTargetLanguage.selectedItemPosition
            binding.spinnerSourceLanguage.setSelection(targetPos)
            binding.spinnerTargetLanguage.setSelection(sourcePos)
        }

        // Xử lý khi nhấn dịch
        binding.btnTranslate.setOnClickListener {
            val inputText = binding.editTextInput.text.toString()
            val sourceLang = binding.spinnerSourceLanguage.selectedItem.toString()
            val targetLang = binding.spinnerTargetLanguage.selectedItem.toString()

            if (sourceLang == targetLang) {
                Toast.makeText(requireContext(), "Ngôn ngữ nguồn và đích không được trùng nhau", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (inputText.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập văn bản", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sourceCode = langCodeMap[sourceLang] ?: "en"
            val targetCode = langCodeMap[targetLang] ?: "es"
            val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getInt("user_id", -1)  // Giá trị mặc định là -1 nếu không tìm thấy

            viewModel.translateText(inputText, sourceCode, targetCode, userId)
        }

        // Quan sát kết quả
        viewModel.translatedText.observe(viewLifecycleOwner, Observer { translated ->
            // Kiểm tra nếu 'translated' là null
            if (translated != null) {
                binding.editTextOutput.setText(translated)  // Sử dụng setText thay vì gán trực tiếp
                binding.editTextOutput.visibility = View.VISIBLE
            } else {
                binding.editTextOutput.text.clear() // Nếu không có kết quả dịch, xóa nội dung
            }
        })

        // Quan sát lỗi
        viewModel.error.observe(viewLifecycleOwner, Observer { errorMsg ->
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
