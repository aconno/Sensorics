package com.aconno.sensorics.data.repository.mpublish

import android.arch.persistence.room.*
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
abstract class MqttPublishDao {
    @get:Query("SELECT * FROM mqtt_publish")
    abstract val all: Single<List<MqttPublishEntity>>

    @Query("SELECT * FROM mqtt_publish WHERE id = :mqttPublishId")
    abstract fun getMqttPublishById(mqttPublishId: Long): Maybe<MqttPublishEntity>

    @Query("SELECT * FROM mqtt_publish WHERE enabled = 1")
    abstract fun getEnabledMqttPublish(): Single<List<MqttPublishEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(mqttPublishEntity: MqttPublishEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(mqttPublishEntity: MqttPublishEntity)

    @Delete
    abstract fun delete(mqttPublishEntity: MqttPublishEntity)
}