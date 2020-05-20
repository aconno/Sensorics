package com.aconno.sensorics.domain.interactor.ifttt.publish

import com.aconno.sensorics.domain.FormatListManager
import com.aconno.sensorics.domain.interactor.type.SingleUseCase
import com.aconno.sensorics.domain.toSnakeCase
import io.reactivex.Single

class GetAllDeviceParameterPlaceholderStringsUseCase(
    private val formatListManager: FormatListManager
) : SingleUseCase<Map<String,List<String>>> {

    override fun execute(): Single<Map<String, List<String>>> {
        return Single.fromCallable {
            formatListManager.getFormats()
                .groupBy { it.getName() }
                .mapValues { mapEntry ->
                    mapEntry.value
                        .map { it.getFormat() }
                        .map {
                            it.keys.map { paramName ->
                                paramName.replace(" ","").toSnakeCase()
                            }
                        }.toList().flatten()
                }.mapValues {
                    it.value.toSortedSet().toList()
                }
            }
        }


}