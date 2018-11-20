package com.aconno.sensorics.domain.interactor.format

import com.aconno.sensorics.domain.ConfigListManager
import com.aconno.sensorics.domain.interactor.resources.GetMainResourceUseCase
import io.reactivex.Single

class GetMainResourceUseCaseImpl(
    private val configListManager: ConfigListManager
) : GetMainResourceUseCase {

    override fun execute(deviceName: String): Single<String> {
        return Single.fromCallable {
            configListManager.getConfigs()
                .find {
                    it.name == deviceName
                }?.deviceScreenPath
        }
    }
}
