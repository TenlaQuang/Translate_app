package com.example.mobile_app.ui.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mobile_app.databinding.FragmentCameraBinding
import com.example.mobile_app.repository.TranslateRepository
import com.example.mobile_app.viewmodel.TranslateViewModel
import com.example.mobile_app.viewmodel.ViewModelFactory
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private lateinit var binding: FragmentCameraBinding
    private lateinit var viewModel: TranslateViewModel
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null

    private val targetLanguages = arrayOf("English", "Albanian", "Arabic", "Azerbaijani", "Basque", "Bengali", "Bulgarian",
        "Catalan", "Chinese", "Chinese (traditional)", "Czech", "Danish", "Dutch",
        "Esperanto", "Estonian", "Finnish", "French", "Galician", "German", "Greek",
        "Hebrew", "Hindi", "Hungarian", "Indonesian", "Irish", "Italian", "Japanese",
        "Korean", "Latvian", "Lithuanian", "Malay", "Norwegian", "Persian", "Polish",
        "Portuguese", "Romanian", "Russian", "Slovak", "Slovenian", "Spanish", "Swedish",
        "Tagalog", "Thai", "Turkish", "Ukranian", "Urdu", "Vietnamese")
    private val langCodeMap = mapOf(
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
        "Urdu" to "ur",
        "Vietnamese" to "vi"
    )

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Bạn cần cấp quyền camera để sử dụng tính năng này", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo ViewModel
        try {
            val repository = TranslateRepository()
            viewModel = ViewModelProvider(this)[TranslateViewModel::class.java]
            Log.d("CameraFragment", "ViewModel initialized successfully")
        } catch (e: Exception) {
            Log.e("CameraFragment", "Error initializing ViewModel", e)
            Toast.makeText(requireContext(), "Error initializing ViewModel: ${e.message}", Toast.LENGTH_LONG).show()
            return
        }

        // Khởi tạo camera executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Thiết lập spinner ngôn ngữ đích
        setupSpinner()

        // Kiểm tra quyền camera
        checkCameraPermissionAndStart()

        // Xử lý nút chụp ảnh
        binding.btnCapture.setOnClickListener {
            takePhoto()
        }

        // Quan sát kết quả dịch
        viewModel.translatedText.observe(viewLifecycleOwner) {
            binding.translatedTextView.text = it
            binding.translatedTextView.visibility = View.VISIBLE
            // Hiển thị resultLayout
            binding.resultLayout.visibility = View.VISIBLE
            // Ẩn PreviewView và nút chụp
            binding.previewView.visibility = View.GONE
            binding.btnCapture.visibility = View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        // Debug màu nền tại runtime
        val backgroundColor = ContextCompat.getColor(requireContext(), android.R.color.background_light)
        Log.d("CameraFragment", "Background color: #$backgroundColor")
        binding.root.setBackgroundColor(backgroundColor)
    }

    private fun setupSpinner() {
        val targetAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, targetLanguages)
        targetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTargetLanguage.adapter = targetAdapter
    }

    private fun checkCameraPermissionAndStart() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            // ImageCapture
            imageCapture = ImageCapture.Builder()
                .build()

            // Chọn camera sau (back camera)
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
                Log.d("CameraFragment", "Camera started successfully")
            } catch (exc: Exception) {
                Log.e("CameraFragment", "Use case binding failed", exc)
                Toast.makeText(requireContext(), "Không thể khởi động camera", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // Tạo file tạm để lưu ảnh
        val photoFile = File(
            requireContext().externalCacheDir,
            "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    binding.imageView.setImageURI(output.savedUri)
                    binding.imageView.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Ảnh đã được chụp: ${photoFile.absolutePath}", Toast.LENGTH_SHORT).show()

                    // Trích xuất văn bản từ ảnh
                    extractTextFromImage(output.savedUri!!)
                }

                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraFragment", "Photo capture failed: ${exc.message}", exc)
                    Toast.makeText(requireContext(), "Lỗi khi chụp ảnh", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun extractTextFromImage(uri: android.net.Uri) {
        val image = InputImage.fromFilePath(requireContext(), uri)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val extractedText = visionText.text
                if (extractedText.isNotEmpty()) {
                    // Lấy ngôn ngữ đích từ spinner
                    val targetLang = binding.spinnerTargetLanguage.selectedItem.toString()
                    val targetCode = langCodeMap[targetLang] ?: "vi"

                    // Lấy userId từ SharedPreferences
                    val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    val userId = sharedPreferences.getInt("user_id", -1)

                    // Dịch văn bản với source là "auto"
                    Log.d("CameraFragment", "Translating: text=$extractedText, source=auto, target=$targetCode, userId=$userId")
                    viewModel.translateText(extractedText, "auto", targetCode, userId)
                } else {
                    Toast.makeText(requireContext(), "Không tìm thấy văn bản trong ảnh", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("CameraFragment", "Text recognition failed: ${e.message}", e)
                Toast.makeText(requireContext(), "Lỗi nhận diện văn bản: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }
}