package com.nsicyber.whispersnearby.utils

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class LocationUtils(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fun getCurrentLocation(onLocationRetrieved: (Location?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            onLocationRetrieved(null) // İzin yoksa null döner
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            onLocationRetrieved(location)
        }
    }
}



fun calculateDistance(
    userLatitude: Double,
    userLongitude: Double,
    messageLatitude: Double,
    messageLongitude: Double
): Double {
    val earthRadius = 6371000.0 // Dünya'nın yarıçapı (metre)

    val dLat = Math.toRadians(messageLatitude - userLatitude)
    val dLon = Math.toRadians(messageLongitude - userLongitude)

    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(userLatitude)) * cos(Math.toRadians(messageLatitude)) *
            sin(dLon / 2) * sin(dLon / 2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return earthRadius * c // Mesafe metre cinsinden döndürülür
}
