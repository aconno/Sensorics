package com.aconno.acnsensa.data.repository

import android.arch.persistence.room.*
import com.aconno.acnsensa.data.repository.devices.DeviceEntity
import io.reactivex.Maybe

@Dao
abstract class PublishDeviceJoinDao {
    @Query(
        "SELECT * FROM devices WHERE macAddress IN (SELECT dId FROM google_publish_device_join WHERE gId = :googlePublishId)"
    )
    abstract fun getDevicesThatConnectedWithGooglePublish(googlePublishId: Long): Maybe<List<DeviceEntity>>

    @Query(
        "SELECT * FROM devices WHERE macAddress IN (SELECT dId FROM rest_publish_device_join WHERE rId = :restPublishId)"
    )
    abstract fun getDevicesThatConnectedWithRestPublish(restPublishId: Long): Maybe<List<DeviceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertGoogle(googlePublishDeviceJoinEntity: GooglePublishDeviceJoinEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRest(restPublishDeviceJoinEntity: RestPublishDeviceJoinEntity): Long

    @Delete
    abstract fun delete(googlePublishDeviceJoinEntity: GooglePublishDeviceJoinEntity)

    @Delete
    abstract fun delete(restPublishDeviceJoinEntity: RestPublishDeviceJoinEntity)
}

