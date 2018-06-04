package com.aconno.acnsensa.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.domain.scanning.Bluetooth
import com.aconno.acnsensa.viewmodel.BluetoothScanningViewModel

/**
 * @author aconno
 */
class BluetoothScanningViewModelFactory(
    private val bluetooth: Bluetooth,
    private val acnSensaApplication: AcnSensaApplication
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = BluetoothScanningViewModel(bluetooth, acnSensaApplication)
        return getViewModel(viewModel, modelClass)
    }
}