package com.example.mobile_app.ui.fragment

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mobile_app.databinding.FragmentMicBinding
import com.example.mobile_app.repository.TranslateRepository
import com.example.mobile_app.viewmodel.TranslateViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class MicFragment : Fragment() {

    private lateinit var binding: FragmentMicBinding
    private lateinit var viewModel: TranslateViewModel
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent
    private var isRecording = false
    private var blinkJob: Job? = null

    private val languages = listOf("en", "vi", "fr", "ja", "zh")
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
    )    // Xin quyền ghi âm
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startRecording()
            } else {
                Toast.makeText(requireContext(), "Bạn cần cấp quyền ghi âm để sử dụng tính năng này", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel
        val repository = TranslateRepository()
        viewModel = ViewModelProvider(this)[TranslateViewModel::class.java]

        // Spinner ngôn ngữ
        val sourceAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sourceLanguages)
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val targetAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, targetLanguages)
        targetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerSourceLanguage.adapter = sourceAdapter
        binding.spinnerTargetLanguage.adapter = targetAdapter

        binding.btnSwapLanguages.setOnClickListener {
            val sourcePos = binding.spinnerSourceLanguage.selectedItemPosition
            val targetPos = binding.spinnerTargetLanguage.selectedItemPosition
            binding.spinnerSourceLanguage.setSelection(targetPos)
            binding.spinnerTargetLanguage.setSelection(sourcePos)
                    }

        // Khởi tạo SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        // Xử lý kết quả giọng nói
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                stopBlinking()
                isRecording = false

                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val spokenText = matches?.firstOrNull()

                if (!spokenText.isNullOrEmpty()) {
                    val sourceLang = binding.spinnerSourceLanguage.selectedItem.toString()
                    val targetLang = binding.spinnerTargetLanguage.selectedItem.toString()
                    val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    val userId = sharedPreferences.getInt("user_id", -1)
                    val sourceCode = langCodeMap[sourceLang] ?: "auto"
                    val targetCode = langCodeMap[targetLang] ?: "en"
                    viewModel.translateText(spokenText, sourceCode, targetCode, userId)
                    binding.sourceTextView.text = spokenText
                } else {
                    Toast.makeText(requireContext(), "Không nhận được nội dung giọng nói", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onError(error: Int) {
                stopBlinking()
                isRecording = false
                Toast.makeText(requireContext(), "Lỗi nhận diện giọng nói: $error", Toast.LENGTH_SHORT).show()
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        // Mic click
        binding.btnMic.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                checkAudioPermissionAndStart()
            }
        }

        // Kết quả dịch
        viewModel.translatedText.observe(viewLifecycleOwner) {
            binding.translatedTextView.text = it
        }

        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAudioPermissionAndStart() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startRecording()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startRecording() {
        isRecording = true
        startBlinking()
        speechRecognizer.startListening(recognizerIntent)
    }

    private fun stopRecording() {
        speechRecognizer.stopListening()
        isRecording = false
        stopBlinking()
    }

    private fun startBlinking() {
        blinkJob = lifecycleScope.launch {
            while (isRecording) {
                binding.btnMic.alpha = 0.2f
                delay(500)
                binding.btnMic.alpha = 1f
                delay(500)
            }
        }
    }

    private fun stopBlinking() {
        blinkJob?.cancel()
        binding.btnMic.alpha = 1f
    }
}
