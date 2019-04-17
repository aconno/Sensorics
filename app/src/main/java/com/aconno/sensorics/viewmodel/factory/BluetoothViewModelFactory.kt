package com.aconno.sensorics.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import com.aconno.sensorics.BluetoothStateReceiver
import com.aconno.sensorics.domain.scanning.Bluetooth
import com.aconno.sensorics.viewmodel.BluetoothViewModel

class BluetoothViewModelFactory(
    private val bluetooth: Bluetooth,
    private val bluetoothStateReceiver: BluetoothStateReceiver,
    private val application: Application
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = BluetoothViewModel(bluetooth, bluetoothStateReceiver, application)
        return getViewModel(viewModel, modelClass)
    }
}