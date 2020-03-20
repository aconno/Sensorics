package com.aconno.sensorics.viewmodel

import com.aconno.sensorics.domain.ifttt.GeneralAzureMqttPublishDeviceJoin
import com.aconno.sensorics.domain.ifttt.PublishDeviceJoin
import com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish.GetAzureMqttPublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.AddAnyPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.DeletePublishDeviceJoinUseCase
import com.aconno.sensorics.domain.interactor.repository.SavePublishDeviceJoinUseCase
import com.aconno.sensorics.model.AzureMqttPublishModel
import com.aconno.sensorics.model.mapper.AzureMqttPublishModelDataMapper
import io.reactivex.Maybe
import io.reactivex.Single

class AzureMqttPublisherViewModel(
    private val addAnyPublishUseCase: AddAnyPublishUseCase,
    private val getAzureMqttPublishByIdUseCase: GetAzureMqttPublishByIdUseCase,
    savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
    private val azureMqttPublishModelDataMapper: AzureMqttPublishModelDataMapper
) : PublisherViewModel<AzureMqttPublishModel>(
    savePublishDeviceJoinUseCase, deletePublishDeviceJoinUseCase
) {
    override fun getById(id: Long): Maybe<AzureMqttPublishModel> {
        return getAzureMqttPublishByIdUseCase.execute(id).map {
            azureMqttPublishModelDataMapper.toAzureMqttPublishModel(it)
        }
    }

    override fun save(
        model: AzureMqttPublishModel
    ): Single<Long> {
        val transform = azureMqttPublishModelDataMapper.toAzureMqttPublish(model)
        return addAnyPublishUseCase.execute(transform)
    }

    override fun createPublishDeviceJoin(deviceId: String, publishId: Long): PublishDeviceJoin {
        return GeneralAzureMqttPublishDeviceJoin(
            publishId,
            deviceId
        )
    }
}