package com.nsicyber.whispersnearby.utils

import android.content.Context
import java.util.UUID


object DeviceIdProvider {
    fun getDeviceId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("device_id", null) ?: run {
            val deviceId = UUID.randomUUID().toString()
            sharedPreferences.edit().putString("device_id", deviceId).apply()
            deviceId
        }
    }
}
