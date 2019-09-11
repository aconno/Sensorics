package com.aconno.sensorics.device.beacon.v2.arbitrarydata

import com.aconno.sensorics.device.beacon.ArbitraryData
import com.aconno.sensorics.domain.scanning.BluetoothTaskProcessor

abstract class ArbitraryData(size: Int = 0) : ArbitraryData(size) {
    abstract fun read(taskProcessor: BluetoothTaskProcessor)

    abstract fun write(taskProcessor: BluetoothTaskProcessor, full: Boolean)
}