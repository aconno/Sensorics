package com.aconno.sensorics.ui.configure

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aconno.bluetooth.beacon.Beacon

class BeaconViewModel : ViewModel() {
    val beacon: MutableLiveData<Beacon> = MutableLiveData()
}