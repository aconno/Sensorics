package com.aconno.sensorics.domain.telephony

interface CallStateListener {
    fun onCallStateChanged(state: Int, phoneNumber: String?)
}