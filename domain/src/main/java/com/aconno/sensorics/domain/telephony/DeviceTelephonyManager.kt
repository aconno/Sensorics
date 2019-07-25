package com.aconno.sensorics.domain.telephony

interface DeviceTelephonyManager {
    fun getCallState(): Int

    fun unregisterCallStateListener(callStateListener: CallStateListener): Boolean

    fun registerCallStateListener(callStateListener: CallStateListener): Boolean
}