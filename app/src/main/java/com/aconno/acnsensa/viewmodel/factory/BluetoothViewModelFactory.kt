package com.aconno.acnsensa.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.support.v4.content.LocalBroadcastManager
import com.aconno.acnsensa.BluetoothStateReceiver
import com.aconno.acnsensa.domain.Bluetooth
import com.aconno.acnsensa.viewmodel.BluetoothViewModel

class BluetoothViewModelFactory(
    private val bluetooth: Bluetooth,
    private val bluetoothStateReceiver: BluetoothStateReceiver,
    private val localBroadcastManager: LocalBroadcastManager
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = BluetoothViewModel(bluetooth, bluetoothStateReceiver, localBroadcastManager)
        return getViewModel(viewModel, modelClass)
    }
}