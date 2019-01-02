package com.aconno.sensorics.data.repository

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.aconno.sensorics.data.repository.action.ActionDao
import com.aconno.sensorics.data.repository.action.ActionEntity
import com.aconno.sensorics.data.repository.devices.DeviceDao
import com.aconno.sensorics.data.repository.devices.DeviceEntity
import com.aconno.sensorics.data.repository.googlepublish.GooglePublishDao
import com.aconno.sensorics.data.repository.googlepublish.GooglePublishEntity
import com.aconno.sensorics.data.repository.mqttpublish.MqttPublishDao
import com.aconno.sensorics.data.repository.mqttpublish.MqttPublishEntity
import com.aconno.sensorics.data.repository.publishdevicejoin.GooglePublishDeviceJoinEntity
import com.aconno.sensorics.data.repository.publishdevicejoin.MqttPublishDeviceJoinEntity
import com.aconno.sensorics.data.repository.publishdevicejoin.PublishDeviceJoinDao
import com.aconno.sensorics.data.repository.publishdevicejoin.RestPublishDeviceJoinEntity
import com.aconno.sensorics.data.repository.restpublish.RESTPublishDao
import com.aconno.sensorics.data.repository.restpublish.RestHeaderEntity
import com.aconno.sensorics.data.repository.restpublish.RestHttpGetParamEntity
import com.aconno.sensorics.data.repository.restpublish.RestPublishEntity
import com.aconno.sensorics.data.repository.sync.SyncDao
import com.aconno.sensorics.data.repository.sync.SyncEntity

@Database(
    entities = [
        ActionEntity::class,
        DeviceEntity::class,
        GooglePublishDeviceJoinEntity::class,
        GooglePublishEntity::class,
        MqttPublishDeviceJoinEntity::class,
        MqttPublishEntity::class,
        RestHeaderEntity::class,
        RestHttpGetParamEntity::class,
        RestPublishDeviceJoinEntity::class,
        RestPublishEntity::class,
        SyncEntity::class
    ],
    version = 9
)
abstract class SensoricsDatabase : RoomDatabase() {

    abstract fun actionDao(): ActionDao

    abstract fun deviceDao(): DeviceDao

    abstract fun googlePublishDao(): GooglePublishDao

    abstract fun mqttPublishDao(): MqttPublishDao

    abstract fun publishDeviceJoinDao(): PublishDeviceJoinDao

    abstract fun restPublishDao(): RESTPublishDao

    abstract fun syncDao(): SyncDao
}