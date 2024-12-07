package com.nsicyber.whispersnearby.data.repository

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetector
import com.nsicyber.whispersnearby.domain.repository.EmotionRecognitionMlRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await


class EmotionRecognitionMlRepositoryImpl(
    private val faceDetector: FaceDetector
) : EmotionRecognitionMlRepository {

    override fun recognizeEmotion(inputImage: InputImage): Flow<Result<String>> = flow {
        try {
            val faces = faceDetector.process(inputImage).await()
            val face = faces.firstOrNull()

                emit(Result.success(analyzeFaceToEmoji(face)))

        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    private fun analyzeFaceToEmoji(face: Face?): String {
        if(face==null) return "😐"
        val smilingProbability = face.smilingProbability ?: 0f
        val leftEyeOpenProbability = face.leftEyeOpenProbability ?: 0f
        val rightEyeOpenProbability = face.rightEyeOpenProbability ?: 0f
        val headEulerAngleY = face.headEulerAngleY // Yatay baş açısı
        val headEulerAngleZ = face.headEulerAngleZ // Yan baş eğikliği

        return when {
            // Mutlu
            smilingProbability > 0.7 -> "😄"

            // Üzgün: Daha hassas kontrol
            smilingProbability < 0.2 && leftEyeOpenProbability > 0.6 && rightEyeOpenProbability > 0.6 &&
                    (headEulerAngleY > 15 || headEulerAngleZ > 20) -> "😢"

            // Şaşkın
            smilingProbability < 0.3 && leftEyeOpenProbability > 0.7 && rightEyeOpenProbability > 0.7 -> "😮"

            // Kızgın
            smilingProbability < 0.3 && leftEyeOpenProbability < 0.5 && rightEyeOpenProbability < 0.5 -> "😠"

            // Göz kırpma
            (leftEyeOpenProbability < 0.3 && rightEyeOpenProbability > 0.7) ||
                    (leftEyeOpenProbability > 0.7 && rightEyeOpenProbability < 0.3) -> "😉"

            // Nötr
            else -> "😐"
        }
    }

}
