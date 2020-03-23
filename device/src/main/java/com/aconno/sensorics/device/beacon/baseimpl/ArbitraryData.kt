package com.aconno.sensorics.device.beacon.baseimpl

import com.aconno.sensorics.device.beacon.ArbitraryData
import com.aconno.sensorics.domain.scanning.BluetoothTaskProcessor
import com.aconno.sensorics.domain.scanning.Task

abstract class ArbitraryData(size: Int = 0) : ArbitraryData(size) {
    abstract fun read(taskProcessor: BluetoothTaskProcessor): Task

    abstract fun write(taskProcessor: BluetoothTaskProcessor, full: Boolean)
}