package com.aconno.sensorics.domain.interactor.publisher

import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.domain.actions.GeneralAction
import com.aconno.sensorics.domain.ifttt.*
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.MalformedJsonException
import io.reactivex.Single
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter


class ConvertJsonToActionsUseCase : ConvertJsonToObjectsUseCase<Action>() {

    private val gson : Gson

    init {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeHierarchyAdapter(Condition::class.java, object : TypeAdapter<Condition>() {
            override fun write(writer: JsonWriter?, value: Condition?) {} //implementation not needed because the created gson object is used only to read json, not to write
            override fun read(reader: JsonReader?): Condition {
                return Gson().getAdapter(LimitCondition::class.java).read(reader)
            }
        })
        gson = gsonBuilder.create()
    }

    override fun execute(parameter: String): Single<List<Action>> {
        try {

            val list: JsonArray = JsonParser.parseString(parameter).asJsonArray

            list.forEach {
                it.asJsonObject.add("id", JsonPrimitive(0))
            }

            return Single.just(gson.fromJson<ArrayList<Action>>(
                    list.toString(),
                    ACTION_LIST_TYPE
            ))
        } catch (e: JsonSyntaxException) {
            return Single.error(e)
        } catch (e: JsonParseException) {
            return Single.error(e)
        } catch (e: MalformedJsonException) {
            return Single.error(e)
        }
    }

    companion object {
        private val ACTION_LIST_TYPE = object : TypeToken<ArrayList<GeneralAction>>() {}.type
    }




}