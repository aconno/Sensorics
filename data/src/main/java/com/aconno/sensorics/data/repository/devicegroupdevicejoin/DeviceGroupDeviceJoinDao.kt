package com.aconno.sensorics.data.repository.devicegroupdevicejoin

import androidx.room.*
import com.aconno.sensorics.data.repository.devices.DeviceEntity
import io.reactivex.Maybe

@Dao
abstract class DeviceGroupDeviceJoinDao {

    @Query(
        "SELECT * FROM devices JOIN device_group_device_join ON devices.macAddress = device_group_device_join.deviceId WHERE deviceGroupId = :deviceGroupId"
    )
    abstract fun getDevicesInDeviceGroup(deviceGroupId: Long): Maybe<List<DeviceEntity>>

    @Query(
        "SELECT * FROM devices JOIN device_group_device_join ON devices.macAddress = device_group_device_join.deviceId"
    )
    abstract fun getDevices(): Maybe<List<DeviceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(deviceGroupDeviceJoinEntity: DeviceGroupDeviceJoinEntity): Long

    @Delete
    abstract fun delete(deviceGroupDeviceJoinEntity: DeviceGroupDeviceJoinEntity)
}