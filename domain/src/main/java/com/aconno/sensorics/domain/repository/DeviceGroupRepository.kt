package com.aconno.sensorics.domain.repository

import com.aconno.sensorics.domain.model.DeviceGroup
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

interface DeviceGroupRepository {

    fun getAllDeviceGroups(): Single<List<DeviceGroup>>

    fun insertDeviceGroup(deviceGroup: DeviceGroup): Single<Long>

    fun updateDeviceGroup(deviceGroup: DeviceGroup): Completable

    fun deleteDeviceGroup(deviceGroup: DeviceGroup): Completable

    fun getDeviceGroupForDevice(deviceMacAddress : String): Maybe<DeviceGroup>
}