package com.aconno.sensorics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

/**
 * This is shared viewModel which is used to communicate between different android components
 * (for example between activity and fragments)
 */
class BeaconSettingsTransporterSharedViewModel : ViewModel() {
    private val _beaconJsonLiveDataForFragments = MutableLiveData<String>()
    private val _beaconUpdatedJsonLiveDataForActivity = MutableLiveData<String>()

    /**
     * Subscribe in fragments
     */
    val beaconJsonLiveDataForFragments
        get() = _beaconJsonLiveDataForFragments
    /**
     * Subscribe in Activity
     */
    val beaconUpdatedJsonLiveDataForActivity
        get() = _beaconUpdatedJsonLiveDataForActivity

    /**
     * Use this method to send data from Activity to fragments
     */
    fun sendBeaconJsonToObservers(beaconJson: String) {
        Timber.d("send beacon json to fragments: $beaconJson")
        _beaconJsonLiveDataForFragments.value = beaconJson
    }

    /**
     * Use this method to send data from fragments to Activity
     */
    fun beaconDataChanged(updatedBeaconJson: String) {
        Timber.d("updatedBeaconJson is : $updatedBeaconJson")
        _beaconUpdatedJsonLiveDataForActivity.postValue(updatedBeaconJson)
    }
}