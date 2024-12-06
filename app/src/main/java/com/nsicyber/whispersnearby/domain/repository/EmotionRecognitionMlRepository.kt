package com.nsicyber.whispersnearby.domain.repository

import com.google.mlkit.vision.common.InputImage
import com.nsicyber.whispersnearby.EmotionEnumClass
import kotlinx.coroutines.flow.Flow

interface EmotionRecognitionMlRepository {
    fun recognizeEmotion(inputImage: InputImage): Flow<Result<String>>
}
