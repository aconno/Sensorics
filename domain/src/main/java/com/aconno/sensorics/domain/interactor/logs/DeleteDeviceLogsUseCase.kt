package com.aconno.sensorics.domain.interactor.logs

import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.sensorics.domain.logs.LogsRepository
import io.reactivex.Completable

class DeleteDeviceLogsUseCase(private val logsRepository: LogsRepository): CompletableUseCaseWithParameter<String> {
    override fun execute(parameter: String): Completable {
        return logsRepository.deleteAllDeviceLogs(parameter)
    }
}