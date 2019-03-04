package com.aconno.bluetooth.beacon

import android.bluetooth.le.ScanResult
import com.aconno.bluetooth.BluetoothDevice
import com.aconno.bluetooth.DeviceSpec
import com.aconno.bluetooth.UUIDProvider
import com.aconno.bluetooth.tasks.ReadTask
import com.aconno.bluetooth.tasks.Task
import com.aconno.bluetooth.tasks.WriteTask
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
    val ABSTRACT_DATA_CHUNK_COUNT = 1
    val ABSTRACT_DATA_CHUNK_SIZE = 50

    val gson: Gson = GsonBuilder().create()
    var type: Type = object : TypeToken<Map<String, String>>() {}.type


    fun unlock(password: String, callback: LockStateTask.LockStateRequestTaskCallback) {
        paramDevice.queueTask(PasswordWriteTask(paramDevice, password, callback))
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
        slotAmount = 6

        slots = MutableList(slotAmount) { i ->
            Slot()
        }

        paramDevice.queueTasks(readParameters() + readSlots() + readAbstractData())
    }

    private fun readParameters(): List<Task> {
        return listOf(ParametersReadTask(this))
    }

    private fun readSlots(): List<Task> {
        return (0 until slotAmount).map { i -> SlotReadTask(this, i) }.toList()
    }

    private fun readAbstractData(): List<Task> {
        return (ABSTRACT_DATA_CHUNK_COUNT - 1 downTo 0).map {
            object : ReadTask(UUIDProvider.provideFullUUID("E001")) {
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

                override fun onError(error: Int) {
                    TODO("Not implemented")
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
        } + listOf(object : WriteTask(
            UUIDProvider.provideFullUUID("C002"),
            byteArrayOf(parameters.flatMap { it.value }.size.toByte())
        ) {
            override fun onSuccess() {
                Timber.i("Finalized Write of Parameters")
            }

            override fun onError(error: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun writeSlots(incremental: Boolean): List<Task> {
        return slots.filterNotNull().filter { it.dirty or !incremental }.map { slot ->
            val slotIndex: Int = slots.indexOf(slot)
            object :
                WriteTask(UUIDProvider.provideFullUUID("B001"), byteArrayOf(slotIndex.toByte())) {
                override fun onSuccess() {
                    slot.write().reversed().forEach { taskQueue.offer(it) }
                }

                override fun onError(error: Int) {
                    Timber.e("Failed to write slot id $slotIndex")
                    throw IllegalStateException("Handle this state")
                }
            }
        }.toList() + listOf(object :
            WriteTask(UUIDProvider.provideFullUUID("B001"), byteArrayOf(slotAmount.toByte())) {
            override fun onSuccess() {
                Timber.i("Finalized Write of Slots")
            }

            override fun onError(error: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun writeAbstractData(incremental: Boolean): List<Task> {
        return gson.toJson(abstractDataMapped).let { json ->
            if (json != abstractData || !incremental) (0 until ABSTRACT_DATA_CHUNK_COUNT).map {
                object : WriteTask(
                    UUIDProvider.provideFullUUID("E001"),
                    json.toByteArray().extendOrShorten(200).copyOfRange(it * 50, it * 50 + 50)
                ) {
                    override fun onSuccess() {
                        Timber.i("Wrote part $it of 4 for arbitrary data")
                    }

                    override fun onError(error: Int) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                }
            }.toList() else listOf()
        }
    }


    class SlotReadTask(val beacon: Beacon, val slotIndex: Int) :
        WriteTask(UUIDProvider.provideFullUUID("B001"), byteArrayOf(slotIndex.toByte())) {
        override fun onSuccess() {
            taskQueue.offer(object : ReadTask(UUIDProvider.provideFullUUID("B002")) {
                override fun onSuccess(value: ByteArray) {
                    beacon.slots[slotIndex] = Slot(value).apply {
                        read().reversed().forEach { taskQueue.offer(it) }
                    }
                }

                override fun onError(error: Int) {
                    Timber.e("Failed to read slot type for slot $slotIndex")
                }
            })
        }

        override fun onError(error: Int) {
            Timber.e("Failed to write slot id $slotIndex")
            throw IllegalStateException("Handle this state")
        }
    }

    class ParametersReadTask(val beacon: Beacon) : ReadTask(UUIDProvider.provideFullUUID("C001")) {
        override fun onSuccess(value: ByteArray) {
            val parameterCount = value[0]
            (0 until parameterCount).map { i ->
                ParameterReadTask(beacon, i)
            }.forEach {
                taskQueue.offer(it)
            }
        }

        override fun onError(error: Int) {
            Timber.e("Failed to read parameter count")
            throw IllegalStateException("Handle this state")
        }

        class ParameterReadTask(val beacon: Beacon, val i: Int) :
            WriteTask(UUIDProvider.provideFullUUID("C002"), byteArrayOf(i.toByte())) {
            override fun onSuccess() {
                taskQueue.offer(object : ReadTask(UUIDProvider.provideFullUUID("C003")) {
                    override fun onSuccess(value: ByteArray) {
                        val group: String =
                            com.aconno.bluetooth.ValueConverter.UTF8STRING.converter.deserialize(
                                value.copyOfRange(0, value.stringLength()),
                                order = ByteOrder.BIG_ENDIAN
                            ) as String
                        taskQueue.offer(object : ReadTask(UUIDProvider.provideFullUUID("C004")) {
                            override fun onSuccess(value: ByteArray) {
                                Timber.e(value.toHex())
                                beacon.parameters.getOrPut(group) {
                                    mutableListOf()
                                }.add(Parameter(i, value))
                            }

                            override fun onError(error: Int) {
                                Timber.e("Failure to read parameter data for parameter $i")
                                throw IllegalStateException("Handle this state")
                            }

                        })
                    }

                    override fun onError(error: Int) {
                        Timber.e("Failed to read parameter group for parameter $i")
                        throw IllegalStateException("Handle this state")
                    }
                })
            }

            override fun onError(error: Int) {
                Timber.e("Failed to write parameter id $i")
                throw IllegalStateException("Handle this state")
            }
        }
    }

    class LockStateTask(
        private val callback: LockStateRequestTaskCallback
    ) : ReadTask(UUIDProvider.provideFullUUID("D001")) {
        override fun onSuccess(value: ByteArray) {
            if (value[0] == 0x01.toByte()) {
                Timber.e("Device unlocked")
                callback.onDeviceLockStateRead(true)
            } else {
                Timber.e("Device locked")
                callback.onDeviceLockStateRead(false)
            }
        }

        override fun onError(error: Int) {
            Timber.e("Error reading beacon lock state")
            throw IllegalStateException("Handle this state")
        }

        interface LockStateRequestTaskCallback {
            fun onDeviceLockStateRead(unlocked: Boolean)
        }
    }

    class PasswordWriteTask(
            private val bluetoothDevice: BluetoothDevice,
            private val password: String,
            private val checkCallback: LockStateTask.LockStateRequestTaskCallback? = null,
            private val checkValid: Boolean = true
    ) : WriteTask(UUIDProvider.provideFullUUID("D001"), password.toByteArray()) {
        override fun onSuccess() {
            Timber.e("Wrote password")
            if (checkValid && checkCallback != null) {
                bluetoothDevice.queueTask(LockStateTask(checkCallback))
            }
        }

        override fun onError(error: Int) {
            Timber.e("Error writing beacon password $password")
            throw IllegalStateException("Handle this state")
        }
    }


    companion object {
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