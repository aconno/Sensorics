package com.aconno.sensorics.domain.repository

import com.aconno.sensorics.domain.model.Device
import io.reactivex.Completable
import io.reactivex.Flowable

interface DeviceRepository {

    fun getAllDevices(): Flowable<List<Device>>

    fun insertDevice(device: Device): Completable

    fun deleteDevice(device: Device): Completable
}