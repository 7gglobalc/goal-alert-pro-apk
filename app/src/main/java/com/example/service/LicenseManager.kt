package com.example.service

import android.content.Context
import android.content.SharedPreferences
import java.util.UUID

class LicenseManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("goalalert_license_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_ACTIVATED = "is_activated"
        private const val KEY_DEVICE_ID = "device_hardware_id"
        private const val KEY_ACTIVATION_DATE = "activation_date"
        
        // Master Admin PIN specified by the owner
        const val MASTER_ADMIN_PIN = "munali0736"

        // Emergency fallback / testing codes
        val GLOBAL_PASSCODES = listOf(
            MASTER_ADMIN_PIN,  // Primary Master Admin PIN
            "0736",            // Keypad Numeric Shortcut (last 4 digits of admin pin)
            "2026",            // Year fallback
            "7788",
            "9900"
        )

        /**
         * Generates a unique 4-digit activation PIN bound to a specific customer's Device ID.
         * Deterministic: the exact same Device ID + secret salt always produces the exact same 4-digit code.
         * This prevents customers from sharing codes with unpaid friends.
         */
        fun generateDeviceActivationPin(deviceId: String, salt: String = MASTER_ADMIN_PIN): String {
            val cleanId = deviceId.trim().uppercase()
            if (cleanId.isEmpty()) return "0736"

            var hash = 0L
            val seed = "$cleanId:$salt"
            for (char in seed) {
                hash = (hash * 37 + char.code.toLong()) and 0x7FFFFFFF
            }
            val code = (1000 + (hash % 9000)).toInt()
            return code.toString()
        }

        /**
         * Validates whether a given PIN is valid for a specific device.
         */
        fun isValidPinForDevice(pin: String, deviceId: String): Boolean {
            val trimmed = pin.trim()
            if (trimmed.equals(MASTER_ADMIN_PIN, ignoreCase = true)) return true
            if (GLOBAL_PASSCODES.contains(trimmed)) return true
            val expectedDevicePin = generateDeviceActivationPin(deviceId)
            return trimmed == expectedDevicePin
        }
    }

    val deviceId: String
        get() {
            var id = prefs.getString(KEY_DEVICE_ID, null)
            if (id == null) {
                id = UUID.randomUUID().toString().take(6).uppercase()
                prefs.edit().putString(KEY_DEVICE_ID, id).apply()
            }
            return id
        }

    /**
     * Gets the expected unique 4-digit activation PIN for this device.
     */
    val deviceActivationPin: String
        get() = generateDeviceActivationPin(deviceId)

    fun isActivated(): Boolean {
        return prefs.getBoolean(KEY_IS_ACTIVATED, false)
    }

    fun activateWithNumericCode(code: String): Boolean {
        return activateWithCode(code)
    }

    fun activateWithCode(code: String): Boolean {
        val trimmed = code.trim().lowercase()
        val currentDevicePin = deviceActivationPin

        val isValid = trimmed == MASTER_ADMIN_PIN.lowercase() ||
                trimmed == currentDevicePin ||
                GLOBAL_PASSCODES.any { it.equals(trimmed, ignoreCase = true) }

        if (isValid) {
            prefs.edit()
                .putBoolean(KEY_IS_ACTIVATED, true)
                .putLong(KEY_ACTIVATION_DATE, System.currentTimeMillis())
                .putString("activated_with_pin", code)
                .apply()
        }
        return isValid
    }

    fun lockApp() {
        prefs.edit().putBoolean(KEY_IS_ACTIVATED, false).apply()
    }

    fun unlockDirectly() {
        prefs.edit().putBoolean(KEY_IS_ACTIVATED, true).apply()
    }
}
