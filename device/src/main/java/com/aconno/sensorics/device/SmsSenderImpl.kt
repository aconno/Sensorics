package com.aconno.sensorics.device

import android.telephony.SmsManager
import com.aconno.sensorics.domain.SmsSender
import timber.log.Timber

class SmsSenderImpl : SmsSender {
    override fun sendSms(phoneNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}