package com.aconno.sensorics.viewmodel.factory

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.domain.scanning.Bluetooth
import com.aconno.sensorics.viewmodel.BluetoothScanningViewModel

class BluetoothScanningViewModelFactory(
    private val bluetooth: Bluetooth,
    private val sensoricsApplication: SensoricsApplication
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = BluetoothScanningViewModel(sensoricsApplication, bluetooth)
        return getViewModel(viewModel, modelClass)
    }
}