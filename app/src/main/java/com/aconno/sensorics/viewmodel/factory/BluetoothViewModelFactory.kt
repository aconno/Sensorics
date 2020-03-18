package com.aconno.sensorics.viewmodel.factory

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.scanning.Bluetooth
import com.aconno.sensorics.viewmodel.BluetoothViewModel

class BluetoothViewModelFactory(
    private val bluetooth: Bluetooth
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = BluetoothViewModel(bluetooth)
        return getViewModel(viewModel, modelClass)
    }
}