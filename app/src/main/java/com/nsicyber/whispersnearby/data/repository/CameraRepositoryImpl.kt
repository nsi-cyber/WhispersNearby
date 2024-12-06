package com.nsicyber.whispersnearby.data.repository

import android.app.Application
import android.graphics.Matrix
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.face.FaceLandmark
import com.nsicyber.whispersnearby.domain.repository.CameraRepository
import com.nsicyber.whispersnearby.flowWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton

class CameraRepositoryImpl @Inject constructor(
    private val application: Application
) : CameraRepository {


    override suspend fun takePhoto(
        controller: LifecycleCameraController,onImage:(data:ImageProxy)->Unit
    )  {
            controller.takePicture(
                ContextCompat.getMainExecutor(application),
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        super.onCaptureSuccess(image)
                        onImage(image)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        super.onError(exception)
                    }
                }
            )
        }
        }





