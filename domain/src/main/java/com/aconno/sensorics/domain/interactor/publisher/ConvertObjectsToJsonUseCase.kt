package com.aconno.sensorics.domain.interactor.publisher

import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import com.google.gson.Gson
import io.reactivex.Single

class ConvertObjectsToJsonUseCase<T> :
    SingleUseCaseWithParameter<String, List<T>> {
    private val gson: Gson = Gson()
    override fun execute(parameter: List<T>): Single<String> {
        return Single.just(gson.toJson(parameter))
    }
}