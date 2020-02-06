package com.aconno.sensorics.dagger.settings_framework

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aconno.sensorics.viewmodel.BeaconSettingsTransporterSharedViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class BeaconSettingsSharedViewModelFactory @Inject constructor() : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(BeaconSettingsTransporterSharedViewModel::class.java)) {
            BeaconSettingsTransporterSharedViewModel() as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}