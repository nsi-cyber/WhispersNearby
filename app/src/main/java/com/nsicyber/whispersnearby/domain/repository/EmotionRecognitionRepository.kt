package com.nsicyber.whispersnearby.domain.repository

import android.graphics.Bitmap
import com.nsicyber.whispersnearby.EmotionEnumClass
import kotlinx.coroutines.flow.Flow
import org.tensorflow.lite.Interpreter

interface EmotionRecognitionRepository {
    fun recognizeEmotion(bitmap: Bitmap): Flow<Result<EmotionEnumClass>>
}


