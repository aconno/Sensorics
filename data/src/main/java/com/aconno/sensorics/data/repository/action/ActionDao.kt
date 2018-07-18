package com.aconno.sensorics.data.repository.action

import android.arch.persistence.room.*
import io.reactivex.Single

@Dao
interface ActionDao {

    @Query("SELECT * FROM actions")
    fun getAll(): Single<List<ActionEntity>>

    @Query("SELECT * FROM actions WHERE id = :actionId")
    fun getActionById(actionId: Long): Single<ActionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(action: ActionEntity)

    @Delete
    fun delete(action: ActionEntity)
}