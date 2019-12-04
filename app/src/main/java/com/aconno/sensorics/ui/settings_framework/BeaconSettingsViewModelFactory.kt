package com.aconno.sensorics.ui.settings_framework

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.viewmodel.factory.BaseViewModelFactory

class BeaconSettingsViewModelFactory : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = BeaconSettingsViewModel()
        return getViewModel(viewModel, modelClass)
    }
}