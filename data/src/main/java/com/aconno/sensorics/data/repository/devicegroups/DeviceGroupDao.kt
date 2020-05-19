package com.aconno.sensorics.data.repository.devicegroups

import androidx.room.*
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
abstract class DeviceGroupDao {

    @Query("SELECT * FROM device_groups")
    abstract fun getAll(): Single<List<DeviceGroupEntity>>

    @Query("SELECT * FROM device_groups join device_group_device_join on device_groups.id = device_group_device_join.deviceGroupId and device_group_device_join.deviceId = :deviceMacAddress")
    abstract fun getDeviceGroupForDevice(deviceMacAddress : String): Maybe<DeviceGroupEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(deviceGroup: DeviceGroupEntity) : Long

    @Update
    abstract fun update(deviceGroup: DeviceGroupEntity)

    @Delete
    abstract fun delete(deviceGroup: DeviceGroupEntity)
}