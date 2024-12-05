package com.nsicyber.whispersnearby.domain.useCase

import com.nsicyber.whispersnearby.domain.repository.ChatRepository
import javax.inject.Inject


class ReportMessageUseCase @Inject
constructor(private val repository: ChatRepository) {
    suspend operator fun invoke(messageId: String,deviceId:String) {
        repository.reportMessage(messageId,deviceId)
    }
}