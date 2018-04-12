package com.aconno.acnsensa.domain

interface SmsSender {
    fun sendSms(phoneNumber: String, message: String)
}
