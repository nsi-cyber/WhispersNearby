package com.nsicyber.whispersnearby.domain.repository

import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController


interface CameraRepository {
    suspend fun takePhoto(
        controller: LifecycleCameraController,onImage:(data:ImageProxy?)->Unit
    )
}