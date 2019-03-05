package com.aconno.sensorics.domain.interactor.logs

import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.sensorics.domain.logs.Log
import com.aconno.sensorics.domain.logs.LogsRepository
import io.reactivex.Completable

class AddLogUseCase(private val logsRepository: LogsRepository): CompletableUseCaseWithParameter<Log> {
    override fun execute(parameter: Log): Completable {
        return logsRepository.insertLog(parameter)
    }
}