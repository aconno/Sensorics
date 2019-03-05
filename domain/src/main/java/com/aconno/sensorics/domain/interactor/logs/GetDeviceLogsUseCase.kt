package com.aconno.sensorics.domain.interactor.logs

import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.sensorics.domain.logs.Log
import com.aconno.sensorics.domain.logs.LogsRepository
import io.reactivex.Single

class GetDeviceLogsUseCase(private val logsRepository: LogsRepository): SingleUseCaseWithParameter<List<Log>, String> {
    override fun execute(parameter: String): Single<List<Log>> {
        return logsRepository.getAllDeviceLogs(parameter)
    }
}