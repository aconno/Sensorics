package com.aconno.sensorics.data.repository.format

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Single

@Dao
interface FormatDao {

    @Query("SELECT contentJson FROM formats")
    fun getAllFormats(): Single<List<String>>

    @Query("SELECT id FROM formats")
    fun getAllFormatIds(): Single<List<String>>

    @Query("SELECT timestamp FROM formats WHERE id = :formatId")
    fun getTimestamp(formatId: String): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(formatEntity: FormatEntity)

    @Query("DELETE FROM formats WHERE id = :formatId")
    fun delete(formatId: String)
}