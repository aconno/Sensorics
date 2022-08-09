package com.aconno.sensorics.data.repository.sync

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
abstract class SyncDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(sync: SyncEntity)

    @Query("SELECT * FROM sync WHERE publisherUniqueId = :uniqueId")
    abstract fun getByUniqueId(uniqueId: String): List<SyncEntity>
}