package com.aconno.acnsensa.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.viewmodel.BeaconListViewModel
import io.reactivex.Flowable

class BeaconListViewModelFactory(private val beacons: Flowable<Device>) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = BeaconListViewModel(beacons)
        return getViewModel(viewModel, modelClass)
    }
}