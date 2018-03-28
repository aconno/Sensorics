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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(action: ActionEntity)

    @Delete
    abstract fun delete(action: ActionEntity)
}