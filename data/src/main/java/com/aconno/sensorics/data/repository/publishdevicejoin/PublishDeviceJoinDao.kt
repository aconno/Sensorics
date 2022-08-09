package com.aconno.sensorics.data.repository.publishdevicejoin

import androidx.room.*
import com.aconno.sensorics.data.repository.devices.DeviceEntity
import io.reactivex.Maybe

@Dao
abstract class PublishDeviceJoinDao {
    @Query(
        "SELECT * FROM devices WHERE macAddress IN (SELECT deviceId FROM publish_device_join WHERE publishId = :publishId AND publishType= :publishType)"
    )
    abstract fun getDevicesConnectedWithPublish(publishId: Long, publishType: String): Maybe<List<DeviceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(genericPublishDeviceJoinEntity: GenericPublishDeviceJoinEntity): Long

    @Delete
    abstract fun delete(genericPublishDeviceJoinEntity: GenericPublishDeviceJoinEntity)
}

