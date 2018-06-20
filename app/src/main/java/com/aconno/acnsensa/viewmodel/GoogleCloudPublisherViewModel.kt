package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.ifttt.GeneralGooglePublishDeviceJoin
import com.aconno.acnsensa.domain.interactor.ifttt.gpublish.AddGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.DeletePublishDeviceJoinUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetDevicesThatConnectedWithGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetSavedDevicesMaybeUseCase
import com.aconno.acnsensa.domain.interactor.repository.SavePublishDeviceJoinUseCase
import com.aconno.acnsensa.model.*
import com.aconno.acnsensa.model.mapper.DeviceRelationModelMapper
import com.aconno.acnsensa.model.mapper.GooglePublishModelDataMapper
import io.reactivex.Completable
import io.reactivex.Single

class GoogleCloudPublisherViewModel(
    private val addGooglePublishUseCase: AddGooglePublishUseCase,
    private val googlePublishModelDataMapper: GooglePublishModelDataMapper,
    private val savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    private val deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
    private val devicesThatConnectedWithGooglePublishUseCase: GetDevicesThatConnectedWithGooglePublishUseCase,
    private val savedDevicesMaybeUseCase: GetSavedDevicesMaybeUseCase,
    private val deviceRelationModelMapper: DeviceRelationModelMapper
) : ViewModel() {

    fun save(
        googlePublishModel: GooglePublishModel
    ): Single<Long> {

        val transform = googlePublishModelDataMapper.transform(googlePublishModel)
        return addGooglePublishUseCase.execute(transform)
    }

    fun addOrUpdateGoogleRelation(
        deviceId: String,
        googleId: Long
    ): Completable {
        return savePublishDeviceJoinUseCase.execute(
            GeneralGooglePublishDeviceJoin(
                googleId,
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

    public fun getDevicesThatConnectedWithGooglePublish(googleId: Long): Single<MutableList<DeviceRelationModel>> {
        return devicesThatConnectedWithGooglePublishUseCase.execute(googleId)
            .toFlowable()
            .flatMapIterable { it }
            .map {
                deviceRelationModelMapper.toDeviceRelationModel(it, true)
            }.toList()
    }

    fun deleteRelationGoogle(
        deviceId: String,
        googleId: Long
    ): Completable {
        return deletePublishDeviceJoinUseCase.execute(
            GeneralGooglePublishDeviceJoin(
                googleId,
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