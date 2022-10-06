package com.aconno.sensorics.device

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.VibratorManager
import com.aconno.sensorics.domain.Vibrator
import timber.log.Timber

class VibratorImpl(private val context: Context) : Vibrator {

    override fun vibrate(milliseconds: Long) {

        val v = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? android.os.Vibrator
        }

        // TODO: Keep first case only when min SDK gets bumped to 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Timber.i("Vibrator started")
            v?.vibrate(
                VibrationEffect.createOneShot(
                    milliseconds,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )

        } else {
            Timber.i("Vibrator not started")
            //deprecated in API 26
            @Suppress("DEPRECATION")
            v?.vibrate(milliseconds)
        }
    }
}