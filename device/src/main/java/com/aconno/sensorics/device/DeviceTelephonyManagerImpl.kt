package com.aconno.sensorics.device

import android.content.Context
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.aconno.sensorics.domain.telephony.CallStateListener
import com.aconno.sensorics.domain.telephony.DeviceTelephonyManager

class DeviceTelephonyManagerImpl(
    context: Context
) : DeviceTelephonyManager {
    private val telephonyManager = context.getSystemService(
        Context.TELEPHONY_SERVICE
    ) as TelephonyManager

    private val phoneStateListener: PhoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            super.onCallStateChanged(state, phoneNumber)
            callStateListeners.forEach { it.onCallStateChanged(state, phoneNumber) }
        }
    }

    private val callStateListeners: MutableSet<CallStateListener> = mutableSetOf()

    override fun getCallState(): Int {
        return telephonyManager.callState
    }

    override fun registerCallStateListener(callStateListener: CallStateListener): Boolean {
        return callStateListeners.add(callStateListener).also {
            if(callStateListeners.count() == 1) {
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
            }
        }
    }

    override fun unregisterCallStateListener(callStateListener: CallStateListener): Boolean {
        return callStateListeners.remove(callStateListener).also {
            if(callStateListeners.count() == 0) {
                telephonyManager.listen(phoneStateListener, 0)
            }
        }
    }
}