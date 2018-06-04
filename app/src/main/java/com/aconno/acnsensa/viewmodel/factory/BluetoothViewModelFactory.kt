package com.aconno.acnsensa.viewmodel.factory

import android.app.Application
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.BluetoothStateReceiver
import com.aconno.acnsensa.domain.scanning.Bluetooth
import com.aconno.acnsensa.viewmodel.BluetoothViewModel

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