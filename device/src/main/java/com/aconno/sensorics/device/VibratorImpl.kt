package com.aconno.sensorics.device

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import com.aconno.sensorics.domain.Vibrator

class VibratorImpl(private val context: Context) : Vibrator {
    override fun vibrate(milliseconds: Long) {
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? android.os.Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v?.vibrate(
                VibrationEffect.createOneShot(
                    milliseconds,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            //deprecated in API 26
            v?.vibrate(milliseconds)
        }
    }
}