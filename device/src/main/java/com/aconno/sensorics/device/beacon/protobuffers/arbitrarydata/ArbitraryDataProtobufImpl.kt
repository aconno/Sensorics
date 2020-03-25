package com.aconno.sensorics.device.beacon.protobuffers.arbitrarydata

import com.aconno.sensorics.device.beacon.baseimpl.ArbitraryDataBaseImpl
import com.aconno.sensorics.device.beacon.protobuffers.generatedmodel.ArbitraryDataProtobufModel
import com.aconno.sensorics.device.bluetooth.tasks.CharacteristicReadTask
import com.aconno.sensorics.device.bluetooth.tasks.CharacteristicWriteTask
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.UINT32
import com.aconno.sensorics.domain.migrate.getValueForUpdate
import com.aconno.sensorics.domain.scanning.BluetoothTaskProcessor
import com.aconno.sensorics.domain.scanning.Task
import java.util.*
import java.util.zip.CRC32

class ArbitraryDataProtobufImpl(
    var serviceUuid: UUID = DEFAULT_ARBITRARY_DATA_SERVICE_UUID,
    var uuid: UUID = DEFAULT_ARBITRARY_DATA_CHARACTERISTIC_UUID
) : ArbitraryDataBaseImpl() {
    private lateinit var arbitraryDataModel : ArbitraryDataProtobufModel.ArbitraryData

    var initialState : Map<String,String>? = null

    override fun read(taskProcessor: BluetoothTaskProcessor): Task {
        return object : CharacteristicReadTask(
            name = "Arbitrary Data Read Task",
            serviceUUID = serviceUuid,
            characteristicUUID = uuid
        ) {
            var data: ByteArray = byteArrayOf()
            var totalSize: Int = 0

            override fun onSuccess(value: ByteArray) {
                data += value
                totalSize = UINT32.deserialize(data,0).toInt()

                taskQueue.clear()
                if (data.size < totalSize) {
                    taskQueue.offer(this.apply {
                        active = false
                    })
                } else {
                    //first 4 bytes are for total size, last 4 bytes for crc and all bytes in between are for arbitrary data protobuf model
                    arbitraryDataModel = ArbitraryDataProtobufModel.ArbitraryData.parseFrom(data.sliceArray(IntRange(4,data.size-5)))

                    capacity = arbitraryDataModel.capacity

                    val crcGiven: Long = UINT32.deserialize(data, data.size - 4)
                    val crcCalculated: Long = CRC32().getValueForUpdate(data.copyOf(data.size - 4))

                    if (crcGiven != crcCalculated) {
                        throw IllegalStateException("CRC doesn't match!")
                    }

                    arbitraryDataModel.arbitraryData.valuesMap.entries.forEach {
                        this@ArbitraryDataProtobufImpl[it.key] = it.value
                    }

                    initialState = emptyMap<String,String>().apply {
                        putAll(this)
                    }

                    available.postValue(capacity - getSerializedSize())

                }
            }
        }
    }

    override fun getSerializedSize(map: Map<String, String>): Int {
        val protobufModel = buildProtobufModelForMap(map)
        return protobufModel.arbitraryData.serializedSize
    }

    private fun updateProtobufModel() {
        arbitraryDataModel = buildProtobufModelForMap(this)
    }

    private fun buildProtobufModelForMap(map: Map<String, String>) : ArbitraryDataProtobufModel.ArbitraryData {
        return ArbitraryDataProtobufModel.ArbitraryData.newBuilder()
            .setCapacity(capacity)
            .setArbitraryData(ArbitraryDataProtobufModel.Values.newBuilder().putAllValues(map))
            .build()
    }

    override fun write(taskProcessor: BluetoothTaskProcessor, full: Boolean) {
        if(!dirty && !full) {
            return
        }
        updateProtobufModel()

        arbitraryDataModel.arbitraryData.toByteArray()
            .let { it + UINT32.serialize(CRC32().getValueForUpdate(it)) }
            .also {
                taskProcessor.queueTask(object : CharacteristicWriteTask(
                    name = "Arbitrary Data Write Task",
                    serviceUUID = serviceUuid,
                    characteristicUUID = uuid,
                    value = it
                ) {
                    override fun onSuccess() {
                    }
                })
            }
    }

    override val dirty: Boolean
        get() = initialState != this

    override fun serialize(): ByteArray {
        updateProtobufModel()
        return arbitraryDataModel.arbitraryData.toByteArray()
    }

    override fun serialize(map: Map<String, String>): ByteArray {
        return buildProtobufModelForMap(map).arbitraryData.toByteArray()
    }


}