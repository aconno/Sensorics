package com.aconno.sensorics.device.location

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class LocationStateListener {

    private val locationStateSubject: PublishSubject<Boolean> = PublishSubject.create()


    fun getLocationStates(): Observable<Boolean> {
        return locationStateSubject
    }

    fun onLoationStateEvent(isLocationOn: Boolean) {
        locationStateSubject.onNext(isLocationOn)
    }
}