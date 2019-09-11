package com.aconno.sensorics.device.beacon.v2.parameters

import com.aconno.sensorics.device.beacon.Parameters
import com.aconno.sensorics.domain.migrate.*
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.UINT32
import timber.log.Timber
import java.util.zip.CRC32

class ParametersImpl : Parameters() {
    override var count: Int = 0
    override lateinit var config: Config

    override fun fromBytes(data: ByteArray) {
        val crcGiven: Long = UINT32.deserialize(data, data.size - 4)
        val crcCalculated: Long = CRC32().getValueForUpdate(data.copyOf(data.size - 4))

        if (crcGiven != crcCalculated) {
            throw IllegalStateException("CRC doesn't match!")
        }

        val reader: ValueReader = ValueReaderImpl(data)
        // Read characteristic size
        reader.readUInt32()
        val parameterMaxValueSize: Int = reader.readUInt32().toInt()
        val parameterNameSize: Short = reader.readUInt8()
        val parameterUnitSize: Short = reader.readUInt8()
        val parameterGroupCount: Short = reader.readUInt8()
        val parameterGroupNameSize: Int = reader.readUInt8().toInt()


        config = Config(
            parameterNameSize.toInt(),
            parameterUnitSize.toInt(),
            parameterMaxValueSize
        )

        Timber.d("Parameter max value size - $parameterMaxValueSize")
        Timber.d("Parameter name size - $parameterMaxValueSize")
        Timber.d("Parameter unit size- $parameterMaxValueSize")
        Timber.d("Parameter group count - $parameterMaxValueSize")
        Timber.d("Parameter group name size - $parameterMaxValueSize")

        var currentParameterIndex = 0
        for (groupIndex in 0 until parameterGroupCount) {
            val parameterCount: Int = reader.readUInt32().toInt()
            val parameterGroupName: String = reader.readAsciiString(parameterGroupNameSize)

            this[parameterGroupName] = mutableListOf()

            Timber.d("Parameter group $parameterGroupName ($parameterCount parameters)")

            for (parameterIndex in 0 until parameterCount) {
                val index = reader.currentIndex
                val parameter = BaseParameterImpl.Factory.create<Any>(currentParameterIndex, reader, config)
                Timber.d("${parameter.name} - ${parameter.getValue()} - ${data.copyOfRange(index, reader.currentIndex).toCompactHex()}")
                this[parameterGroupName]?.add(parameter)

                currentParameterIndex++
            }
        }

        Timber.d("Processed all parameters")
    }

    override fun toBytes(): ByteArray {
        val allData = flatten().sortedBy { it.id }.map { it.toBytes() }.flatten()
        Timber.d(allData.toHex())
        return allData.let { it + UINT32.serialize(CRC32().getValueForUpdate(it)) }
    }
}