package com.aconno.sensorics.data.repository.devicegroups

import androidx.room.*
import io.reactivex.Single

@Dao
abstract class DeviceGroupDao {

    @Query("SELECT * FROM device_groups")
    abstract fun getAll(): Single<List<DeviceGroupEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(deviceGroup: DeviceGroupEntity) : Long

    @Update
    abstract fun update(deviceGroup: DeviceGroupEntity)

    @Delete
    abstract fun delete(deviceGroup: DeviceGroupEntity)
}