package com.nsicyber.whispersnearby.domain.repository

import com.nsicyber.whispersnearby.data.remote.ChatMessage
import kotlinx.coroutines.flow.Flow


interface ChatRepository {
    suspend fun sendMessage(message: ChatMessage)
    suspend fun reportMessage(messageId: String,deviceId:String)
    fun getNearbyMessages(
        latitude: Double,
        longitude: Double,
        radius: Double,
        secretCode: String? = null
    ): Flow<List<ChatMessage>>
    suspend fun deleteAllMessages()
}