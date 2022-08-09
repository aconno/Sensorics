package com.aconno.sensorics.domain.repository

import io.reactivex.Completable
import io.reactivex.Single

interface Settings {

    fun getLastClickedDeviceMac(): Single<String>

    fun setClickedDeviceMac(macAddress: String): Completable
}