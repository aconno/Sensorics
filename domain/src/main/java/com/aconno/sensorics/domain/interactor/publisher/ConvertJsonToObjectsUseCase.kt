package com.aconno.sensorics.domain.interactor.publisher

import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter

abstract class ConvertJsonToObjectsUseCase<T> : SingleUseCaseWithParameter<List<T>, String> {


}