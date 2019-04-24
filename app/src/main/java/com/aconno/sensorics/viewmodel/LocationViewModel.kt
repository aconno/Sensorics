package com.aconno.sensorics.viewmodel

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aconno.sensorics.LocationStateReceiver
import com.aconno.sensorics.device.location.LocationStateListener
import io.reactivex.disposables.Disposable

class LocationViewModel(
    private val locationStateReceiver: LocationStateReceiver,
    private val locationStateListener: LocationStateListener,
    private val application: Application
) : ViewModel() {

    private var locationStateDisposable: Disposable? = null

    val locationStateLiveData = MutableLiveData<Boolean>()

    fun getLocationStateUpdates() {
        application.applicationContext.registerReceiver(
            locationStateReceiver,
            IntentFilter(Intent.ACTION_PROVIDER_CHANGED)
        )
        locationStateDisposable = locationStateListener.getLocationStates().subscribe {
            locationStateLiveData.postValue(it)
        }
    }

    fun stopLocationStatuUpdates() {
        application.applicationContext.unregisterReceiver(locationStateReceiver)
        locationStateDisposable?.dispose()
    }

    override fun onCleared() {
        locationStateDisposable?.dispose()
        super.onCleared()
    }
}