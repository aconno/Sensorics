package com.aconno.acnsensa.data.repository

import android.arch.persistence.room.*
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
abstract class RESTPublishDao {

    @get:Query("SELECT * FROM rest_publish")
    abstract val all: Single<List<RESTPublishEntity>>

    @Query("SELECT * FROM rest_publish WHERE id = :restPublishId")
    abstract fun getRESTPublishById(restPublishId: Long): Maybe<RESTPublishEntity>

    @Query("SELECT * FROM rest_publish WHERE enabled = 1")
    abstract fun getEnabledRESTPublish(): Single<List<RESTPublishEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(restPublishEntity: RESTPublishEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(restPublishEntity: RESTPublishEntity)

    @Delete
    abstract fun delete(restPublishEntity: RESTPublishEntity)
}