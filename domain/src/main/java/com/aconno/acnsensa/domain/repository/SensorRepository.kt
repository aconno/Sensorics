package com.aconno.acnsensa.domain.repository

import com.aconno.acnsensa.domain.model.Device
import io.reactivex.Completable
import io.reactivex.Single

interface SensorRepository {

    fun getAllDevices(): Single<List<Device>>

    fun getDevice(macAddress: String): Single<Device>

    fun addDevice(macAddress: String): Completable

    fun addDevice(macAddress: String, name: String): Completable

    fun removeDevice(macAddress: String): Completable
}