package com.aconno.sensorics.data.repository.devices

import androidx.room.*
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
abstract class DeviceDao {

    @Query("SELECT * FROM devices")
    abstract fun getAll(): Flowable<List<DeviceEntity>>

    @Query("SELECT * FROM devices")
    abstract fun getAllMaybe(): Maybe<List<DeviceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(device: DeviceEntity)

    @Update
    abstract fun update(device: DeviceEntity)

    @Delete
    abstract fun delete(device: DeviceEntity)
}