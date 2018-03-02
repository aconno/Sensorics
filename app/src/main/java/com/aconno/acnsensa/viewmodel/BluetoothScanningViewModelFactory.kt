package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.domain.Bluetooth

/**
 * @author aconno
 */
class BluetoothScanningViewModelFactory(
    private val bluetooth: Bluetooth,
    private val acnSensaApplication: AcnSensaApplication
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST") //Safe to suppress since as? casting is being used.
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        val viewModel: T? = BluetoothScanningViewModel(
            bluetooth,
            acnSensaApplication
        ) as? T
        viewModel?.let { return viewModel }

        throw IllegalArgumentException("Illegal cast for BluetoothScanningViewModel")
    }
}