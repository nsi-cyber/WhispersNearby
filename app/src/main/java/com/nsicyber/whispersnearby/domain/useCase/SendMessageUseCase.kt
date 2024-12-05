package com.nsicyber.whispersnearby.domain.useCase

import com.nsicyber.whispersnearby.data.remote.ChatMessage
import com.nsicyber.whispersnearby.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase  @Inject
constructor(private val repository: ChatRepository) {
    suspend operator fun invoke(message: ChatMessage) {
        repository.sendMessage(message)
    }
}