package com.aconno.acnsensa.data.repository

import android.arch.persistence.room.*
import io.reactivex.Single

/**
 * @author aconno
 */
@Dao
abstract class ActionDao {

    @get:Query("SELECT * FROM actions")
    abstract val all: Single<List<ActionEntity>>

    @Query("SELECT * FROM actions WHERE id = :actionId")
    abstract fun getActionById(actionId: Long): Single<ActionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(action: ActionEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(action: ActionEntity)

    @Delete
    abstract fun delete(action: ActionEntity)
}