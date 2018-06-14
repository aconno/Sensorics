package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.ifttt.GooglePublish
import com.aconno.acnsensa.domain.ifttt.MqttPublish
import com.aconno.acnsensa.domain.ifttt.RESTPublish
import com.aconno.acnsensa.domain.interactor.ifttt.gpublish.DeleteGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.gpublish.GetAllGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.gpublish.UpdateGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.mpublish.AddMqttPublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.mpublish.DeleteMqttPublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.mpublish.GetAllMqttPublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.rpublish.DeleteRestPublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.rpublish.GetAllRESTPublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.rpublish.UpdateRESTPublishUserCase
import com.aconno.acnsensa.model.BasePublishModel
import com.aconno.acnsensa.model.GooglePublishModel
import com.aconno.acnsensa.model.MqttPublishModel
import com.aconno.acnsensa.model.RESTPublishModel
import com.aconno.acnsensa.model.mapper.*
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
    private val updateMqttPublishUseCase: AddMqttPublishUseCase,
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
                        transform as BasePublishModel
                    }
                    is RESTPublish -> {
                        val transform = restPublishDataMapper.transform(it)
                        transform as BasePublishModel
                    }
                    is MqttPublish -> {
                        val transform = mqttPublishModelDataMapper.toMqttPublishModel(it)
                        transform as BasePublishModel
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