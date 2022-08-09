package com.aconno.sensorics.viewmodel.publish

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.ifttt.GeneralAzureMqttPublishDeviceJoin
import com.aconno.sensorics.domain.interactor.ifttt.publish.AddAnyPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.DeletePublishDeviceJoinUseCase
import com.aconno.sensorics.domain.interactor.repository.SavePublishDeviceJoinUseCase
import com.aconno.sensorics.model.AzureMqttPublishModel
import com.aconno.sensorics.model.mapper.AzureMqttPublishModelDataMapper
import io.reactivex.Completable
import io.reactivex.Single

class PublisherViewModel(
    private val savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    private val deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
    private val addAnyPublishUseCase: AddAnyPublishUseCase,
    private val azureMqttPublishModelDataMapper: AzureMqttPublishModelDataMapper
) : ViewModel() {

    fun save(
        azureMqttPublishModel: AzureMqttPublishModel
    ): Single<Long> {
        val transform = azureMqttPublishModelDataMapper.toAzureMqttPublish(azureMqttPublishModel)
        return addAnyPublishUseCase.execute(transform)
    }

    fun addOrUpdatePublisherDeviceRelation(
        deviceId: String,
        publisherId: Long
    ): Completable {
        return savePublishDeviceJoinUseCase.execute(
            GeneralAzureMqttPublishDeviceJoin(
                publisherId,
                deviceId
            )
        )
    }

    fun deletePublishDeviceRelation(
        deviceId: String,
        publisherId: Long
    ): Completable {
        return deletePublishDeviceJoinUseCase.execute(
            GeneralAzureMqttPublishDeviceJoin(
                publisherId,
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