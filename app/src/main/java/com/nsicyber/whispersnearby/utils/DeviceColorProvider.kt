package com.nsicyber.whispersnearby.utils

import android.content.Context
import android.graphics.Color
import androidx.core.graphics.ColorUtils

object DeviceColorProvider {
    private const val PREF_NAME = "device_color_prefs"
    private const val KEY_COLOR = "device_color"

    fun getDeviceColor(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        if (!sharedPreferences.contains(KEY_COLOR)) {
            // Rastgele renk oluştur
            val randomColor = generateRandomColor()
            sharedPreferences.edit().putInt(KEY_COLOR, randomColor).apply()
            return randomColor
        }
        return sharedPreferences.getInt(KEY_COLOR, Color.BLACK)
    }

    private fun generateRandomColor(): Int {
        val hue = (0..360).random().toFloat() // Renk tonunu rastgele seç
        val saturation = 0.7f // Doygunluk
        val lightness = 0.5f // Açıklık
        return ColorUtils.HSLToColor(floatArrayOf(hue, saturation, lightness))
    }
}

fun getContrastingTextColor(backgroundColor: Int): Int {
    val r = Color.red(backgroundColor)
    val g = Color.green(backgroundColor)
    val b = Color.blue(backgroundColor)

    // Luma formülü: 0.299*R + 0.587*G + 0.114*B
    val luma = (0.299 * r + 0.587 * g + 0.114 * b)

    return if (luma > 128) Color.BLACK else Color.WHITE
}

