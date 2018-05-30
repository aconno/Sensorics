package com.aconno.acnsensa.data.repository

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

/**
 * @author aconno
 */
@Database(
    entities = [ActionEntity::class, GooglePublishEntity::class, RESTPublishEntity::class],
    version = 1
)
abstract class AcnSensaDatabase : RoomDatabase() {
    abstract fun actionDao(): ActionDao
    abstract fun googlePublishDao(): GooglePublishDao
    abstract fun restPublishDao(): RESTPublishDao
}