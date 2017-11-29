package de.troido.acnsensa.tasker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.twofortyfouram.locale.api.Intent.*
import de.troido.acnsensa.R.id.*
import de.troido.acnsensa.tasker.SensorTaskerActivity.Companion.BUNDLE_EXTRA_STRING_SENSOR
import de.troido.acnsensa.tasker.SensorTaskerActivity.Companion.BUNDLE_EXTRA_TRIGGER_TYPE
import de.troido.acnsensa.tasker.SensorTaskerActivity.Companion.BUNDLE_EXTRA_VALUE_NEW
import de.troido.acnsensa.tasker.SensorTaskerActivity.Companion.BUNDLE_EXTRA_VALUE_OLD
import de.troido.acnsensa.tasker.SensorTaskerActivity.Companion.BUNDLE_EXTRA_VALUE_TARGET

class QueryReceiver : EventReceiver() {
    override fun getPluginConditionResult(context: Context, conditionBundle: Bundle, originalIntent: Intent): Int {
        try {
            val dataBundle = TaskerPlugin.Event.retrievePassThroughData(originalIntent) ?: return RESULT_CONDITION_UNKNOWN
            val dataSensor = dataBundle.getString(BUNDLE_EXTRA_STRING_SENSOR)
            val targetSensor = conditionBundle.getString(BUNDLE_EXTRA_STRING_SENSOR)
            return if (dataSensor == targetSensor) {
                Log.d("QueryReceiver", "$targetSensor => $dataSensor")
                if (resultMatches(conditionBundle, dataBundle, context)) {
                    RESULT_CONDITION_SATISFIED
                } else {
                    RESULT_CONDITION_UNSATISFIED
                }
            } else {
                RESULT_CONDITION_UNSATISFIED
            }
        } catch (e: Exception) {
            Log.e("QueryReceiver", "Error", e)
        }

        return RESULT_CONDITION_UNKNOWN
    }

    private fun resultMatches(bundle1: Bundle, data: Bundle, context: Context): Boolean {
        val old = data.getString(BUNDLE_EXTRA_VALUE_OLD).toFloat()
        val new = data.getString(BUNDLE_EXTRA_VALUE_NEW).toFloat()
        val target = bundle1.getFloat(BUNDLE_EXTRA_VALUE_TARGET)
        val triggerType = bundle1.getInt(BUNDLE_EXTRA_TRIGGER_TYPE)
        val triggerString = when (triggerType) {
            rb_trigger_above -> "Above"
            rb_trigger_exact -> "Exact"
            rb_trigger_below -> "Below"
            else -> triggerType.toString()
        }
        Log.d("QueryReceiver", "Old: $old New: $new Target: $target Trigger type: $triggerString")
        Toast.makeText(context, "Old: $old New: $new Target: $target Trigger type: $triggerString", LENGTH_SHORT).show()

        return when (triggerType) {
            rb_trigger_above -> {
                new >= target && old < target
            }
            rb_trigger_exact -> {
                new == target && old != target
            }
            rb_trigger_below -> {
                new <= target && old > target
            }
            else -> {
                Log.e("QueryReceiver", triggerType.toString())
                false
            }
        }
    }

    override fun isBundleValid(bundle: Bundle): Boolean = BundleUtils.isBundleValid(bundle)

    override fun isAsync(): Boolean = false
}
