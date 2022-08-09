package com.aconno.sensorics.device.bluetooth.tasks.lock

import com.aconno.sensorics.device.bluetooth.tasks.CharacteristicWriteTask
import com.aconno.sensorics.device.bluetooth.tasks.lock.LockStateTask.Companion.LOCK_STATE_CHARACTERISTIC_UUID
import com.aconno.sensorics.device.bluetooth.tasks.lock.LockStateTask.Companion.LOCK_STATE_SERVICE_UUID
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.ASCII_STRING
import com.aconno.sensorics.domain.scanning.Bluetooth
import timber.log.Timber
import java.nio.ByteOrder

/**
 * Password write task (password limited to 15 bytes)
 *
 * @property password password
 * @property checkCallback lock state request callback
 * @property checkValid should the task check if the password was valid
 */
// TODO: Pass Max Length passed as configuration
class PasswordWriteTask(
    private var password: String,
    private val checkCallback: LockStateRequestCallback? = null,
    private val checkValid: Boolean = true
) : CharacteristicWriteTask(
    serviceUUID = LOCK_STATE_SERVICE_UUID,
    characteristicUUID = LOCK_STATE_CHARACTERISTIC_UUID,
    value = ASCII_STRING.serialize(password, order = ByteOrder.BIG_ENDIAN).copyOf(15)
) {
    override fun onSuccess() {
        Timber.e("Wrote password")
        if (checkValid && checkCallback != null) {
            taskQueue.offer(LockStateTask(checkCallback))
        }
    }

    override fun onError(bluetooth: Bluetooth, e: Exception) {
        Timber.e("Error writing beacon password $password")
        super.onError(bluetooth, e)
    }
}