package com.aconno.acnsensa.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.aconno.acnsensa.domain.ifttt.*
import com.aconno.acnsensa.domain.interactor.ifttt.GetAllGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.GetAllRESTPublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.UpdateGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.UpdateRESTPublishUserCase
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class PublishListViewModel(
    application: Application,
    private val getAllGooglePublishUseCase: GetAllGooglePublishUseCase,
    private val getAllRESTPublishUseCase: GetAllRESTPublishUseCase,
    private val updateGooglePublishUseCase: UpdateGooglePublishUseCase,
    private val updateRESTPublishUserCase: UpdateRESTPublishUserCase

) : AndroidViewModel(application) {

    fun updateGoogle(
        id: Long,
        name: String,
        projectId: String,
        region: String,
        deviceRegistry: String,
        device: String,
        privateKey: String,
        enabled: Boolean,
        timeType: String,
        timeMillis: Long,
        lastTimeMillis: Long
    ) {
        val googlePublish = GeneralGooglePublish(
            id,
            name,
            projectId,
            region,
            deviceRegistry,
            device,
            privateKey,
            enabled,
            timeType,
            timeMillis,
            lastTimeMillis
        )

        updateGooglePublishUseCase.execute(googlePublish)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Timber.d("Save succeeded, action id: ${googlePublish.id}") },
                { Timber.e("Failed to update Google Publish Data with id: ${googlePublish.id}") })
    }

    fun updateREST(
        id: Long,
        name: String,
        url: String,
        method: String,
        enabled: Boolean,
        timeType: String,
        timeMillis: Long,
        lastTimeMillis: Long
    ) {
        val generalRESTPublish = GeneralRESTPublish(
            id, name, url, method, enabled, timeType, timeMillis, lastTimeMillis
        )

        updateRESTPublishUserCase.execute(generalRESTPublish)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Timber.d("Save succeeded, action id: ${generalRESTPublish.id}") },
                { Timber.e("Failed to update REST Publish Data with id: ${generalRESTPublish.id}") })
    }

    fun getAllPublish(): Flowable<List<BasePublish>> {
        return Single.merge(
            getAllGooglePublishUseCase.execute(),
            getAllRESTPublishUseCase.execute()
        )
    }
}