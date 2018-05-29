package com.aconno.acnsensa.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.aconno.acnsensa.domain.ifttt.GeneralGooglePublish
import com.aconno.acnsensa.domain.ifttt.GeneralRESTPublish
import com.aconno.acnsensa.domain.interactor.ifttt.AddGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.AddRESTPublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.UpdateGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.UpdateRESTPublishUserCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class PublishViewModel(
    application: Application,
    private val addGooglePublishUseCase: AddGooglePublishUseCase,
    private val addRESTPublishUseCase: AddRESTPublishUseCase,
    private val updateGooglePublishUseCase: UpdateGooglePublishUseCase,
    private val updateRESTPublishUserCase: UpdateRESTPublishUserCase
) : AndroidViewModel(application) {

    private var id = 0L


    fun saveGoogle(
        name: String,
        projectId: String,
        region: String,
        deviceRegistry: String,
        device: String,
        privateKey: String,
        timeType: String,
        timeMillis: Long

    ): GeneralGooglePublish {
        val googlePublish = GeneralGooglePublish(
            id,
            name,
            projectId,
            region,
            deviceRegistry,
            device,
            privateKey,
            false,
            timeType,
            timeMillis,
            0L
        )

        addGooglePublishUseCase.execute(googlePublish)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Timber.d("Save succeeded, action id: ${googlePublish.id}") },
                { Timber.e("Failed to add Google Publish Data with id: ${googlePublish.id}") })

        return googlePublish
    }

    fun saveREST(
        name: String,
        url: String,
        method: String,
        timeType: String,
        timeMillis: Long
    ): GeneralRESTPublish {
        val generalRESTPublish = GeneralRESTPublish(
            id, name, url, method, false, timeType, timeMillis, 0L
        )

        addRESTPublishUseCase.execute(generalRESTPublish)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Timber.d("Save succeeded, action id: ${generalRESTPublish.id}") },
                { Timber.e("Failed to add REST Publish Data with id: ${generalRESTPublish.id}") })

        return generalRESTPublish
    }

    fun updateGoogle(
        id: Long,
        name: String,
        projectId: String,
        region: String,
        deviceRegistry: String,
        device: String,
        privateKey: String,
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
            false,
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
        timeType: String,
        timeMillis: Long,
        lastTimeMillis: Long
    ) {
        val generalRESTPublish = GeneralRESTPublish(
            id, name, url, method, false, timeType, timeMillis, lastTimeMillis
        )

        updateRESTPublishUserCase.execute(generalRESTPublish)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Timber.d("Save succeeded, action id: ${generalRESTPublish.id}") },
                { Timber.e("Failed to update REST Publish Data with id: ${generalRESTPublish.id}") })
    }
}