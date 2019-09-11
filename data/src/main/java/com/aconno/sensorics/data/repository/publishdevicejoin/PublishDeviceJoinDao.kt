package com.aconno.sensorics.data.repository.publishdevicejoin

import androidx.room.*
import com.aconno.sensorics.data.repository.devices.DeviceEntity
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

    @Query(
        "SELECT * FROM devices WHERE macAddress IN (SELECT dId FROM mqtt_publish_device_join WHERE mId = :mqttPublishId)"
    )
    abstract fun getDevicesThatConnectedWithMqttPublish(mqttPublishId: Long): Maybe<List<DeviceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertGoogle(googlePublishDeviceJoinEntity: GooglePublishDeviceJoinEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRest(restPublishDeviceJoinEntity: RestPublishDeviceJoinEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMqtt(mqttPublishDeviceJoinEntity: MqttPublishDeviceJoinEntity): Long

    @Delete
    abstract fun delete(googlePublishDeviceJoinEntity: GooglePublishDeviceJoinEntity)

    @Delete
    abstract fun delete(restPublishDeviceJoinEntity: RestPublishDeviceJoinEntity)

    @Delete
    abstract fun delete(mqttPublishDeviceJoinEntity: MqttPublishDeviceJoinEntity)
}

