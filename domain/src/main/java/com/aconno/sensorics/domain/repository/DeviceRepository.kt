package com.aconno.sensorics.domain.repository

import com.aconno.sensorics.domain.model.Device
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

interface DeviceRepository {

    fun getAllDevices(): Flowable<List<Device>>

    fun getAllDevicesMaybe(): Maybe<List<Device>>

    fun insertDevice(device: Device): Completable

    fun deleteDevice(device: Device): Completable

    fun updateDevice(device: Device): Completable
}