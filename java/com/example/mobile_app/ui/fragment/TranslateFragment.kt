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

    private val sourceLanguages = arrayOf("Auto Detect", "English", "Albanian", "Arabic", "Azerbaijani", "Basque", "Bengali", "Bulgarian",
        "Catalan", "Chinese", "Chinese (traditional)", "Czech", "Danish", "Dutch",
        "Esperanto", "Estonian", "Finnish", "French", "Galician", "German", "Greek",
        "Hebrew", "Hindi", "Hungarian", "Indonesian", "Irish", "Italian", "Japanese",
        "Korean", "Latvian", "Lithuanian", "Malay", "Norwegian", "Persian", "Polish",
        "Portuguese", "Romanian", "Russian", "Slovak", "Slovenian", "Spanish", "Swedish",
        "Tagalog", "Thai", "Turkish", "Ukranian", "Urdu")
    private val targetLanguages = arrayOf("English", "Albanian", "Arabic", "Azerbaijani", "Basque", "Bengali", "Bulgarian",
        "Catalan", "Chinese", "Chinese (traditional)", "Czech", "Danish", "Dutch",
        "Esperanto", "Estonian", "Finnish", "French", "Galician", "German", "Greek",
        "Hebrew", "Hindi", "Hungarian", "Indonesian", "Irish", "Italian", "Japanese",
        "Korean", "Latvian", "Lithuanian", "Malay", "Norwegian", "Persian", "Polish",
        "Portuguese", "Romanian", "Russian", "Slovak", "Slovenian", "Spanish", "Swedish",
        "Tagalog", "Thai", "Turkish", "Ukranian", "Urdu")
    private val langCodeMap = mapOf(
        "Auto Detect" to "auto",
        "English" to "en",
        "Albanian" to "sq",
        "Arabic" to "ar",
        "Azerbaijani" to "az",
        "Basque" to "eu",
        "Bengali" to "bn",
        "Bulgarian" to "bg",
        "Catalan" to "ca",
        "Chinese" to "zh",
        "Chinese (traditional)" to "zt",
        "Czech" to "cs",
        "Danish" to "da",
        "Dutch" to "nl",
        "Esperanto" to "eo",
        "Estonian" to "et",
        "Finnish" to "fi",
        "French" to "fr",
        "Galician" to "gl",
        "German" to "de",
        "Greek" to "el",
        "Hebrew" to "he",
        "Hindi" to "hi",
        "Hungarian" to "hu",
        "Indonesian" to "id",
        "Irish" to "ga",
        "Italian" to "it",
        "Japanese" to "ja",
        "Korean" to "ko",
        "Latvian" to "lv",
        "Lithuanian" to "lt",
        "Malay" to "ms",
        "Norwegian" to "nb",
        "Persian" to "fa",
        "Polish" to "pl",
        "Portuguese" to "pt",
        "Romanian" to "ro",
        "Russian" to "ru",
        "Slovak" to "sk",
        "Slovenian" to "sl",
        "Spanish" to "es",
        "Swedish" to "sv",
        "Tagalog" to "tl",
        "Thai" to "th",
        "Turkish" to "tr",
        "Ukranian" to "uk",
        "Urdu" to "ur"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTranslateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sourceAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sourceLanguages)
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val targetAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, targetLanguages)
        targetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerSourceLanguage.adapter = sourceAdapter
        binding.spinnerTargetLanguage.adapter = targetAdapter

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
