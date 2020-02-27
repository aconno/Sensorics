package com.aconno.sensorics.data.repository.azuremqttpublish

import androidx.room.*
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
abstract class AzureMqttPublishDao {
    @get:Query("SELECT * FROM azure_mqtt_publish")
    abstract val all: Single<List<AzureMqttPublishEntity>>

    @Query("SELECT * FROM azure_mqtt_publish WHERE id = :azureMqttPublishId")
    abstract fun getAzureMqttPublishById(azureMqttPublishId: Long): Maybe<AzureMqttPublishEntity>

    @Query("SELECT * FROM azure_mqtt_publish WHERE enabled = 1")
    abstract fun getEnabledAzureMqttPublish(): Single<List<AzureMqttPublishEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(azureMqttPublishEntity: AzureMqttPublishEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(azureMqttPublishEntity: AzureMqttPublishEntity)

    @Delete
    abstract fun delete(azureMqttPublishEntity: AzureMqttPublishEntity)
}