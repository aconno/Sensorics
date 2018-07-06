package com.aconno.sensorics.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.domain.scanning.Bluetooth
import com.aconno.sensorics.viewmodel.BluetoothScanningViewModel

/**
 * @author aconno
 */
class BluetoothScanningViewModelFactory(
    private val bluetooth: Bluetooth,
    private val sensoricsApplication: SensoricsApplication
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = BluetoothScanningViewModel(bluetooth, sensoricsApplication)
        return getViewModel(viewModel, modelClass)
    }
}