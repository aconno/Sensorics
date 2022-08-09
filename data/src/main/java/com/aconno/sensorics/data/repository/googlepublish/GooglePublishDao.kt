package com.aconno.sensorics.data.repository.googlepublish

import androidx.room.*
import com.aconno.sensorics.data.repository.PublishDao
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
abstract class GooglePublishDao : PublishDao<GooglePublishEntity> {
    @get:Query("SELECT * FROM google_publish")
    abstract override val all: Single<List<GooglePublishEntity>>

    @get:Query("SELECT * FROM google_publish WHERE enabled = 1")
    abstract override val allEnabled: Single<List<GooglePublishEntity>>

    @Query("SELECT * FROM google_publish WHERE id = :id")
    abstract override fun getById(id: Long): Maybe<GooglePublishEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract override fun insert(entity: GooglePublishEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract override fun update(entity: GooglePublishEntity)

    @Delete
    abstract override fun delete(entity: GooglePublishEntity)
}