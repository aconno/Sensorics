package com.aconno.sensorics.domain.interactor.publisher

import com.aconno.sensorics.domain.ifttt.*
import com.aconno.sensorics.domain.ifttt.outcome.PublishType
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.MalformedJsonException
import io.reactivex.Single

class ConvertJsonToPublishersUseCase : ConvertJsonToObjectsUseCase<BasePublish>() {
    private val gson = Gson()
    override fun execute(parameter: String): Single<List<BasePublish>> {
        try {

            val list: JsonArray = JsonParser().parse(parameter).asJsonArray

            list.forEach {
                it.asJsonObject.add("id", JsonPrimitive(0))
            }

            return Single.just(gson.fromJson<ArrayList<BasePublishImpl>>(
                list.toString(),
                BASE_PUBLISH_TYPE
            ).mapIndexedNotNull { index, publisher ->
                when (publisher.type) {
                    PublishType.MQTT -> gson.fromJson(list[index], GeneralMqttPublish::class.java)
                    PublishType.REST -> gson.fromJson(list[index], GeneralRestPublish::class.java)
                    PublishType.GOOGLE -> gson.fromJson(list[index], GeneralGooglePublish::class.java)
                }
            })
        } catch (e: JsonSyntaxException) {
            return Single.error(e)
        } catch (e: JsonParseException) {
            return Single.error(e)
        } catch (e: MalformedJsonException) {
            return Single.error(e)
        }
    }

    companion object {
        private val BASE_PUBLISH_TYPE = object : TypeToken<ArrayList<BasePublishImpl>>() {}.type
    }
}