package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.ifttt.GeneralGooglePublishDeviceJoin
import com.aconno.acnsensa.domain.ifttt.GeneralRestPublishDeviceJoin
import com.aconno.acnsensa.domain.ifttt.RESTHeader
import com.aconno.acnsensa.domain.interactor.ifttt.gpublish.AddGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.rpublish.AddRESTPublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.*
import com.aconno.acnsensa.model.DeviceRelationModel
import com.aconno.acnsensa.model.GooglePublishModel
import com.aconno.acnsensa.model.RESTHeaderModel
import com.aconno.acnsensa.model.RESTPublishModel
import com.aconno.acnsensa.model.mapper.DeviceRelationModelMapper
import com.aconno.acnsensa.model.mapper.GooglePublishModelDataMapper
import com.aconno.acnsensa.model.mapper.RESTHeaderModelMapper
import com.aconno.acnsensa.model.mapper.RESTPublishModelDataMapper
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PublishViewModel(
    private val addGooglePublishUseCase: AddGooglePublishUseCase,
    private val addRESTPublishUseCase: AddRESTPublishUseCase,
    private val googlePublishModelDataMapper: GooglePublishModelDataMapper,
    private val restPublishModelDataMapper: RESTPublishModelDataMapper,
    private val savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    private val deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
    private val devicesThatConnectedWithGooglePublishUseCase: GetDevicesThatConnectedWithGooglePublishUseCase,
    private val devicesThatConnectedWithRESTPublishUseCase: GetDevicesThatConnectedWithRESTPublishUseCase,
    private val savedDevicesMaybeUseCase: GetSavedDevicesMaybeUseCase,
    private val deviceRelationModelMapper: DeviceRelationModelMapper,
    private val saveRESTHeaderUseCase: SaveRESTHeaderUseCase,
    private val deleteRESTHeaderUseCase: DeleteRESTHeaderUseCase,
    private val getRESTHeadersByIdUseCase: GetRESTHeadersByIdUseCase,
    private val restHeaderModelMapper: RESTHeaderModelMapper
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

    fun addOrUpdateRestRelation(
        deviceId: String,
        restId: Long
    ): Disposable {
        return savePublishDeviceJoinUseCase.execute(
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
    ): Disposable {
        return savePublishDeviceJoinUseCase.execute(
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
    ): Disposable {
        return deletePublishDeviceJoinUseCase.execute(
            GeneralGooglePublishDeviceJoin(
                googleId,
                deviceId
            )
        ).subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun deleteRelationRest(
        deviceId: String,
        restId: Long
    ): Disposable {
        return deletePublishDeviceJoinUseCase.execute(
            GeneralRestPublishDeviceJoin(
                restId,
                deviceId
            )
        ).subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun addRESTHeader(
        list: List<RESTHeaderModel>,
        it: Long
    ): Disposable {
        return saveRESTHeaderUseCase.execute(
            restHeaderModelMapper.toRESTHeaderListByRESTPublishId(list, it)
        ).subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun deleteRESTHeader(restHeader: RESTHeader): Disposable {
        return deleteRESTHeaderUseCase.execute(
            restHeader
        ).subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun getRESTHeadersById(rId: Long): Maybe<List<RESTHeaderModel>> {
        return getRESTHeadersByIdUseCase.execute(rId)
            .map(restHeaderModelMapper::toRESTHeaderModelList)
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