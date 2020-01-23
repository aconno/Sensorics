package com.aconno.sensorics.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

object SaveChangesEvent

class BeaconInformationViewModel : ViewModel() {
    private val _beaconInformation = MutableLiveData<String>()

    val saveChangesLiveData = MutableLiveData<SaveChangesEvent>()
    val beaconInformation: LiveData<String>
        get() = _beaconInformation


    fun beaconInformationLoaded(beaconinfo: String) {
        _beaconInformation.value = beaconinfo
    }
}