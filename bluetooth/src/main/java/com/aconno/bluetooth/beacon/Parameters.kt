package com.aconno.bluetooth.beacon

import com.aconno.bluetooth.*
import timber.log.Timber
import java.nio.ByteOrder

val PARAMETER_COUNT_UUID = UUIDProvider.provideFullUUID("C001")
val PARAMETER_INDEX_UUID = UUIDProvider.provideFullUUID("C002")
val PARAMETER_GROUP_UUID = UUIDProvider.provideFullUUID("C003")
val PARAMETER_DATA_UUID = UUIDProvider.provideFullUUID("C004")

class Parameters(
        val map: MutableMap<String, MutableList<Parameter>> = mutableMapOf(),
        var count: Int = 0
) : BleReadableWritable {


    override fun read(): List<Task> {
        return listOf(ParameterCountReadTask(), object : GenericTask("Adding parameter tasks") {
            override fun execute() {
                (0 until count).map { ParameterReadTask(it) }.forEach { taskQueue.offer(it) }
            }
        })
    }

    override fun write(full: Boolean): List<Task> {
        return map.flatMap { it.value }.filter { it.dirty or full }.map { it.write() } +
                listOf(object : CharacteristicWriteTask("Saving Parameters to Flash", characteristicUUID = PARAMETER_INDEX_UUID, value = byteArrayOf(count.toByte())) {
                    override fun onSuccess() {
                        Timber.i("Finalized Write of Parameters")
                    }
                })
    }

    inner class ParameterCountReadTask : CharacteristicReadTask("Reading parameter count", characteristicUUID = PARAMETER_COUNT_UUID) {
        override fun onSuccess(value: ByteArray) {
            count = value[0].toInt()
        }
    }

    inner class ParameterReadTask(val i: Int) : CharacteristicWriteTask("Writing parameter $i index", characteristicUUID = PARAMETER_INDEX_UUID, value = byteArrayOf(i.toByte())) {
        override fun onSuccess() {
            taskQueue.offer(object : CharacteristicReadTask("Reading parameter $i group", characteristicUUID = PARAMETER_GROUP_UUID) {
                override fun onSuccess(value: ByteArray) {
                    val group: String = ValueConverter.UTF8STRING.converter.deserialize(value.copyOfRange(0, value.stringLength()), order = ByteOrder.BIG_ENDIAN) as String
                    taskQueue.offer(object : CharacteristicReadTask("Reading parameter $i data", characteristicUUID = PARAMETER_DATA_UUID) {
                        override fun onSuccess(value: ByteArray) {
                            Timber.e(value.toHex())
                            val parameter = Parameter(i, value)
                            Timber.i("Parameter: ${parameter.name} loaded with value ${parameter.value}")
                            map.getOrPut(group) { mutableListOf() }.add(parameter)
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

    fun getParameterString(name: String): String = getParameterValue(name, "Unavailable")

    inline fun <reified T> getParameterValue(name: String, default: T): T = map
            .flatMap { it.value }
            .find { it.name == name }
        ?.value as T? ?: (default)
}