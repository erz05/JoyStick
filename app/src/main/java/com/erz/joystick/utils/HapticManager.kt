package com.erz.joystick.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class HapticManager(context: Context) {
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun triggerFeedback(isDoubleTap: Boolean) {
        val v = vibrator ?: return
        if (!v.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val effectId = if (isDoubleTap) {
                VibrationEffect.EFFECT_DOUBLE_CLICK
            } else {
                VibrationEffect.EFFECT_CLICK
            }
            try {
                v.vibrate(VibrationEffect.createPredefined(effectId))
            } catch (e: Exception) {
                val pattern = if (isDoubleTap) {
                    longArrayOf(0, 40, 50, 40)
                } else {
                    longArrayOf(0, 20)
                }
                v.vibrate(VibrationEffect.createWaveform(pattern, -1))
            }
        } else {
            @Suppress("DEPRECATION")
            if (isDoubleTap) {
                v.vibrate(longArrayOf(0, 40, 50, 40), -1)
            } else {
                v.vibrate(20)
            }
        }
    }
}
