package com.nsicyber.whispersnearby.domain.useCase

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.nsicyber.whispersnearby.EmotionEnumClass
import com.nsicyber.whispersnearby.domain.repository.EmotionRecognitionMlRepository
import com.nsicyber.whispersnearby.domain.repository.EmotionRecognitionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class EmotionRecognitionMlUseCase @Inject constructor(
    private val repository: EmotionRecognitionMlRepository
) {
    operator fun invoke(bitmap: InputImage): Flow<Result<String>> =
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
