package com.aconno.sensorics.data.repository.mqttvirtualscanningsource

import androidx.room.*
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
abstract class MqttVirtualScanningSourceDao {
    @get:Query("SELECT * FROM mqtt_virtual_scanning_source")
    abstract val all: Single<List<MqttVirtualScanningSourceEntity>>

    @Query("SELECT * FROM mqtt_virtual_scanning_source WHERE id = :mqttVirtualScanningSourceId")
    abstract fun getMqttVirtualScanningSourceById(mqttVirtualScanningSourceId: Long): Maybe<MqttVirtualScanningSourceEntity>

    @Query("SELECT * FROM mqtt_virtual_scanning_source WHERE enabled = 1")
    abstract fun getEnabledMqttVirtualScanningSource(): List<MqttVirtualScanningSourceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(mqttVirtualScanningSourceEntity: MqttVirtualScanningSourceEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(mqttVirtualScanningSourceEntity: MqttVirtualScanningSourceEntity)

    @Delete
    abstract fun delete(mqttVirtualScanningSourceEntity: MqttVirtualScanningSourceEntity)
}