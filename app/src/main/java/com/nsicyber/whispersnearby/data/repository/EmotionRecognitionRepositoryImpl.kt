package com.nsicyber.whispersnearby.data.repository

import android.content.res.AssetManager
import android.graphics.Bitmap
import com.nsicyber.whispersnearby.EmotionEnumClass
import com.nsicyber.whispersnearby.domain.repository.EmotionRecognitionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.tensorflow.lite.Interpreter

class EmotionRecognitionRepositoryImpl(
    private val interpreter: Interpreter,

    ) : EmotionRecognitionRepository {

    override fun recognizeEmotion(bitmap: Bitmap): Flow<Result<EmotionEnumClass>> = flow {
        try {

            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false)
            val inputArray = Array(1) { Array(INPUT_SIZE) { Array(INPUT_SIZE) { FloatArray(1) } } }

            for (i in 0 until INPUT_SIZE) {
                for (j in 0 until INPUT_SIZE) {
                    val pixel = scaledBitmap.getPixel(i, j)
                    val grayscale = (0.299 * ((pixel shr 16) and 0xFF) +
                            0.587 * ((pixel shr 8) and 0xFF) +
                            0.114 * (pixel and 0xFF)).toFloat() / 255.0f
                    inputArray[0][i][j][0] = grayscale
                }
            }
            val outputArray = Array(1) { FloatArray(EmotionEnumClass.entries.size) }
            interpreter.run(inputArray, outputArray)
            val maxIndex = outputArray[0].indices.maxByOrNull { outputArray[0][it] } ?: -1


            val bestEmotion = if (maxIndex != -1) {
                EmotionEnumClass.values()[maxIndex]
            } else {
                EmotionEnumClass.NEUTRAL
            }

            emit(
                Result.success(
                    bestEmotion
                )
            )
        } catch (e: Exception) {

            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.Default)


    companion object {
        private const val INPUT_SIZE = 48
    }
}
