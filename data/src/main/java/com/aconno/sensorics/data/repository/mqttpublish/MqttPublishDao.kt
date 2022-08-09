package com.aconno.sensorics.data.repository.mqttpublish

import androidx.room.*
import com.aconno.sensorics.data.repository.PublishDao
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
abstract class MqttPublishDao : PublishDao<MqttPublishEntity> {
    @get:Query("SELECT * FROM mqtt_publish")
    abstract override val all: Single<List<MqttPublishEntity>>

    @get:Query("SELECT * FROM mqtt_publish WHERE enabled = 1")
    abstract override val allEnabled: Single<List<MqttPublishEntity>>

    @Query("SELECT * FROM mqtt_publish WHERE id = :id")
    abstract override fun getById(id: Long): Maybe<MqttPublishEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract override fun insert(entity: MqttPublishEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract override fun update(entity: MqttPublishEntity)

    @Delete
    abstract override fun delete(entity: MqttPublishEntity)
}