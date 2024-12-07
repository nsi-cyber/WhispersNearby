package com.nsicyber.whispersnearby.presentation.chatScreen

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.location.Location
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.LifecycleCameraController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.nsicyber.whispersnearby.data.remote.ChatMessage
import com.nsicyber.whispersnearby.domain.repository.CameraRepository
import com.nsicyber.whispersnearby.domain.useCase.DeleteAllMessagesUseCase
import com.nsicyber.whispersnearby.domain.useCase.EmotionRecognitionMlUseCase
import com.nsicyber.whispersnearby.domain.useCase.GetNearbyMessagesUseCase
import com.nsicyber.whispersnearby.domain.useCase.ReportMessageUseCase
import com.nsicyber.whispersnearby.domain.useCase.SendMessageUseCase
import com.nsicyber.whispersnearby.utils.MessageEncryptor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val reportMessageUseCase: ReportMessageUseCase,
    private val getNearbyMessagesUseCase: GetNearbyMessagesUseCase,
    private val deleteAllMessagesUseCase: DeleteAllMessagesUseCase,
    //  private val takePhotoUseCase: TakePhotoUseCase,
    private val emotionRecognitionMlUseCase: EmotionRecognitionMlUseCase,
    private val repository: CameraRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()


    fun deleteAllMessages() {
        viewModelScope.launch {
            deleteAllMessagesUseCase()
        }
    }

    fun reportMessage(messageId: String, deviceId: String) {
        viewModelScope.launch {
            reportMessageUseCase(messageId, deviceId)
        }
    }


    fun loadNearbyMessages(
        latitude: Double,
        longitude: Double,
        radius: Double,
        secretCode: String? = null
    ) {
        viewModelScope.launch {
            getNearbyMessagesUseCase(latitude, longitude, radius, secretCode).collect { messages ->
                _uiState.value = uiState.value.copy(messages = messages)
            }
        }
    }

    fun sendMessage(
        content: String,
        latitude: Double,
        longitude: Double,
        deviceId: String,
        secretCode: String?,
        deviceColor: Int?
    ) {

        val encryptedContent = MessageEncryptor.encryptMessage(content)
        val chatMessage = ChatMessage(
            content = encryptedContent,
            timestamp = System.currentTimeMillis(),
            latitude = latitude,
            longitude = longitude,
            deviceId = deviceId,
            secretCode = secretCode.orEmpty(),
            color = deviceColor ?: Color.BLACK

        )
        viewModelScope.launch {
            sendMessageUseCase(chatMessage)

        }
    }

    @OptIn(ExperimentalGetImage::class)
    fun sendMessageWithImage(
        controller: LifecycleCameraController,
        content: String,
        latitude: Double,
        longitude: Double,
        deviceId: String,
        secretCode: String?,
        deviceColor: Int?
    ) {
        viewModelScope.launch {
            repository.takePhoto(controller) { imageProxy ->

                val emoji = "😐" // Varsayılan emoji

                try {
                    if (imageProxy != null) {
                        val inputImage = InputImage.fromMediaImage(
                            imageProxy.image!!,
                            imageProxy.imageInfo.rotationDegrees
                        )

                        viewModelScope.launch {
                            emotionRecognitionMlUseCase(inputImage).collect { faceResult ->
                                faceResult.fold(
                                    onSuccess = { detectedEmoji ->
                                        sendMessageUseCase(ChatMessage(
                                            content = MessageEncryptor.encryptMessage(content),
                                            latitude = latitude,
                                            longitude = longitude,
                                            deviceId = deviceId,
                                            timestamp = System.currentTimeMillis(),
                                            secretCode = secretCode.orEmpty(),
                                            color = deviceColor?:Color.BLACK,
                                            emoji = detectedEmoji)
                                        )
                                    },
                                    onFailure = {
                                        Log.e("FaceDetection", "Failed to detect face.")
                                        sendMessageUseCase(ChatMessage(
                                            content = MessageEncryptor.encryptMessage(content),
                                            latitude = latitude,
                                            timestamp = System.currentTimeMillis(),

                                            longitude = longitude,
                                            deviceId = deviceId,
                                            secretCode = secretCode.orEmpty(),
                                            color = deviceColor?:Color.BLACK,
                                            emoji = emoji)
                                        )
                                    }
                                )
                            }
                        }
                    } else {
                        sendMessage(
                            content = content,
                            latitude = latitude,
                            longitude = longitude,
                            deviceId = deviceId,
                            secretCode = secretCode.orEmpty(),
                            deviceColor = deviceColor,
                        )
                    }
                } finally {
                    imageProxy?.close()
                }
            }
        }
    }


    /*
        @OptIn(ExperimentalGetImage::class)
        fun sendMessageWithImage(
            controller: LifecycleCameraController,
            content: String,
            latitude: Double,
            longitude: Double,
            deviceId: String,
            secretCode: String?,
            deviceColor: Int?
        ) {
            viewModelScope.launch {
                withContext(Dispatchers.Main) {
                // 1. Fotoğraf Çek
                takePhotoUseCase(controller).collect { photoResult ->
                    photoResult.fold(
                        onSuccess = { imageProxy ->
                            try {
                                // 2. Face Detection Yap
                                val inputImage = InputImage.fromMediaImage(
                                    imageProxy.image!!,
                                    imageProxy.imageInfo.rotationDegrees
                                )
                                emotionRecognitionUseCase(inputImage.toBitmap()).collect { faceResult ->
                                    faceResult.fold(
                                        onSuccess = { emoji ->
                                            // 3. Mesajı Gönder
                                            val encryptedContent =
                                                MessageEncryptor.encryptMessage(content)
                                            val chatMessage = ChatMessage(
                                                content = encryptedContent,
                                                timestamp = System.currentTimeMillis(),
                                                latitude = latitude,
                                                longitude = longitude,
                                                deviceId = deviceId,
                                                secretCode = secretCode.orEmpty(),
                                                color = deviceColor ?: Color.BLACK,
                                                emoji = emoji.name
                                            )
                                            sendMessageUseCase(chatMessage)
                                        },
                                        onFailure = { faceError ->
                                            Log.e(
                                                "FaceDetection",
                                                "Failed to detect face: ${faceError.message}"
                                            )
                                        }
                                    )
                                }
                            } finally {
                                imageProxy.close() // Kaynakları serbest bırak
                            }
                        },
                        onFailure = { photoError ->
                            Log.e("PhotoCapture", "Failed to capture photo: ${photoError.message}")
                        }
                    )
                }
            }
            }
        }

     */
}

fun InputImage.toBitmap(): Bitmap {
    return when (this.rotationDegrees) {
        0 -> this.bitmapInternal
            ?: throw IllegalArgumentException("InputImage does not contain a Bitmap.")

        else -> {
            // Gerekirse görüntüyü döndür
            val matrix = Matrix().apply { postRotate(this@toBitmap.rotationDegrees.toFloat()) }
            Bitmap.createBitmap(
                this.bitmapInternal
                    ?: throw IllegalArgumentException("InputImage does not contain a Bitmap."),
                0,
                0,
                this.width,
                this.height,
                matrix,
                true
            )
        }
    }
}
