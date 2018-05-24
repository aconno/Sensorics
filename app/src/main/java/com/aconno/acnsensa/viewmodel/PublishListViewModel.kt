package com.aconno.acnsensa.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.aconno.acnsensa.domain.ifttt.GeneralGooglePublish
import com.aconno.acnsensa.domain.ifttt.GooglePublish
import com.aconno.acnsensa.domain.interactor.ifttt.GetAllGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.UpdateGooglePublishUseCase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class PublishListViewModel(
    application: Application,
    private val getAllGooglePublishUseCase: GetAllGooglePublishUseCase,
    private val updateGooglePublishUseCase: UpdateGooglePublishUseCase
) : AndroidViewModel(application) {

    fun update(
        id: Long,
        name: String,
        projectId: String,
        region: String,
        deviceRegistry: String,
        device: String,
        privateKey: String,
        enabled: Boolean
    ) {
        val googlePublish = GeneralGooglePublish(
            id, name, projectId, region, deviceRegistry, device, privateKey, enabled
        )

        updateGooglePublishUseCase.execute(googlePublish)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Timber.d("Save succeeded, action id: ${googlePublish.id}") },
                { Timber.e("Failed to update Google Publish Data with id: ${googlePublish.id}") })
    }


    fun getAllPublish(): Single<List<GooglePublish>> {
        return getAllGooglePublishUseCase.execute()
    }
}