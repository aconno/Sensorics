package com.aconno.sensorics.viewmodel

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt.AddMqttVirtualScanningSourceUseCase
import com.aconno.sensorics.model.MqttVirtualScanningSourceModel
import com.aconno.sensorics.model.mapper.MqttVirtualScanningSourceModelDataMapper
import io.reactivex.Single

class MqttVirtualScanningSourceViewModel(
        private val addMqttVirtualScanningSourceUseCase: AddMqttVirtualScanningSourceUseCase,
        private val mqttVirtualScanningSourceModelDataMapper: MqttVirtualScanningSourceModelDataMapper
) : ViewModel() {

    fun save(
            mqttVirtualScanningSourceModel: MqttVirtualScanningSourceModel
    ): Single<Long> {
        val transform = mqttVirtualScanningSourceModelDataMapper.toMqttVirtualScanningSource(mqttVirtualScanningSourceModel)
        return addMqttVirtualScanningSourceUseCase.execute(transform)
    }

    fun checkFieldsAreEmpty(
            vararg strings: String
    ): Boolean {

        strings.forEach {
            if (it.isBlank()) {
                return true
            }
        }

        return false
    }
}