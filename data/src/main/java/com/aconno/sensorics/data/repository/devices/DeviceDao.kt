package com.aconno.sensorics.data.repository.devices

import androidx.room.*
import io.reactivex.Flowable

@Dao
abstract class DeviceDao {

    @Query("SELECT * FROM devices")
    abstract fun getAll(): Flowable<List<DeviceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(device: DeviceEntity)

    @Delete
    abstract fun delete(device: DeviceEntity)
}