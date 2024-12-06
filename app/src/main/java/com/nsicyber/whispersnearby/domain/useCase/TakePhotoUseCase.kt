package com.nsicyber.whispersnearby.domain.useCase

import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import com.nsicyber.whispersnearby.data.remote.ChatMessage
import com.nsicyber.whispersnearby.domain.repository.CameraRepository
import com.nsicyber.whispersnearby.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject



/*

class TakePhotoUseCase @Inject
constructor(
    private val repository: CameraRepository
) {
    operator fun invoke(controller: LifecycleCameraController): Flow<Result<ImageProxy>> = flow {
        try {
            repository.takePhoto(controller)
                .collect { result1 ->
                    emit(result1)
                }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
 */