package de.troido.acnsensa.persistence.preferences

import android.content.SharedPreferences
import de.troido.acnsensa.data.Axis
import de.troido.acnsensa.data.AxisComponent
import de.troido.acnsensa.data.Sensor

/**
 * Separates the simple class name and the axis name in the [SharedPreferences] key of an
 * [AxisComponent].
 */
private const val NAME_AXIS_SEPARATOR = "_"

/**
 * Returns the [SharedPreferences] key for the given sensor class and the [Axis].
 * While a constraint on both [Sensor] and [AxisComponent] would be more appropriate, there are
 * typechecking issues with such a solution if the type is not explicitly known.
 */
fun <S : Sensor<*>> axisKey(clazz: Class<S>, axis: Axis): String =
        clazz.simpleName + NAME_AXIS_SEPARATOR + axis.name

/**
 * Returns the [SharedPreferences] key for the axis component sensor class [S] and the given [Axis].
 */
inline fun <reified S> axisKey(axis: Axis): String where S : Sensor<*>,
                                                         S : AxisComponent =
        axisKey(S::class.java, axis)
