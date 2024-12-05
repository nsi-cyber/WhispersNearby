package com.nsicyber.whispersnearby.domain.useCase

import com.nsicyber.whispersnearby.domain.repository.ChatRepository
import javax.inject.Inject

class DeleteAllMessagesUseCase  @Inject
constructor(private val repository: ChatRepository) {
    suspend operator fun invoke() {
        repository.deleteAllMessages()
    }
}