package com.aconno.sensorics.viewmodel

import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.location.LocationManager
import androidx.core.location.LocationManagerCompat
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

    val locationStateLiveData = MutableLiveData<Boolean>()

    private var locationStateDisposable: Disposable? = null

    fun startLocationStateUpdates() {
        postCurrentLocationState()
        registerLocationReceiver()
        subscribeToLocationStateUpdates()
    }

    fun stopObservingLocationUpdates() {
        application.applicationContext.unregisterReceiver(locationStateReceiver)
        locationStateDisposable?.dispose()
    }

    private fun postCurrentLocationState() {
        val locationManager =
            application.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isLocationEnabled = LocationManagerCompat.isLocationEnabled(locationManager)
        locationStateLiveData.postValue(isLocationEnabled)
    }

    private fun registerLocationReceiver() {
        application.applicationContext.registerReceiver(
            locationStateReceiver,
            IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        )
    }

    private fun subscribeToLocationStateUpdates() {
        locationStateDisposable = locationStateListener.getLocationStates().subscribe {
            locationStateLiveData.postValue(it)
        }
    }

    override fun onCleared() {
        locationStateDisposable?.dispose()
        super.onCleared()
    }
}