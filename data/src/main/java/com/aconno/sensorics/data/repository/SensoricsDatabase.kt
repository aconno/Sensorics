package com.aconno.sensorics.data.repository

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.aconno.sensorics.data.repository.action.ActionDao
import com.aconno.sensorics.data.repository.action.ActionEntity
import com.aconno.sensorics.data.repository.devices.DeviceDao
import com.aconno.sensorics.data.repository.devices.DeviceEntity
import com.aconno.sensorics.data.repository.format.FormatDao
import com.aconno.sensorics.data.repository.format.FormatEntity
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

@Database(
    entities = [
        ActionEntity::class,
        DeviceEntity::class,
        FormatEntity::class,
        GooglePublishDeviceJoinEntity::class,
        GooglePublishEntity::class,
        MqttPublishDeviceJoinEntity::class,
        MqttPublishEntity::class,
        RestHeaderEntity::class,
        RestHttpGetParamEntity::class,
        RestPublishDeviceJoinEntity::class,
        RestPublishEntity::class],
    version = 5
)
abstract class SensoricsDatabase : RoomDatabase() {

    abstract fun actionDao(): ActionDao

    abstract fun deviceDao(): DeviceDao

    abstract fun formatDao(): FormatDao

    abstract fun googlePublishDao(): GooglePublishDao

    abstract fun mqttPublishDao(): MqttPublishDao

    abstract fun publishDeviceJoinDao(): PublishDeviceJoinDao

    abstract fun restPublishDao(): RESTPublishDao
}