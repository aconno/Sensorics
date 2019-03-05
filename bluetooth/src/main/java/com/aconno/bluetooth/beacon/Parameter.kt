package com.aconno.bluetooth.beacon

import com.aconno.bluetooth.BluetoothDevice
import com.aconno.bluetooth.beacon.Beacon.Companion.PARAMETER_DATA_UUID
import com.aconno.bluetooth.beacon.Beacon.Companion.PARAMETER_INDEX_UUID
import com.aconno.bluetooth.tasks.CharacteristicWriteTask
import com.aconno.bluetooth.tasks.Task
import timber.log.Timber
import java.nio.ByteOrder
import kotlin.experimental.and

class Parameter(
    val id: Int,
    val data: ByteArray
) {
    var dirty: Boolean = false

    val type: ValueConverter =
        when ((ValueConverter.UINT16.converter.deserialize(data.copyOfRange(0, 2)) as Int)) {
            0 -> ValueConverter.BOOLEAN
            1 -> ValueConverter.UINT8
            2 -> ValueConverter.UINT16
            3 -> ValueConverter.UINT32
            4 -> ValueConverter.SINT8
            5 -> ValueConverter.SINT16
            6 -> ValueConverter.SINT32
            7 -> ValueConverter.FLOAT
            8 -> ValueConverter.ENUM
            9 -> ValueConverter.UTF8STRING
            else -> TODO("Unimplemented type")
        }

    val writable: Boolean = (data[2] and 0x01) == 0x00.toByte()

    val name: String = ValueConverter.UTF8STRING.converter.deserialize(
        data.copyOfRange(4, 24), order = ByteOrder.BIG_ENDIAN
    ) as String

    val unit: String = ValueConverter.UTF8STRING.converter.deserialize(
        data.copyOfRange(28, 40), order = ByteOrder.BIG_ENDIAN
    ) as String

    val choices: List<String>? by lazy {
        if (type == ValueConverter.ENUM) {
            (ValueConverter.UTF8STRING.converter.deserialize(
                data.copyOfRange(40, 80), ByteOrder.BIG_ENDIAN
            ) as String).split(',')
        } else null
    }

    var value: Any = when (type) {
        ValueConverter.ENUM ->
            (type.converter.deserialize(data.copyOfRange(24, 28))!! as Long).toInt()
        ValueConverter.UTF8STRING ->
            type.converter.deserialize(
                data.copyOfRange(24, 24 + data.stringLength(24)),
                order = ByteOrder.BIG_ENDIAN
            )!!
        else -> type.converter.deserialize(data.copyOfRange(24, 28))!!
    }
        set(value) {
            dirty = (field != value) or dirty
            field = type.converter.fromString(value.toString())!!
        }

    val min: Int = ValueConverter.SINT32.converter.deserialize(data.copyOfRange(40, 44)) as Int
    val max: Int = ValueConverter.SINT32.converter.deserialize(data.copyOfRange(44, 48)) as Int

    fun write(): Task {
        return object : CharacteristicWriteTask(
            characteristicUUID = PARAMETER_INDEX_UUID,
            value = byteArrayOf(id.toByte())
        ) {
            override fun onSuccess() {
                Timber.e("About to write parameter: $name")
                taskQueue.offer(object : CharacteristicWriteTask(
                    characteristicUUID = PARAMETER_DATA_UUID,
                    value = type.converter.serialize(this@Parameter.value.toString()).extendOrShorten(
                        56
                    )
                ) {
                    override fun onSuccess() {
                        Timber.i("Written data for parameter $id")
                        dirty = false
                    }

                    override fun onError(device: BluetoothDevice, e: Exception) {
                        Timber.e("Error writing parameter $id data!")
                        super.onError(device, e)
                    }
                })
            }

            override fun onError(device: BluetoothDevice, e: Exception) {
                Timber.e("Error writing parameter id for id $id!")
                super.onError(device, e)
            }
        }
    }

}
