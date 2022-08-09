package com.aconno.sensorics.domain.interactor.resources

import com.aconno.sensorics.domain.ConfigListManager
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Single

/**
 * Use-case for getting the main device screen for a certain device according to device name.
 *
 * TODO: Remove getting device screen by device name and get it by ID because this is horrible and
 * TODO: WILL fail if we name another device the same name accidentally.
 *
 * @param configListManager the manager that manages all the available configurations
 * @param cacheDirPath cache directory path
 */
class GetMainResourceUseCase(
    private val configListManager: ConfigListManager,
    private val cacheDirPath: String
) : SingleUseCaseWithParameter<String, String> {

    override fun execute(parameter: String): Single<String> {
        return Single.fromCallable {
            configListManager.getConfigs()
                .find {
                    it.name == parameter
                }?.let {
                    "file://" + cacheDirPath + it.deviceScreenPath
                }
        }
    }
}
