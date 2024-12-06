package com.nsicyber.whispersnearby.utils

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat



fun checkAndRequestCameraPermission(
    activity: ComponentActivity,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val permission = android.Manifest.permission.CAMERA

    if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
        onPermissionGranted()
    } else {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), 100)
    }
}

fun checkAndRequestLocationPermission(
    activity: ComponentActivity,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val permission = android.Manifest.permission.ACCESS_FINE_LOCATION

    if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
        onPermissionGranted()
    } else {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), 100)
    }
}
