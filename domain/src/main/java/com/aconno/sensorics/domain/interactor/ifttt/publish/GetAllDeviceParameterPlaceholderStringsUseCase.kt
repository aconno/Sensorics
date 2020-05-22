package com.aconno.sensorics.domain.interactor.ifttt.publish

import com.aconno.sensorics.domain.FormatListManager
import com.aconno.sensorics.domain.interactor.type.SingleUseCase
import com.aconno.sensorics.domain.toSnakeCase
import io.reactivex.Single
import java.util.Comparator

class GetAllDeviceParameterPlaceholderStringsUseCase(
    private val formatListManager: FormatListManager
) : SingleUseCase<Map<String,List<Pair<String,String?>>>> {

    override fun execute(): Single<Map<String, List<Pair<String,String?>>>> {
        return Single.fromCallable {
            formatListManager.getFormats()
                .groupBy { it.getName() }
                .mapValues { mapEntry ->
                    mapEntry.value
                        .map { it.getFormat() }
                        .map {
                            it.values.map { paramFormat ->
                                Pair(
                                    paramFormat.name.replace(" ","").toSnakeCase(),
                                    paramFormat.description
                                )
                            }
                        }.toList().flatten()
                }.mapValues {
                    it.value.toSortedSet(kotlin.Comparator { a, b -> a.first.compareTo(b.first)  }).toList()
                }
            }
        }


}