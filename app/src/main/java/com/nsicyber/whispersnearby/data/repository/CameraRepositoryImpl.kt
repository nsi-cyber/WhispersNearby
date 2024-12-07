package com.nsicyber.whispersnearby.data.repository

import android.app.Application
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import com.nsicyber.whispersnearby.domain.repository.CameraRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CameraRepositoryImpl @Inject constructor(
    private val application: Application
) : CameraRepository {

    override suspend fun takePhoto(
        controller: LifecycleCameraController,
        onImage: (data: ImageProxy?) -> Unit
    ) {
        controller.takePicture(
            ContextCompat.getMainExecutor(application),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    Log.e("CameraCapture", "onCaptureSuccess ")
                    onImage(image)
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("CameraCapture", "Failed to capture photo: ${exception.message}")
                    onImage(null)
                }
            }
        )
    }
}


