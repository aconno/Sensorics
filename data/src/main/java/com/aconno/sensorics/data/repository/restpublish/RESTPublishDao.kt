package com.aconno.sensorics.data.repository.restpublish

import android.arch.persistence.room.*
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
abstract class RESTPublishDao {

    @get:Query("SELECT * FROM rest_publish")
    abstract val all: Single<List<RestPublishEntity>>

    @Query("SELECT * FROM rest_publish WHERE id = :restPublishId")
    abstract fun getRESTPublishById(restPublishId: Long): Maybe<RestPublishEntity>

    @Query("SELECT * FROM rest_publish WHERE enabled = 1")
    abstract fun getEnabledRESTPublish(): Single<List<RestPublishEntity>>

    @Query("SELECT * FROM rest_headers WHERE rId = :restPublishId")
    abstract fun getHeadersByRESTPublishId(restPublishId: Long): Maybe<List<RestHeaderEntity>>

    @Query("SELECT * FROM rest_http_params WHERE rId = :restPublishId")
    abstract fun getRESTHttpGetParamsByRESTPublishId(restPublishId: Long): Maybe<List<RestHttpGetParamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(restPublishEntity: RestPublishEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertHeaders(restHeaderEntity: List<RestHeaderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertHttpGetParams(restHeaderEntity: List<RestHttpGetParamEntity>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(restPublishEntity: RestPublishEntity)

    @Delete
    abstract fun delete(restPublishEntity: RestPublishEntity)

    @Delete
    abstract fun delete(restHeaderEntity: RestHeaderEntity)

    @Delete
    abstract fun delete(restHttpGetParamEntity: RestHttpGetParamEntity)
}