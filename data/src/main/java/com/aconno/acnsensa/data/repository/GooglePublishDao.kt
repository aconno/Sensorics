package com.aconno.acnsensa.data.repository

import android.arch.persistence.room.*
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
abstract class GooglePublishDao {

    @get:Query("SELECT * FROM google_publish")
    abstract val all: Single<List<GooglePublishEntity>>

    @Query("SELECT * FROM google_publish WHERE id = :googlePublishId")
    abstract fun getGooglePublishById(googlePublishId: Long): Maybe<GooglePublishEntity>

    @Query("SELECT * FROM google_publish WHERE enabled = 1")
    abstract fun getEnabledGooglePublish(): Single<List<GooglePublishEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(googlePublishEntity: GooglePublishEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(googlePublishEntity: GooglePublishEntity)

    @Delete
    abstract fun delete(googlePublishEntity: GooglePublishEntity)
}