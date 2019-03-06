package com.aconno.sensorics.data.repository.logs

import android.arch.persistence.room.*
import io.reactivex.Single

@Dao
interface LogDao {

    @Query("SELECT * FROM logs WHERE deviceMacAddress = :deviceMacAddress")
    fun getLogsForDevice(deviceMacAddress: String): Single<List<LogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(log: LogEntity): Long

    @Query("DELETE FROM logs WHERE deviceMacAddress = :deviceMacAddress")
    fun deleteAllDeviceLogs(deviceMacAddress: String)
}