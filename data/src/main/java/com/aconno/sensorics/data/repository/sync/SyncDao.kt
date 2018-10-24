package com.aconno.sensorics.data.repository.sync

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
abstract class SyncDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(sync: SyncEntity)

    @Query("SELECT * FROM sync WHERE publisherUniqueId = :uniqueId")
    abstract fun getByUniqueId(uniqueId: String): List<SyncEntity>
}