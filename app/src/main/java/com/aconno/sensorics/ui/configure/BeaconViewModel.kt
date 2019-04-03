package com.aconno.sensorics.ui.configure

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.bluetooth.beacon.Beacon

class BeaconViewModel : ViewModel() {
    val beacon: MutableLiveData<Beacon> = MutableLiveData()
}