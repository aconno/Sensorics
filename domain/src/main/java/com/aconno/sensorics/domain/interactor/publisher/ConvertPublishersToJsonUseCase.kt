package com.aconno.sensorics.domain.interactor.publisher

import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import com.google.gson.Gson
import io.reactivex.Single

class ConvertPublishersToJsonUseCase :
    SingleUseCaseWithParameter<String, List<BasePublish>> {
    private val gson: Gson = Gson()
    override fun execute(parameter: List<BasePublish>): Single<String> {
        return Single.just(gson.toJson(parameter))
    }
}