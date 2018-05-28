package com.aconno.acnsensa.domain.interactor.ifttt

import com.aconno.acnsensa.domain.ifttt.BasePublish
import com.aconno.acnsensa.domain.ifttt.GooglePublish
import com.aconno.acnsensa.domain.ifttt.GooglePublishRepository
import com.aconno.acnsensa.domain.interactor.type.SingleUseCase
import io.reactivex.Single

class GetAllEnabledGooglePublishUseCase(
    private val googlePublishRepository: GooglePublishRepository
) : SingleUseCase<List<BasePublish>> {
    override fun execute(): Single<List<BasePublish>> {
        return googlePublishRepository.getAllEnabledGooglePublish()
    }
}