package com.aconno.sensorics.domain.format

import com.aconno.sensorics.domain.interactor.resources.GetFormatsUseCase
import com.aconno.sensorics.domain.model.Device

class ConnectionCharacteristicsFinderImpl(
    private val getFormatsUseCase: GetFormatsUseCase
) : ConnectionCharacteristicsFinder {

    private val supportedFormats
        get() = getFormatsUseCase.execute()

    private val filteredConnections = supportedFormats.filter { it.isConnectible() }

    override fun hasCharacteristics(device: Device): Boolean {
        return if (device.connectable) {
            filteredConnections.find {
                device.name == it.getName()
            } != null
        } else {
            false
        }
    }

    override fun addCharacteristicsToDevice(device: Device): Device {
        if (hasCharacteristics(device)) {

            val connection = filteredConnections.find {
                device.name == it.getName()
            }!!

            return Device(
                device.name,
                device.alias,
                device.macAddress,
                device.icon,
                device.connectable,
                connection.getConnectionWriteList(),
                connection.getConnectionReadList()
            )
        } else {
            return device
        }
    }
}