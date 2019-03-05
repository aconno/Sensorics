package com.aconno.bluetooth.beacon

import android.bluetooth.le.ScanResult
import com.aconno.bluetooth.BluetoothDevice
import com.aconno.bluetooth.DeviceSpec
import com.aconno.bluetooth.UUIDProvider
import com.aconno.bluetooth.tasks.CharacteristicReadTask
import com.aconno.bluetooth.tasks.CharacteristicWriteTask
import com.aconno.bluetooth.tasks.GenericTask
import com.aconno.bluetooth.tasks.Task
import com.aconno.bluetooth.toHex
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import io.reactivex.functions.Predicate
import timber.log.Timber
import java.lang.reflect.Type
import java.nio.ByteOrder

class Beacon(
    val paramDevice: BluetoothDevice,
    var name: String? = "",
    val address: String = "00:00:00:00:00:00",
    var connectible: Boolean = true,
    var rssi: Int = 0,
    var manufacturer: String = "Aconno",
    var model: String = "V1",
    val softwareVersion: String = "1",
    var hardwareVersion: String = "1",
    var firmwareVersion: String = "1",
    var advFeature: String = "N/A",
    var supportedTxPower: Array<Int> = arrayOf(),
    var supportedSlots: Array<Slot.Type> = arrayOf(),
    var slotAmount: Int = 0,
    var slots: MutableList<Slot?> = mutableListOf(),
    val parameters: MutableMap<String, MutableList<Parameter>> = mutableMapOf(),
    var abstractData: String = "",
    var abstractDataMapped: MutableMap<String, String> = mutableMapOf()
) : DeviceSpec(paramDevice) {
    val gson: Gson = GsonBuilder().create()
    var type: Type = object : TypeToken<Map<String, String>>() {}.type

    fun unlock(password: String, callback: LockStateTask.LockStateRequestTaskCallback) {
        device.queueTask(PasswordWriteTask(device, password, callback))
    }

    fun requestDeviceLockStatus(callback: LockStateTask.LockStateRequestTaskCallback) {
        callback.onDeviceLockStateRead(true)
        // TODO: Re-enable when this is made on the FW
        // paramDevice.queueTask(LockStateTask(callback))
    }

    fun read() {
        // TODO: Not hardcode but fw is changing a bunch so this stays hardcoded for now
        supportedSlots =
            "EMPTY,CUSTOM,URL,I_BEACON".split(',').map { Slot.Type.valueOf(it) }.toTypedArray()
        // TODO: Not hardcode look above

        device.queueTasks(readParameters() + listOf<Task>(object : GenericTask() {
            override fun execute() {
                slotAmount =
                    ((parameters.flatMap { it.value }.find { it.name == "Slot Amount" }?.value
                        ?: 6) as Number).toInt()

                slots = MutableList(slotAmount) { Slot() }
                device.queueTasks(readSlots())
            }
        }) + readAbstractData())
    }

    private fun readParameters(): List<Task> {
        return listOf(ParametersReadTask(this))
    }

    private fun readSlots(): List<Task> {
        return (0 until slotAmount).map { i -> SlotReadTask(this, i) }.toList()
    }

    private fun readAbstractData(): List<Task> {
        return (ABSTRACT_DATA_CHUNK_COUNT - 1 downTo 0).map {
            object : CharacteristicReadTask(characteristicUUID = ABSTRACT_DATA_UUID) {
                override fun onSuccess(value: ByteArray) {
                    abstractData += ValueConverter.UTF8STRING.converter.deserialize(
                        value,
                        ByteOrder.BIG_ENDIAN
                    ) as String
                    if (it == 0) {
                        abstractDataMapped = try {
                            gson.fromJson(abstractData, type) ?: mutableMapOf()
                        } catch (e: JsonSyntaxException) {
                            mutableMapOf()
                        }
                    }
                }
            }
        }
    }

    fun write(incremental: Boolean = false) {
        paramDevice.queueTasks(
            writeParameters(incremental) + writeSlots(incremental) + writeAbstractData(
                incremental
            )
        )
    }

    private fun writeParameters(incremental: Boolean): List<Task> {
        return parameters.flatMap { it.value }.filter { it.dirty or !incremental }.map {
            it.write()
        } + listOf(object : CharacteristicWriteTask(
            characteristicUUID = PARAMETER_INDEX_UUID,
            value = byteArrayOf(parameters.flatMap { it.value }.size.toByte())
        ) {
            override fun onSuccess() {
                Timber.i("Finalized Write of Parameters")
            }
        })
    }

    private fun writeSlots(incremental: Boolean): List<Task> {
        return slots.filterNotNull().filter { it.dirty or !incremental }.map { slot ->
            val slotIndex: Int = slots.indexOf(slot)
            object : CharacteristicWriteTask(
                characteristicUUID = SLOT_INDEX_UUID,
                value = byteArrayOf(slotIndex.toByte())
            ) {
                override fun onSuccess() {
                    slot.write().reversed().forEach { taskQueue.offer(it) }
                }

                override fun onError(device: BluetoothDevice, e: Exception) {
                    Timber.e("Failed to write slot id $slotIndex")
                    super.onError(device, e)
                }
            }
        }.toList() + listOf(object : CharacteristicWriteTask(
            characteristicUUID = SLOT_INDEX_UUID,
            value = byteArrayOf(slotAmount.toByte())
        ) {
            override fun onSuccess() {
                Timber.i("Finalized Write of Slots")
            }
        })
    }

    private fun writeAbstractData(incremental: Boolean): List<Task> {
        return gson.toJson(abstractDataMapped).let { json ->
            if (json != abstractData || !incremental) {
                json.toByteArray().extendOrShorten(ABSTRACT_DATA_CHUNK_TOTAL_SIZE)
                    .chunked(ABSTRACT_DATA_CHUNK_SIZE).mapIndexed { i, data ->
                    object : CharacteristicWriteTask(
                        characteristicUUID = ABSTRACT_DATA_UUID,
                        value = data
                    ) {
                        override fun onSuccess() {
                            Timber.i("Wrote part $i out of $ABSTRACT_DATA_CHUNK_TOTAL_SIZE for arbitrary data")
                        }
                    }
                }.toList()
            } else listOf()
        }
    }

    class SlotReadTask(val beacon: Beacon, val slotIndex: Int) : CharacteristicWriteTask(
        characteristicUUID = SLOT_INDEX_UUID,
        value = byteArrayOf(slotIndex.toByte())
    ) {
        override fun onSuccess() {
            taskQueue.offer(object : CharacteristicReadTask(characteristicUUID = SLOT_TYPE_UUID) {
                override fun onSuccess(value: ByteArray) {
                    beacon.slots[slotIndex] = Slot(value).apply {
                        read().reversed().forEach { taskQueue.offer(it) }
                    }
                }

                override fun onError(device: BluetoothDevice, e: Exception) {
                    Timber.e("Failed to read slot type for slot $slotIndex")
                    super.onError(device, e)
                }
            })
        }

        override fun onError(device: BluetoothDevice, e: Exception) {
            Timber.e("Failed to write slot id $slotIndex")
            super.onError(device, e)
        }
    }

    class ParametersReadTask(val beacon: Beacon) :
        CharacteristicReadTask(characteristicUUID = PARAMETER_COUNT_UUID) {
        override fun onSuccess(value: ByteArray) {
            val parameterCount = value[0]
            (0 until parameterCount).map { i ->
                ParameterReadTask(beacon, i)
            }.forEach {
                taskQueue.offer(it)
            }
        }

        override fun onError(device: BluetoothDevice, e: Exception) {
            Timber.e("Failed to read parameter count")
            super.onError(device, e)
        }

        class ParameterReadTask(val beacon: Beacon, val i: Int) : CharacteristicWriteTask(
            characteristicUUID = PARAMETER_INDEX_UUID,
            value = byteArrayOf(i.toByte())
        ) {
            override fun onSuccess() {
                taskQueue.offer(object :
                    CharacteristicReadTask(characteristicUUID = PARAMETER_GROUP_UUID) {
                    override fun onSuccess(value: ByteArray) {
                        val group: String =
                            com.aconno.bluetooth.ValueConverter.UTF8STRING.converter.deserialize(
                                value.copyOfRange(0, value.stringLength()),
                                order = ByteOrder.BIG_ENDIAN
                            ) as String
                        taskQueue.offer(object :
                            CharacteristicReadTask(characteristicUUID = PARAMETER_DATA_UUID) {
                            override fun onSuccess(value: ByteArray) {
                                Timber.e(value.toHex())
                                beacon.parameters.getOrPut(group) {
                                    mutableListOf()
                                }.add(Parameter(i, value))
                            }

                            override fun onError(device: BluetoothDevice, e: Exception) {
                                Timber.e("Failure to read parameter data for parameter $i")
                                super.onError(device, e)
                            }

                        })
                    }

                    override fun onError(device: BluetoothDevice, e: Exception) {
                        Timber.e("Failed to read parameter group for parameter $i")
                        super.onError(device, e)
                    }
                })
            }

            override fun onError(device: BluetoothDevice, e: Exception) {
                Timber.e("Failed to write parameter id $i")
                super.onError(device, e)
            }
        }
    }

    class LockStateTask(
        private val device: BluetoothDevice,
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
    ) : CharacteristicWriteTask(
        characteristicUUID = LOCK_STATE_PASSWORD_UUID,
        value = password.toByteArray()
    ) {
        override fun onSuccess() {
            Timber.e("Wrote password")
            if (checkValid && checkCallback != null) {
                device.queueTask(LockStateTask(device, checkCallback))
            }
        }

        override fun onError(device: BluetoothDevice, e: Exception) {
            Timber.e("Error writing beacon password $password")
            super.onError(device, e)
        }
    }


    companion object {
        private const val ABSTRACT_DATA_CHUNK_COUNT = 50
        private const val ABSTRACT_DATA_CHUNK_SIZE = 20
        private const val ABSTRACT_DATA_CHUNK_TOTAL_SIZE =
            ABSTRACT_DATA_CHUNK_SIZE * ABSTRACT_DATA_CHUNK_COUNT

        val SLOT_INDEX_UUID = UUIDProvider.provideFullUUID("B001")
        val SLOT_TYPE_UUID = UUIDProvider.provideFullUUID("B002")
        val SLOT_DATA_UUID = UUIDProvider.provideFullUUID("B003")
        val SLOT_ADV_INT_UUID = UUIDProvider.provideFullUUID("B004")
        val SLOT_RSSI_1M_UUID = UUIDProvider.provideFullUUID("B005")
        val SLOT_RADIO_TX_UUID = UUIDProvider.provideFullUUID("B006")
        val SLOT_TRIGGER_ENABLE_UUID = UUIDProvider.provideFullUUID("B007")
        val SLOT_TRIGGER_TYPE_UUID = UUIDProvider.provideFullUUID("B008")
        val PARAMETER_COUNT_UUID = UUIDProvider.provideFullUUID("C001")
        val PARAMETER_INDEX_UUID = UUIDProvider.provideFullUUID("C002")
        val PARAMETER_GROUP_UUID = UUIDProvider.provideFullUUID("C003")
        val PARAMETER_DATA_UUID = UUIDProvider.provideFullUUID("C004")
        val LOCK_STATE_PASSWORD_UUID = UUIDProvider.provideFullUUID("D001")
        val ABSTRACT_DATA_UUID = UUIDProvider.provideFullUUID("E001")

        @JvmField
        val matcher: Predicate<ScanResult> = Predicate { sr ->
            sr.scanRecord?.bytes?.let {
                if (it.size < 8) false
                else it.rangeContentEquals(
                    0,
                    7,
                    byteArrayOf(0x06, 0xFF.toByte(), 0x59, 0x00, 0x69, 0x02, 0x00)
                )
            } ?: false
        }

    }
}