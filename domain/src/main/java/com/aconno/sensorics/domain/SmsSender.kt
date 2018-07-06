package com.aconno.sensorics.domain

interface SmsSender {
    fun sendSms(phoneNumber: String, message: String)
}
