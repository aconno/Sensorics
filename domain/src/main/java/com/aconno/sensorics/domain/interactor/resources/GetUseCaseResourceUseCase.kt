package com.aconno.sensorics.domain.interactor.resources

import com.aconno.sensorics.domain.ConfigListManager
import io.reactivex.Single

class GetUseCaseResourceUseCase(
    private val configListManager: ConfigListManager,
    private val cacheDirPath: String
) {

    fun execute(deviceName: String): Single<String> {
        return Single.fromCallable {
            configListManager.getConfigs()
                .find {
                    it.name == deviceName
                }?.let {
                    "file://" + cacheDirPath + it.usecaseScreenPath
                }
        }
    }
}
