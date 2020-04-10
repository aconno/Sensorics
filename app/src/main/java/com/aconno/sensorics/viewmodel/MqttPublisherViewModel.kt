package com.aconno.sensorics.viewmodel

import com.aconno.sensorics.domain.ifttt.GeneralMqttPublishDeviceJoin
import com.aconno.sensorics.domain.ifttt.PublishDeviceJoin
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.GetMqttPublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.AddAnyPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.DeletePublishDeviceJoinUseCase
import com.aconno.sensorics.domain.interactor.repository.SavePublishDeviceJoinUseCase
import com.aconno.sensorics.model.MqttPublishModel
import com.aconno.sensorics.model.mapper.MqttPublishModelDataMapper
import io.reactivex.Maybe
import io.reactivex.Single

class MqttPublisherViewModel(
    savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
    private val addAnyPublishUseCase: AddAnyPublishUseCase,
    private val getMqttPublishByIdUseCase: GetMqttPublishByIdUseCase,
    private val mqttPublishModelDataMapper: MqttPublishModelDataMapper
) : PublisherViewModel<MqttPublishModel>(
    savePublishDeviceJoinUseCase, deletePublishDeviceJoinUseCase
) {
    override fun getById(id: Long): Maybe<MqttPublishModel> {
        return getMqttPublishByIdUseCase.execute(id).map {
            mqttPublishModelDataMapper.toMqttPublishModel(it)
        }
    }

    override fun save(
        model: MqttPublishModel
    ): Single<Long> {
        val transform = mqttPublishModelDataMapper.toMqttPublish(model)
        return addAnyPublishUseCase.execute(transform)
    }

    override fun createPublishDeviceJoin(deviceId: String, publishId: Long): PublishDeviceJoin {
        return GeneralMqttPublishDeviceJoin(
            publishId,
            deviceId
        )
    }
}