package com.aconno.sensorics.viewmodel

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.ifttt.PublishDeviceJoin
import com.aconno.sensorics.domain.interactor.ifttt.publish.GetAllDeviceParameterPlaceholderStringsUseCase
import com.aconno.sensorics.domain.interactor.repository.DeletePublishDeviceJoinUseCase
import com.aconno.sensorics.domain.interactor.repository.SavePublishDeviceJoinUseCase
import com.aconno.sensorics.model.BasePublishModel
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

abstract class PublisherViewModel<T>(
    private val savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    private val deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
    private val getAllDeviceParameterPlaceholderStringsUseCase: GetAllDeviceParameterPlaceholderStringsUseCase
) : ViewModel() where T : BasePublishModel {
    abstract fun getById(id: Long): Maybe<T>
    abstract fun save(model: T): Single<Long>

    abstract fun createPublishDeviceJoin(
        deviceId: String,
        publishId: Long
    ): PublishDeviceJoin

    fun addOrUpdateRelation(
        deviceId: String,
        publishId: Long
    ): Completable {
        return savePublishDeviceJoinUseCase.execute(
            createPublishDeviceJoin(deviceId, publishId)
        )
    }

    fun deleteRelation(
        deviceId: String,
        publishId: Long
    ): Completable {
        return deletePublishDeviceJoinUseCase.execute(
            createPublishDeviceJoin(deviceId, publishId)
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

    fun getAllDeviceParameterPlaceholderStrings() : Single<Map<String, List<String>>> {
        return getAllDeviceParameterPlaceholderStringsUseCase.execute().subscribeOn(Schedulers.io())
    }
}