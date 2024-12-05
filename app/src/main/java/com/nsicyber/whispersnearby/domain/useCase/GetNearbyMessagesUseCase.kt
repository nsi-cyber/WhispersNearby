package com.nsicyber.whispersnearby.domain.useCase

import com.nsicyber.whispersnearby.data.remote.ChatMessage
import com.nsicyber.whispersnearby.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNearbyMessagesUseCase  @Inject
constructor(private val repository: ChatRepository) {
    operator fun invoke(
        latitude: Double,
        longitude: Double,
        radius: Double,
        secretCode: String? = null
    ): Flow<List<ChatMessage>> {
        return repository.getNearbyMessages(latitude, longitude, radius, secretCode)
    }
}