package com.aconno.acnsensa.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.aconno.acnsensa.domain.ifttt.GeneralGooglePublish
import com.aconno.acnsensa.domain.ifttt.GooglePublish
import com.aconno.acnsensa.domain.interactor.ifttt.AddGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.UpdateGooglePublishUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class PublishViewModel(
    application: Application,
    private val addGooglePublishUseCase: AddGooglePublishUseCase,
    private val updateGooglePublishUseCase: UpdateGooglePublishUseCase
) : AndroidViewModel(application) {

    private var id = 0L


    fun save(
        name: String,
        projectId: String,
        region: String,
        deviceRegistry: String,
        device: String,
        privateKey: String
    ): GeneralGooglePublish {
        val googlePublish = GeneralGooglePublish(
            id, name, projectId, region, deviceRegistry, device, privateKey, false
        )

        addGooglePublishUseCase.execute(googlePublish)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Timber.d("Save succeeded, action id: ${googlePublish.id}") },
                { Timber.e("Failed to add Google Publish Data with id: ${googlePublish.id}") })

        return googlePublish
    }

    fun update(
        id: Long,
        name: String,
        projectId: String,
        region: String,
        deviceRegistry: String,
        device: String,
        privateKey: String
    ) {
        val googlePublish = GeneralGooglePublish(
            id, name, projectId, region, deviceRegistry, device, privateKey, false
        )
        
        updateGooglePublishUseCase.execute(googlePublish)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Timber.d("Save succeeded, action id: ${googlePublish.id}") },
                { Timber.e("Failed to update Google Publish Data with id: ${googlePublish.id}") })
    }
}