package com.aconno.sensorics.data.repository.format

import android.arch.persistence.room.*
import io.reactivex.Single

@Dao
interface FormatDao {

    @Query("SELECT contentJson FROM formats")
    fun getAllFormats(): Single<List<String>>

    @Query("SELECT timestamp FROM formats WHERE id = :deviceId")
    fun getTimestamp(deviceId: String): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(formatEntity: FormatEntity)

    @Delete
    fun delete(formatEntity: FormatEntity)
}