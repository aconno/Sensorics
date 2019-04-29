package com.aconno.sensorics.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import com.aconno.sensorics.LocationStateReceiver
import com.aconno.sensorics.device.location.LocationStateListener
import com.aconno.sensorics.viewmodel.LocationViewModel

class LocationViewModelFactory(
    private val locationStateReceiver: LocationStateReceiver,
    private val locationStateListener: LocationStateListener,
    private val application: Application
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = LocationViewModel(locationStateReceiver, locationStateListener, application)
        return getViewModel(viewModel, modelClass)
    }
}