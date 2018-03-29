package com.aconno.acnsensa.device

import android.telephony.SmsManager
import com.aconno.acnsensa.domain.SmsSender
import timber.log.Timber

class SmsSenderImpl : SmsSender {
    override fun sendSms(phoneNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            Timber.e("message is $message")
            smsManager.sendTextMessage(phoneNumber, null, "AcnSensa Alert", null, null)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}