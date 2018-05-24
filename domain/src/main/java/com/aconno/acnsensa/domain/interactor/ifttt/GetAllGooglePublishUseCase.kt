package com.aconno.acnsensa.domain.interactor.ifttt

import com.aconno.acnsensa.domain.ifttt.GooglePublish
import com.aconno.acnsensa.domain.ifttt.GooglePublishRepository
import com.aconno.acnsensa.domain.interactor.type.SingleUseCase
import io.reactivex.Single

class GetAllGooglePublishUseCase(
    private val googlePublishRepository: GooglePublishRepository
) : SingleUseCase<List<GooglePublish>> {
    override fun execute(): Single<List<GooglePublish>> {
        return googlePublishRepository.getAllGooglePublish()
    }
}