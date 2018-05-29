package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.model.Device
import io.reactivex.Flowable

class BeaconListViewModel(
    private val beacons: Flowable<Device>
) : ViewModel() {

    private val beaconsLiveData: MutableLiveData<Device> = MutableLiveData()

    init {
        beacons.subscribe {
            beaconsLiveData.value = it
        }
    }

    fun getBeaconsLiveData(): MutableLiveData<Device> {
        return beaconsLiveData
    }
}