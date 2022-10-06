package com.aconno.sensorics.device

import android.content.Context
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.aconno.sensorics.domain.telephony.CallStateListener
import com.aconno.sensorics.domain.telephony.DeviceTelephonyManager

class DeviceTelephonyManagerImpl(
    context: Context
) : DeviceTelephonyManager {

    private val context = context

    private val telephonyManager = context.getSystemService(
        Context.TELEPHONY_SERVICE
    ) as TelephonyManager

    private val phoneStateListener: PhoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            super.onCallStateChanged(state, phoneNumber)
            callStateListeners.forEach { it.onCallStateChanged(state, phoneNumber) }
        }
    }

//    private val telephonyCallback: TelephonyCallback = @RequiresApi(Build.VERSION_CODES.S)
//    object : TelephonyCallback(), TelephonyCallback.CallStateListener {
//        override fun onCallStateChanged(state: Int) {
////            TODO("Not yet implemented")
//        }
//    }

    private val callStateListeners: MutableSet<CallStateListener> = mutableSetOf()

    override fun getCallState(): Int {
        return telephonyManager.callState

//        return if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
//            telephonyManager.callState
//        } else {
//            if (ActivityCompat.checkSelfPermission(
//                    context,
//                    Manifest.permission.READ_PHONE_STATE
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return -1
//            }
//            telephonyManager.callStateForSubscription
//        }
    }

    override fun registerCallStateListener(callStateListener: CallStateListener): Boolean {
//        return callStateListeners.add(callStateListener).also {
//            if (callStateListeners.count() == 1) {
//                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    telephonyManager.registerTelephonyCallback(context.mainExecutor, telephonyCallback)
//                } else {
//                    telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
//                }
//            }
//        }
        return callStateListeners.add(callStateListener).also {
            if (callStateListeners.count() == 1) {
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
            }
        }
    }

    override fun unregisterCallStateListener(callStateListener: CallStateListener): Boolean {
//        return callStateListeners.remove(callStateListener).also {
//            if (callStateListeners.count() == 0) {
//                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    telephonyManager.registerTelephonyCallback(context.mainExecutor, telephonyCallback)
//                } else {
//                    telephonyManager.listen(phoneStateListener, 0)
//                }
//            }
//        }
        return callStateListeners.remove(callStateListener).also {
            if (callStateListeners.count() == 0) {
                telephonyManager.listen(phoneStateListener, 0)
            }
        }
    }
}