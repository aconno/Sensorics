package com.aconno.sensorics.ui.settings_framework

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aconno.sensorics.device.beacon.Beacon

class BeaconSettingsViewModel : ViewModel() {
    val beacon: MutableLiveData<Beacon> = MutableLiveData()
}