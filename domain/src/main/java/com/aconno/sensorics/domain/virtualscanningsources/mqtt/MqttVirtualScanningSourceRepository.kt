package com.aconno.sensorics.domain.virtualscanningsources.mqtt

import com.aconno.sensorics.domain.virtualscanningsources.BaseVirtualScanningSource
import io.reactivex.Maybe
import io.reactivex.Single

interface MqttVirtualScanningSourceRepository {
    fun addMqttVirtualScanningSource(mqttVirtualScanningSource : MqttVirtualScanningSource): Long
    fun updateMqttVirtualScanningSource(mqttVirtualScanningSource: MqttVirtualScanningSource)
    fun deleteMqttVirtualScanningSource(mqttVirtualScanningSource: MqttVirtualScanningSource)
    fun getAllMqttVirtualScanningSource(): Single<List<BaseVirtualScanningSource>>
    fun getAllEnabledMqttVirtualScanningSource(): List<MqttVirtualScanningSource>
    fun getMqttVirtualScanningSourceById(mqttVirtualScanningSourceId: Long): Maybe<MqttVirtualScanningSource>
}