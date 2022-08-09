package com.aconno.sensorics.device.bluetooth.tasks.lock

import com.aconno.sensorics.device.bluetooth.tasks.CharacteristicReadTask
import com.aconno.sensorics.domain.UUIDProvider
import com.aconno.sensorics.domain.scanning.Bluetooth
import timber.log.Timber

/**
 * Lock State Request TaskBase
 *
 * @property callback lock state request callback
 */
class LockStateTask(
    private val callback: LockStateRequestCallback
) : CharacteristicReadTask(
    LOCK_STATE_SERVICE_UUID,
    LOCK_STATE_CHARACTERISTIC_UUID,
    "Lock State Read Task"
) {
    override fun onSuccess(value: ByteArray) {
        if (value[0] == 0x01.toByte()) {
            Timber.e("Device unlocked")
            callback.onDeviceLockStateRead(true)
        } else {
            Timber.e("Device locked")
            callback.onDeviceLockStateRead(false)
        }
    }

    override fun onError(bluetooth: Bluetooth, e: Exception) {
        Timber.e("Error reading beacon lock state")
        super.onError(bluetooth, e)
        callback.onError(e)
    }

    companion object {
        val LOCK_STATE_SERVICE_UUID = UUIDProvider.provideFullUUID("D000")
        val LOCK_STATE_CHARACTERISTIC_UUID = UUIDProvider.provideFullUUID("D001")
    }
}