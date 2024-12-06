package com.nsicyber.whispersnearby.data.remote

import android.graphics.Color

data class ChatMessage(
    val id: String = "",
    val content: String = "",
    val timestamp: Long = 0L,
    val durationTime: Long = 0L,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val deviceId: String = "",
    val secretCode: String = "",
    val reportedBy: List<String> = listOf(),
    val color: Int = Color.BLACK,
    val emoji: String = "😐",

)
