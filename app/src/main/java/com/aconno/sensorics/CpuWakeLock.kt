package com.aconno.sensorics

import android.content.Context
import android.os.PowerManager
import timber.log.Timber


/**
 * Hold a wakelock that can be acquired in the AlarmReceiver and
 * released in the AlarmAlert activity
 */
internal object CpuWakeLock {
    private var cpuWakeLock: PowerManager.WakeLock? = null
    fun acquireCpuWakeLock(context: Context): Boolean {
        Timber.v("Acquiring cpu wake lock")

        if (cpuWakeLock != null) {
            return true
        }

        (context.getSystemService(Context.POWER_SERVICE) as? PowerManager)?.let { powerManager ->
            cpuWakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK or
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
                    PowerManager.ON_AFTER_RELEASE,
                "com.aconno.sensorics:AlarmClock"
            ).also {
                it.acquire(10 * 60 * 1000L)
            }
            return true
        }

        return false
    }

    fun releaseCpuLock() {
        Timber.v("Releasing cpu wake lock")
        cpuWakeLock?.let {
            it.release()
            cpuWakeLock = null
        }
    }
}