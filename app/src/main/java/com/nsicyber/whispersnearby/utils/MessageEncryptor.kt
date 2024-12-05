package com.nsicyber.whispersnearby.utils

import android.util.Base64
import android.util.Log
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object MessageEncryptor {
    private const val SECRET_KEY = "A8qL8zG/2+Q/8Yk9LJ6Wkg==" // Keep this secret and consistent

    fun encryptMessage(message: String): String {
        val key = SECRET_KEY.toByteArray(Charsets.UTF_8)
        val cipher = Cipher.getInstance("AES")
        val secretKeySpec = SecretKeySpec(key, "AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        return Base64.encodeToString(cipher.doFinal(message.toByteArray(Charsets.UTF_8)), Base64.DEFAULT)
    }

    fun decryptMessage(encryptedMessage: String): String {
        try {
            val key = SECRET_KEY.toByteArray(Charsets.UTF_8)
        val cipher = Cipher.getInstance("AES")
        val secretKeySpec = SecretKeySpec(key, "AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
        return String(cipher.doFinal(Base64.decode(encryptedMessage, Base64.DEFAULT)), Charsets.UTF_8)
    } catch (e: Exception) {
        Log.e("MessageEncryptor", "Decryption failed: ${e.message}")
        return "Decryption Error"
    }
    }
}
