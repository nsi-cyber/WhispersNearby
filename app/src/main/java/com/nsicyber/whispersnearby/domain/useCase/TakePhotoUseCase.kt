package com.nsicyber.whispersnearby.domain.useCase


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