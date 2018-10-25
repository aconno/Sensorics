package com.aconno.sensorics.viewmodel

import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.ifttt.GeneralMqttPublishDeviceJoin
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.AddMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.DeletePublishDeviceJoinUseCase
import com.aconno.sensorics.domain.interactor.repository.SavePublishDeviceJoinUseCase
import com.aconno.sensorics.model.MqttPublishModel
import com.aconno.sensorics.model.mapper.MqttPublishModelDataMapper
import io.reactivex.Completable
import io.reactivex.Single

class MqttPublisherViewModel(
    private val savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    private val deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
    private val addMqttPublishUseCase: AddMqttPublishUseCase,
    private val mqttPublishModelDataMapper: MqttPublishModelDataMapper
) : ViewModel() {

    fun save(
        mqttPublishModel: MqttPublishModel
    ): Single<Long> {

        val transform = mqttPublishModelDataMapper.toMqttPublish(mqttPublishModel)
        return addMqttPublishUseCase.execute(transform)
    }

    fun addOrUpdateMqttRelation(
        deviceId: String,
        mqttId: Long
    ): Completable {
        return savePublishDeviceJoinUseCase.execute(
            GeneralMqttPublishDeviceJoin(
                mqttId,
                deviceId
            )
        )
    }

    fun deleteRelationMqtt(
        deviceId: String,
        mqttId: Long
    ): Completable {
        return deletePublishDeviceJoinUseCase.execute(
            GeneralMqttPublishDeviceJoin(
                mqttId,
                deviceId
            )
        )
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