package com.aconno.sensorics.device

import android.content.Context
import android.os.Build
import android.telephony.SmsManager
import com.aconno.sensorics.domain.SmsSender
import timber.log.Timber

class SmsSenderImpl(
    private val context: Context
) : SmsSender {
    override fun sendSms(phoneNumber: String, message: String) {
        try {
            val smsManager = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            } else {
                context.getSystemService(SmsManager::class.java)
            }
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}