package com.aconno.sensorics.viewmodel

import com.aconno.sensorics.domain.ifttt.GeneralGooglePublishDeviceJoin
import com.aconno.sensorics.domain.ifttt.PublishDeviceJoin
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.GetGooglePublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.AddAnyPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.DeletePublishDeviceJoinUseCase
import com.aconno.sensorics.domain.interactor.repository.SavePublishDeviceJoinUseCase
import com.aconno.sensorics.model.GooglePublishModel
import com.aconno.sensorics.model.mapper.GooglePublishDataMapper
import com.aconno.sensorics.model.mapper.GooglePublishModelDataMapper
import io.reactivex.Maybe
import io.reactivex.Single

class GoogleCloudPublisherViewModel(
    private val addAnyPublishUseCase: AddAnyPublishUseCase,
    private val getGooglePublishByIdUseCase: GetGooglePublishByIdUseCase,
    private val googlePublishModelDataMapper: GooglePublishModelDataMapper,
    savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
    private val googlePublishDataMapper: GooglePublishDataMapper
) : PublisherViewModel<GooglePublishModel>(
    savePublishDeviceJoinUseCase, deletePublishDeviceJoinUseCase
) {
    override fun getById(id: Long): Maybe<GooglePublishModel> {
        return getGooglePublishByIdUseCase.execute(id).map {
            googlePublishDataMapper.transform(it)
        }
    }

    override fun save(
        model: GooglePublishModel
    ): Single<Long> {
        val transform = googlePublishModelDataMapper.transform(model)
        return addAnyPublishUseCase.execute(transform)
    }

    override fun createPublishDeviceJoin(deviceId: String, publishId: Long): PublishDeviceJoin {
        return GeneralGooglePublishDeviceJoin(
            publishId,
            deviceId
        )
    }
}