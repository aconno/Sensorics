package com.aconno.acnsensa.domain.repository

import com.aconno.acnsensa.domain.model.Device
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

interface DeviceRepository {

    fun getAllDevices(): Flowable<List<Device>>

    fun getAllDevicesMaybe(): Maybe<List<Device>>

    fun insertDevice(device: Device): Completable

    fun deleteDevice(device: Device): Completable
}