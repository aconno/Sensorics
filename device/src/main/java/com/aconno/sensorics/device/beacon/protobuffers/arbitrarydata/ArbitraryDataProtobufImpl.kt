package com.aconno.sensorics.device.beacon.protobuffers.arbitrarydata

import com.aconno.sensorics.device.beacon.baseimpl.ArbitraryData
import com.aconno.sensorics.device.beacon.protobuffers.generatedmodel.ArbitraryDataProtobufModel
import com.aconno.sensorics.device.beacon.v2.arbitrarydata.ArbitraryDataImpl
import com.aconno.sensorics.device.bluetooth.tasks.CharacteristicReadTask
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.UINT32
import com.aconno.sensorics.domain.migrate.getValueForUpdate
import com.aconno.sensorics.domain.scanning.BluetoothTaskProcessor
import com.aconno.sensorics.domain.scanning.Task
import java.util.*
import java.util.zip.CRC32

class ArbitraryDataProtobufImpl(
    var serviceUuid: UUID = ArbitraryDataImpl.DEFAULT_ARBITRARY_DATA_SERVICE_UUID,
    var uuid: UUID = ArbitraryDataImpl.DEFAULT_ARBITRARY_DATA_CHARACTERISTIC_UUID
) : ArbitraryData() {
    private lateinit var arbitraryDataModel : ArbitraryDataProtobufModel.ArbitraryData

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

                    arbitraryDataModel.arbitraryDataMap.entries.forEach {
                        this@ArbitraryDataProtobufImpl[it.key] = it.value
                    }

                    available.postValue(capacity - getSerializedSize())

                }
            }
        }
    }


    override fun write(taskProcessor: BluetoothTaskProcessor, full: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val dirty: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun serialize(): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    //TODO: first update arbitraryDataModel, then just return arbitraryDataModel.serializedSize
    private fun getSerializedSize() : Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun set(key: String, value: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setAll(newMap: Map<String, String>): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeEntry(key: String): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}