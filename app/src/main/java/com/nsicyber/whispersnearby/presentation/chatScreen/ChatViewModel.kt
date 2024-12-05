package com.nsicyber.whispersnearby.presentation.chatScreen

import android.graphics.Color
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nsicyber.whispersnearby.data.remote.ChatMessage
import com.nsicyber.whispersnearby.domain.useCase.DeleteAllMessagesUseCase
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
    private val deleteAllMessagesUseCase: DeleteAllMessagesUseCase
) : ViewModel()
{

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()



    fun deleteAllMessages() {
        viewModelScope.launch {
            deleteAllMessagesUseCase()
        }
    }

    fun reportMessage(messageId: String,deviceId:String) {
        viewModelScope.launch {
            reportMessageUseCase(messageId,deviceId)
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
            color = deviceColor?: Color.BLACK

        )
        viewModelScope.launch {
                sendMessageUseCase(chatMessage)

        }
    }
}
