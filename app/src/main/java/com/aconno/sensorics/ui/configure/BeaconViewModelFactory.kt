package com.aconno.sensorics.ui.configure

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.viewmodel.factory.BaseViewModelFactory

class BeaconViewModelFactory : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = BeaconViewModel()
        return getViewModel(viewModel, modelClass)
    }
}