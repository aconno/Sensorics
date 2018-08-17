package com.aconno.sensorics.domain.format

import com.aconno.sensorics.domain.model.Device

interface ConnectionCharacteristicsFinder {

    fun hasCharacteristics(device: Device): Boolean

    fun addCharacteristicsToDevice(device: Device): Device
}