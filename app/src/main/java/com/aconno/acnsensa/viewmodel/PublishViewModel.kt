package com.aconno.acnsensa.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.aconno.acnsensa.R.string.*
import com.aconno.acnsensa.domain.ifttt.GeneralGooglePublish
import com.aconno.acnsensa.domain.ifttt.GeneralRESTPublish
import com.aconno.acnsensa.domain.interactor.ifttt.AddGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.AddRESTPublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.UpdateGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.UpdateRESTPublishUserCase
import com.aconno.acnsensa.model.GooglePublishModel
import com.aconno.acnsensa.model.RESTPublishModel
import com.aconno.acnsensa.model.mapper.GooglePublishModelDataMapper
import com.aconno.acnsensa.model.mapper.RESTPublishModelDataMapper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class PublishViewModel(
    application: Application,
    private val addGooglePublishUseCase: AddGooglePublishUseCase,
    private val addRESTPublishUseCase: AddRESTPublishUseCase,
    private val updateGooglePublishUseCase: UpdateGooglePublishUseCase,
    private val updateRESTPublishUserCase: UpdateRESTPublishUserCase,
    private val googlePublishModelDataMapper: GooglePublishModelDataMapper,
    private val restPublishModelDataMapper: RESTPublishModelDataMapper
) : AndroidViewModel(application) {

    fun save(
        googlePublishModel: GooglePublishModel
    ) {

        val transform = googlePublishModelDataMapper.transform(googlePublishModel)
        addGooglePublishUseCase.execute(transform)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Timber.d("Save succeeded, action id: ${transform.id}") },
                { Timber.e("Failed to add Google Publish Data with id: ${transform.id}") })
    }

    fun save(
        restPublishModel: RESTPublishModel
    ) {

        val transform = restPublishModelDataMapper.transform(restPublishModel)


        addRESTPublishUseCase.execute(transform)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Timber.d("Save succeeded, action id: ${transform.id}") },
                { Timber.e("Failed to add REST Publish Data with id: ${transform.id}") })
    }

    fun update(
        googlePublishModel: GooglePublishModel
    ) {
        val transform = googlePublishModelDataMapper.transform(googlePublishModel)
        updateGooglePublishUseCase.execute(transform)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
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
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Timber.d("Save succeeded, action id: ${transform.id}") },
                { Timber.e("Failed to update REST Publish Data with id: ${transform.id}") })
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