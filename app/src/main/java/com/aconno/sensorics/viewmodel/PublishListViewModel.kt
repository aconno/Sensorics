package com.aconno.sensorics.viewmodel

import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.ifttt.GooglePublish
import com.aconno.sensorics.domain.ifttt.MqttPublish
import com.aconno.sensorics.domain.ifttt.RESTPublish
import com.aconno.sensorics.domain.interactor.ifttt.gpublish.DeleteGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.gpublish.GetAllGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.gpublish.UpdateGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mpublish.DeleteMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mpublish.GetAllMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mpublish.UpdateMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.rpublish.DeleteRestPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.rpublish.GetAllRESTPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.rpublish.UpdateRESTPublishUserCase
import com.aconno.sensorics.model.BasePublishModel
import com.aconno.sensorics.model.GooglePublishModel
import com.aconno.sensorics.model.MqttPublishModel
import com.aconno.sensorics.model.RESTPublishModel
import com.aconno.sensorics.model.mapper.*
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PublishListViewModel(
    private val getAllGooglePublishUseCase: GetAllGooglePublishUseCase,
    private val getAllRESTPublishUseCase: GetAllRESTPublishUseCase,
    private val updateGooglePublishUseCase: UpdateGooglePublishUseCase,
    private val updateRESTPublishUserCase: UpdateRESTPublishUserCase,
    private val googlePublishDataMapper: GooglePublishDataMapper,
    private val googlePublishModelDataMapper: GooglePublishModelDataMapper,
    private val restPublishDataMapper: RESTPublishDataMapper,
    private val restPublishModelDataMapper: RESTPublishModelDataMapper,
    private val deleteGooglePublishUseCase: DeleteGooglePublishUseCase,
    private val deleteRestPublishUseCase: DeleteRestPublishUseCase,
    private val getAllMqttPublishUseCase: GetAllMqttPublishUseCase,
    private val updateMqttPublishUseCase: UpdateMqttPublishUseCase,
    private val mqttPublishModelDataMapper: MqttPublishModelDataMapper,
    private val deleteMqttPublishUseCase: DeleteMqttPublishUseCase
) : ViewModel() {

    fun update(googlePublishModel: GooglePublishModel): Disposable {
        val googlePublish = googlePublishModelDataMapper.transform(googlePublishModel)

        return updateGooglePublishUseCase.execute(googlePublish)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun update(restPublish: RESTPublishModel): Disposable {
        val generalRESTPublish = restPublishModelDataMapper.transform(restPublish)

        return updateRESTPublishUserCase.execute(generalRESTPublish)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun update(mqttPublishModel: MqttPublishModel): Disposable {
        val generalMqttPublish = mqttPublishModelDataMapper.toMqttPublish(mqttPublishModel)

        return updateMqttPublishUseCase.execute(generalMqttPublish)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun getAllPublish(): Flowable<List<BasePublishModel>> {
        return Single.merge(
            getAllGooglePublishUseCase.execute(),
            getAllRESTPublishUseCase.execute(),
            getAllMqttPublishUseCase.execute()
        ).flatMapIterable { it }
            .map {
                when (it) {
                    is GooglePublish -> {
                        val transform = googlePublishDataMapper.transform(it)
                        transform
                    }
                    is RESTPublish -> {
                        val transform = restPublishDataMapper.transform(it)
                        transform
                    }
                    is MqttPublish -> {
                        val transform = mqttPublishModelDataMapper.toMqttPublishModel(it)
                        transform
                    }
                    else -> throw NullPointerException("Illegal parameter provided. ")
                }
            }.toList()
            .toFlowable()
    }

    fun delete(googlePublishModel: GooglePublishModel): Disposable {
        val googlePublish = googlePublishModelDataMapper.transform(googlePublishModel)

        return deleteGooglePublishUseCase.execute(googlePublish)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun delete(restPublishModel: RESTPublishModel): Disposable {
        val restPublish = restPublishModelDataMapper.transform(restPublishModel)

        return deleteRestPublishUseCase.execute(restPublish)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun delete(mqttPublishModel: MqttPublishModel): Disposable {
        val mqttPublish = mqttPublishModelDataMapper.toMqttPublish(mqttPublishModel)

        return deleteMqttPublishUseCase.execute(mqttPublish)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

}