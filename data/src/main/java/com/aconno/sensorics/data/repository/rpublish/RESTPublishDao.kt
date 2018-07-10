package com.aconno.sensorics.data.repository.rpublish

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

    @Query("SELECT * FROM rest_headers WHERE rId = :restPublishId")
    abstract fun getHeadersByRESTPublishId(restPublishId: Long): Maybe<List<RESTHeaderEntity>>

    @Query("SELECT * FROM rest_http_params WHERE rId = :restPublishId")
    abstract fun getRESTHttpGetParamsByRESTPublishId(restPublishId: Long): Maybe<List<RESTHttpGetParamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(restPublishEntity: RESTPublishEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertHeaders(restHeaderEntity: List<RESTHeaderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertHttpGetParams(restHeaderEntity: List<RESTHttpGetParamEntity>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(restPublishEntity: RESTPublishEntity)

    @Delete
    abstract fun delete(restPublishEntity: RESTPublishEntity)

    @Delete
    abstract fun delete(restHeaderEntity: RESTHeaderEntity)

    @Delete
    abstract fun delete(restHttpGetParamEntity: RESTHttpGetParamEntity)
}