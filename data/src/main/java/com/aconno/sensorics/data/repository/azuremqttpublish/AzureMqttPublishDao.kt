package com.aconno.sensorics.data.repository.azuremqttpublish

import androidx.room.*
import com.aconno.sensorics.data.repository.PublishDao
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
abstract class AzureMqttPublishDao : PublishDao<AzureMqttPublishEntity> {
    @get:Query("SELECT * FROM azure_mqtt_publish")
    abstract override val all: Single<List<AzureMqttPublishEntity>>

    @get:Query("SELECT * FROM azure_mqtt_publish WHERE enabled = 1")
    abstract override val allEnabled: Single<List<AzureMqttPublishEntity>>

    @Query("SELECT * FROM azure_mqtt_publish WHERE id = :id")
    abstract override fun getById(id: Long): Maybe<AzureMqttPublishEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract override fun insert(entity: AzureMqttPublishEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract override fun update(entity: AzureMqttPublishEntity)

    @Delete
    abstract override fun delete(entity: AzureMqttPublishEntity)
}
