package com.aconno.sensorics.ui.settings_framework

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aconno.bluetooth.BluetoothDevice
import com.aconno.bluetooth.beacon.Beacon

class BeaconSettingsViewModel : ViewModel() {
    val beacon: MutableLiveData<Beacon> = MutableLiveData()
    val device: MutableLiveData<BluetoothDevice> = MutableLiveData()
}