package com.nsicyber.whispersnearby.presentation.chatScreen

import com.nsicyber.whispersnearby.data.remote.ChatMessage

data class ChatUiState(
    val messages: List<ChatMessage>? = emptyList(),
    val isLoading: Boolean = false
)
