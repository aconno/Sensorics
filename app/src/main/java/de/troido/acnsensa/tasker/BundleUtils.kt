package de.troido.acnsensa.tasker

import android.os.Bundle
import android.util.Log

import com.twofortyfouram.assertion.BundleAssertions
import de.troido.acnsensa.tasker.SensorTaskerActivity.Companion.BUNDLE_EXTRA_CLASS_DATA_TYPE
import de.troido.acnsensa.tasker.SensorTaskerActivity.Companion.BUNDLE_EXTRA_STRING_SENSOR
import de.troido.acnsensa.tasker.SensorTaskerActivity.Companion.BUNDLE_EXTRA_TRIGGER_TYPE
import de.troido.acnsensa.tasker.SensorTaskerActivity.Companion.BUNDLE_EXTRA_VALUE_NEW
import de.troido.acnsensa.tasker.SensorTaskerActivity.Companion.BUNDLE_EXTRA_VALUE_OLD
import de.troido.acnsensa.tasker.SensorTaskerActivity.Companion.BUNDLE_EXTRA_VALUE_TARGET

/**
 * Created by nicba1010 on 10/19/2017.
 */

object BundleUtils {
    fun isBundleValid(bundle: Bundle?): Boolean {
        if (bundle != null) {
            try {
                BundleAssertions.assertHasString(bundle, BUNDLE_EXTRA_STRING_SENSOR)
                BundleAssertions.assertHasKey(bundle, BUNDLE_EXTRA_VALUE_TARGET)
                BundleAssertions.assertHasKey(bundle, BUNDLE_EXTRA_TRIGGER_TYPE)
                return true
            } catch (e: AssertionError) {
                Log.e("TASKER", "Fail", e)
            }

        }
        return false
    }

    fun isQueryBundleValid(bundle: Bundle?): Boolean {
        if (bundle != null) {
            try {
                BundleAssertions.assertHasString(bundle, BUNDLE_EXTRA_STRING_SENSOR)
                BundleAssertions.assertHasString(bundle, BUNDLE_EXTRA_VALUE_NEW)
                BundleAssertions.assertHasString(bundle, BUNDLE_EXTRA_VALUE_OLD)
                BundleAssertions.assertHasKey(bundle, BUNDLE_EXTRA_CLASS_DATA_TYPE)
                return true
            } catch (e: AssertionError) {
                Log.e("TASKER", "Fail", e)
            }

        }
        return false
    }

    fun printBundle(bundle: Bundle) {
        Log.d("BundlePrinter", "Bundle " + bundle.toString())
        for (key in bundle.keySet()) {
            Log.d("BundlePrinter", key + " -> " + bundle.get(key))
        }
    }
}
