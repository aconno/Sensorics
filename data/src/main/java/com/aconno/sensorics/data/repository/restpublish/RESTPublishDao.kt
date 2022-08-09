package com.aconno.sensorics.data.repository.restpublish

import androidx.room.*
import com.aconno.sensorics.data.repository.PublishDao
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
abstract class RESTPublishDao : PublishDao<RestPublishEntity> {

    @get:Query("SELECT * FROM rest_publish")
    abstract override val all: Single<List<RestPublishEntity>>

    @get:Query("SELECT * FROM rest_publish WHERE enabled = 1")
    abstract override val allEnabled: Single<List<RestPublishEntity>>

    @Query("SELECT * FROM rest_publish WHERE id = :id")
    abstract override fun getById(id: Long): Maybe<RestPublishEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract override fun insert(entity: RestPublishEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract override fun update(entity: RestPublishEntity)

    @Delete
    abstract override fun delete(entity: RestPublishEntity)

    // Headers
    @Query("SELECT * FROM rest_headers WHERE rId = :restPublishId")
    abstract fun getHeadersByRESTPublishId(restPublishId: Long): Maybe<List<RestHeaderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertHeaders(restHeaderEntity: List<RestHeaderEntity>)

    @Delete
    abstract fun delete(restHeaderEntity: RestHeaderEntity)

    // HttpGetParams
    @Query("SELECT * FROM rest_http_params WHERE rId = :restPublishId")
    abstract fun getRESTHttpGetParamsByRESTPublishId(restPublishId: Long): Maybe<List<RestHttpGetParamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertHttpGetParams(restHeaderEntity: List<RestHttpGetParamEntity>)

    @Delete
    abstract fun delete(restHttpGetParamEntity: RestHttpGetParamEntity)
}