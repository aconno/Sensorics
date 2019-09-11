package com.aconno.sensorics.device.bluetooth.tasks.lock

interface LockStateRequestCallback {
    fun onDeviceLockStateRead(unlocked: Boolean)
    fun onError(e: Exception)
}
