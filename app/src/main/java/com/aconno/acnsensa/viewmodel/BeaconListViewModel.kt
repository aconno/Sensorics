package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.model.Device
import io.reactivex.Flowable
import timber.log.Timber

class BeaconListViewModel(
    private val beacons: Flowable<Device>
) : ViewModel() {

    private val beaconsLiveData: MutableLiveData<MutableList<Device>> = MutableLiveData()

    init {
        beacons.subscribe {
            val beacons = beaconsLiveData.value ?: mutableListOf()
            if (!beacons.contains(it)) {
                beacons.add(it)
                beaconsLiveData.value = beacons
            }
        }
    }

    fun getBeaconsLiveData(): MutableLiveData<MutableList<Device>> {
        return beaconsLiveData
    }
}