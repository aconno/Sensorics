package com.aconno.sensorics.viewmodel

import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.ifttt.GeneralMqttPublishDeviceJoin
import com.aconno.sensorics.domain.interactor.ifttt.mpublish.AddMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.DeletePublishDeviceJoinUseCase
import com.aconno.sensorics.domain.interactor.repository.GetDevicesThatConnectedWithMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.GetSavedDevicesMaybeUseCase
import com.aconno.sensorics.domain.interactor.repository.SavePublishDeviceJoinUseCase
import com.aconno.sensorics.model.DeviceRelationModel
import com.aconno.sensorics.model.MqttPublishModel
import com.aconno.sensorics.model.mapper.DeviceRelationModelMapper
import com.aconno.sensorics.model.mapper.MqttPublishModelDataMapper
import io.reactivex.Completable
import io.reactivex.Single

class MqttPublisherViewModel(
    private val savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    private val deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
    private val savedDevicesMaybeUseCase: GetSavedDevicesMaybeUseCase,
    private val deviceRelationModelMapper: DeviceRelationModelMapper,
    private val addMqttPublishUseCase: AddMqttPublishUseCase,
    private val mqttPublishModelDataMapper: MqttPublishModelDataMapper,
    private val devicesThatConnectedWithMqttPublishUseCase: GetDevicesThatConnectedWithMqttPublishUseCase
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

    fun getAllDevices(): Single<MutableList<DeviceRelationModel>> {
        return savedDevicesMaybeUseCase.execute()
            .toFlowable()
            .flatMapIterable { it }
            .map {
                deviceRelationModelMapper.toDeviceRelationModel(it)
            }.toList()
    }

    fun getDevicesThatConnectedWithMqttPublish(mqttId: Long): Single<MutableList<DeviceRelationModel>> {
        return devicesThatConnectedWithMqttPublishUseCase.execute(mqttId)
            .toFlowable()
            .flatMapIterable { it }
            .map {
                deviceRelationModelMapper.toDeviceRelationModel(it, true)
            }.toList()
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