package com.aconno.sensorics.data.repository.action

import androidx.room.*
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface ActionDao {

    @Query("SELECT * FROM actions")
    fun getAll(): Single<List<ActionEntity>>

    @Query("SELECT * FROM actions")
    fun getAllAsFlowable() : Flowable<List<ActionEntity>>

    @Query("SELECT * FROM actions WHERE id = :actionId")
    fun getActionById(actionId: Long): Single<ActionEntity>

    @Query("SELECT * FROM actions WHERE deviceMacAddress = :macAddress")
    fun getActionsByDeviceMacAddress(macAddress: String): Single<List<ActionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(action: ActionEntity) : Long

    @Delete
    fun delete(action: ActionEntity)
}