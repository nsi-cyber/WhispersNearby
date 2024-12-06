package com.nsicyber.whispersnearby.domain.useCase

import android.graphics.Bitmap
import com.nsicyber.whispersnearby.EmotionEnumClass
import com.nsicyber.whispersnearby.domain.repository.EmotionRecognitionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class EmotionRecognitionUseCase @Inject constructor(
    private val repository: EmotionRecognitionRepository
) {
    operator fun invoke(bitmap: Bitmap): Flow<Result<EmotionEnumClass>> =
    flow {
        try {
            repository.recognizeEmotion(bitmap)
                .collect { result1 ->
                    emit(result1)
                }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

}
