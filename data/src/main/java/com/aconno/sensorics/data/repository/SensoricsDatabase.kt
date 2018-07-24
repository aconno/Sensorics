package com.aconno.sensorics.data.repository

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.aconno.sensorics.data.repository.action.ActionDao
import com.aconno.sensorics.data.repository.action.ActionEntity
import com.aconno.sensorics.data.repository.devices.DeviceDao
import com.aconno.sensorics.data.repository.devices.DeviceEntity
import com.aconno.sensorics.data.repository.gpublish.GooglePublishDao
import com.aconno.sensorics.data.repository.gpublish.GooglePublishEntity
import com.aconno.sensorics.data.repository.mpublish.MqttPublishDao
import com.aconno.sensorics.data.repository.mpublish.MqttPublishEntity
import com.aconno.sensorics.data.repository.pdjoin.GooglePublishDeviceJoinEntity
import com.aconno.sensorics.data.repository.pdjoin.MqttPublishDeviceJoinEntity
import com.aconno.sensorics.data.repository.pdjoin.PublishDeviceJoinDao
import com.aconno.sensorics.data.repository.pdjoin.RestPublishDeviceJoinEntity
import com.aconno.sensorics.data.repository.rpublish.RESTHeaderEntity
import com.aconno.sensorics.data.repository.rpublish.RESTHttpGetParamEntity
import com.aconno.sensorics.data.repository.rpublish.RESTPublishDao
import com.aconno.sensorics.data.repository.rpublish.RESTPublishEntity

@Database(
    entities = [ActionEntity::class, DeviceEntity::class, GooglePublishEntity::class, RESTPublishEntity::class, GooglePublishDeviceJoinEntity::class, RestPublishDeviceJoinEntity::class, RESTHeaderEntity::class, MqttPublishEntity::class, MqttPublishDeviceJoinEntity::class, RESTHttpGetParamEntity::class],
    version = 3
)
abstract class SensoricsDatabase : RoomDatabase() {

    abstract fun mqttPublishDao(): MqttPublishDao

    abstract fun publishDeviceJoinDao(): PublishDeviceJoinDao

    abstract fun deviceDao(): DeviceDao

    abstract fun actionDao(): ActionDao

    abstract fun googlePublishDao(): GooglePublishDao

    abstract fun restPublishDao(): RESTPublishDao
}