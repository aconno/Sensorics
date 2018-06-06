package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.ifttt.GeneralGooglePublishDeviceJoin
import com.aconno.acnsensa.domain.ifttt.GeneralRestPublishDeviceJoin
import com.aconno.acnsensa.domain.interactor.ifttt.AddGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.AddRESTPublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.UpdateGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.UpdateRESTPublishUserCase
import com.aconno.acnsensa.domain.interactor.repository.*
import com.aconno.acnsensa.model.DeviceRelationModel
import com.aconno.acnsensa.model.GooglePublishModel
import com.aconno.acnsensa.model.RESTPublishModel
import com.aconno.acnsensa.model.mapper.DeviceRelationModelMapper
import com.aconno.acnsensa.model.mapper.GooglePublishModelDataMapper
import com.aconno.acnsensa.model.mapper.RESTPublishModelDataMapper
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class PublishViewModel(
    private val addGooglePublishUseCase: AddGooglePublishUseCase,
    private val addRESTPublishUseCase: AddRESTPublishUseCase,
    private val updateGooglePublishUseCase: UpdateGooglePublishUseCase,
    private val updateRESTPublishUserCase: UpdateRESTPublishUserCase,
    private val googlePublishModelDataMapper: GooglePublishModelDataMapper,
    private val restPublishModelDataMapper: RESTPublishModelDataMapper,
    private val savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    private val deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
    private val devicesThatConnectedWithGooglePublishUseCase: GetDevicesThatConnectedWithGooglePublishUseCase,
    private val devicesThatConnectedWithRESTPublishUseCase: GetDevicesThatConnectedWithRESTPublishUseCase,
    private val savedDevicesMaybeUseCase: GetSavedDevicesMaybeUseCase,
    private val deviceRelationModelMapper: DeviceRelationModelMapper
) : ViewModel() {

    fun save(
        googlePublishModel: GooglePublishModel
    ): Single<Long> {

        val transform = googlePublishModelDataMapper.transform(googlePublishModel)
        return addGooglePublishUseCase.execute(transform)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun save(
        restPublishModel: RESTPublishModel
    ): Single<Long> {

        val transform = restPublishModelDataMapper.transform(restPublishModel)
        return addRESTPublishUseCase.execute(transform)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun update(
        googlePublishModel: GooglePublishModel
    ) {
        val transform = googlePublishModelDataMapper.transform(googlePublishModel)
        updateGooglePublishUseCase.execute(transform)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { Timber.d("Save succeeded, action id: ${transform.id}") },
                { Timber.e("Failed to update Google Publish Data with id: ${transform.id}") })
    }

    fun update(
        restPublishModel: RESTPublishModel
    ) {
        val transform = restPublishModelDataMapper.transform(restPublishModel)

        updateRESTPublishUserCase.execute(transform)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { Timber.d("Save succeeded, action id: ${transform.id}") },
                { Timber.e("Failed to update REST Publish Data with id: ${transform.id}") })
    }

    fun addOrUpdateRestRelation(
        deviceId: String,
        restId: Long
    ) {
        savePublishDeviceJoinUseCase.execute(
            GeneralRestPublishDeviceJoin(
                restId,
                deviceId
            )
        ).subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun addOrUpdateGoogleRelation(
        deviceId: String,
        googleId: Long
    ) {
        savePublishDeviceJoinUseCase.execute(
            GeneralGooglePublishDeviceJoin(
                googleId,
                deviceId
            )
        ).subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun getAllDevices(): Single<MutableList<DeviceRelationModel>> {
        return savedDevicesMaybeUseCase.execute()
            .toFlowable()
            .flatMapIterable { it }
            .map {
                deviceRelationModelMapper.toDeviceRelationModel(it)
            }.toList()
    }

    fun getDevicesThatConnectedWithGooglePublish(googleId: Long): Single<MutableList<DeviceRelationModel>> {
        return devicesThatConnectedWithGooglePublishUseCase.execute(googleId)
            .toFlowable()
            .flatMapIterable { it }
            .map {
                deviceRelationModelMapper.toDeviceRelationModel(it, true)
            }.toList()
    }

    fun getDevicesThatConnectedWithRESTPublish(restId: Long): Single<MutableList<DeviceRelationModel>> {
        return devicesThatConnectedWithRESTPublishUseCase.execute(restId)
            .toFlowable()
            .flatMapIterable { it }
            .map {
                deviceRelationModelMapper.toDeviceRelationModel(it, true)
            }.toList()
    }

    fun deleteRelationGoogle(
        deviceId: String,
        googleId: Long
    ) {
        deletePublishDeviceJoinUseCase.execute(
            GeneralGooglePublishDeviceJoin(
                googleId,
                deviceId
            )
        )
    }

    fun deleteRelationRest(
        deviceId: String,
        restId: Long
    ) {
        deletePublishDeviceJoinUseCase.execute(
            GeneralRestPublishDeviceJoin(
                restId,
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