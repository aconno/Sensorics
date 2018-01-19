package de.troido.acnsensa.util

import kotlinx.reflect.lite.ParameterMetadata
import kotlinx.reflect.lite.ReflectionLite

inline fun <reified T : Any> ctorParamNames(): Array<String> =
        ReflectionLite.loadClassMetadata(T::class.java)
                ?.getConstructor(T::class.java.constructors.first())
                ?.parameters
                ?.mapNotNull(ParameterMetadata::name)
                ?.toTypedArray()
                ?: arrayOf()

inline fun <reified T : Any> ctorParamTypes(): Array<out Class<*>> =
        T::class.java.constructors.first().parameterTypes
