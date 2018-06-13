package com.aconno.acnsensa.data.repository

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.aconno.acnsensa.data.repository.action.ActionDao
import com.aconno.acnsensa.data.repository.action.ActionEntity
import com.aconno.acnsensa.data.repository.devices.DeviceDao
import com.aconno.acnsensa.data.repository.devices.DeviceEntity
import com.aconno.acnsensa.data.repository.gpublish.GooglePublishDao
import com.aconno.acnsensa.data.repository.pdjoin.GooglePublishDeviceJoinEntity
import com.aconno.acnsensa.data.repository.gpublish.GooglePublishEntity
import com.aconno.acnsensa.data.repository.pdjoin.PublishDeviceJoinDao
import com.aconno.acnsensa.data.repository.rpublish.RESTPublishDao
import com.aconno.acnsensa.data.repository.rpublish.RESTPublishEntity
import com.aconno.acnsensa.data.repository.pdjoin.RestPublishDeviceJoinEntity
import com.aconno.acnsensa.data.repository.rpublish.RESTHeaderEntity

@Database(
    entities = [ActionEntity::class, DeviceEntity::class, GooglePublishEntity::class, RESTPublishEntity::class, GooglePublishDeviceJoinEntity::class, RestPublishDeviceJoinEntity::class, RESTHeaderEntity::class],
    version = 1
)
abstract class AcnSensaDatabase : RoomDatabase() {

    abstract fun publishDeviceJoinDao(): PublishDeviceJoinDao

    abstract fun deviceDao(): DeviceDao

    abstract fun actionDao(): ActionDao

    abstract fun googlePublishDao(): GooglePublishDao

    abstract fun restPublishDao(): RESTPublishDao
}