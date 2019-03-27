package com.aconno.bluetooth.beacon

import android.bluetooth.le.ScanResult
import com.aconno.bluetooth.*
import io.reactivex.functions.Predicate
import timber.log.Timber

val LOCK_STATE_PASSWORD_UUID = UUIDProvider.provideFullUUID("D001")

class Beacon(
        override val device: BluetoothDevice,
        var name: String? = "",
        var connectible: Boolean = true,
        var advFeature: String = "N/A",
        var supportedTxPower: Array<Int> = arrayOf()
) : DeviceSpec(device), BleReadableWritable {
    var parameters: Parameters = Parameters()
    lateinit var slots: Slots
    lateinit var arbitraryData: ArbitraryData

    val manufacturer: String
        get() = parameters.getParameterString("Manufacturer")

    val model: String
        get() = parameters.getParameterString("Model")

    val hwVersion: String
        get() = parameters.getParameterString("Hardware version")

    val fwVersion: String
        get() = parameters.getParameterString("Firmware version")

    val sdkVersion: String
        get() = parameters.getParameterString("SDK version")

    val freeRTOSVersion: String
        get() = parameters.getParameterString("FreeRTOS version")

    val mac: String
        get() = parameters.getParameterString("MAC")

    val slotCount: String
        get() = slots.size.toString()


    fun unlock(password: String, callback: LockStateTask.LockStateRequestTaskCallback) {
        device.queueTask(PasswordWriteTask(device, password, callback))
    }

    fun requestDeviceLockStatus(callback: LockStateTask.LockStateRequestTaskCallback) {
        callback.onDeviceLockStateRead(true)
        // TODO: Re-enable when this is made on the FW
        // device.queueTask(LockStateTask(callback))
    }
    val supportedSlots = "EMPTY,CUSTOM,URL,I_BEACON".split(',').map { Slot.Type.valueOf(it) }.toTypedArray()

    override fun read(): List<Task> {
        // TODO: Not hardcode but fw is changing a bunch so this stays hardcoded for now
        val supportedSlots = "EMPTY,CUSTOM,URL,I_BEACON".split(',').map { Slot.Type.valueOf(it) }.toTypedArray()
        // TODO: Not hardcode look above

        val tasks = parameters.read() + listOf<Task>(object : GenericTask("Initializing slots and arbitrary data") {
            override fun execute() {
                slots = Slots(parameters.getParameterValue<Number>("Slot amount", 6).toInt(), supportedSlots)
                arbitraryData = ArbitraryData(parameters.getParameterValue("Arb. data size", 1000))
                (slots.read() + arbitraryData.read()).reversed().forEach { taskQueue.offer(it) }
            }
        })
        device.queueTasks(tasks)
        return tasks
    }

    override fun write(full: Boolean): List<Task> {
        val tasks = parameters.write(full) + slots.write(full) + arbitraryData.write(full)
        device.queueTasks(tasks)
        return tasks
    }


    class LockStateTask(
            private val callback: LockStateRequestTaskCallback
    ) : CharacteristicReadTask(characteristicUUID = LOCK_STATE_PASSWORD_UUID) {
        override fun onSuccess(value: ByteArray) {
            if (value[0] == 0x01.toByte()) {
                Timber.e("Device unlocked")
                callback.onDeviceLockStateRead(true)
            } else {
                Timber.e("Device locked")
                callback.onDeviceLockStateRead(false)
            }
        }

        override fun onError(device: BluetoothDevice, e: Exception) {
            Timber.e("Error reading beacon lock state")
            super.onError(device, e)
        }

        interface LockStateRequestTaskCallback {
            fun onDeviceLockStateRead(unlocked: Boolean)
        }
    }

    class PasswordWriteTask(
            private val device: BluetoothDevice,
            private val password: String,
            private val checkCallback: LockStateTask.LockStateRequestTaskCallback? = null,
            private val checkValid: Boolean = true
    ) : CharacteristicWriteTask(characteristicUUID = LOCK_STATE_PASSWORD_UUID, value = password.toByteArray()) {
        override fun onSuccess() {
            Timber.e("Wrote password")
            if (checkValid && checkCallback != null) {
                device.queueTask(LockStateTask(checkCallback))
            }
        }

        override fun onError(device: BluetoothDevice, e: Exception) {
            Timber.e("Error writing beacon password $password")
            super.onError(device, e)
        }
    }

    companion object {
        @JvmField
        val matcher: Predicate<ScanResult> = Predicate { sr ->
            sr.scanRecord?.bytes?.let {
                if (it.size < 8) false
                else it.rangeContentEquals(0, 7, byteArrayOf(0x06, 0xFF.toByte(), 0x59, 0x00, 0x69, 0x02, 0x00))
            } ?: false
        }
    }
}