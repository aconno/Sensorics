package de.troido.acnsensa.tasker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.RadioButton
import com.twofortyfouram.locale.api.Intent.EXTRA_BUNDLE
import com.twofortyfouram.locale.api.Intent.EXTRA_STRING_BLURB
import com.twofortyfouram.locale.sdk.client.ui.activity.AbstractAppCompatPluginActivity
import de.troido.acnsensa.R
import kotlinx.android.synthetic.main.activity_tasker_sensor.*

class SensorTaskerActivity : AbstractAppCompatPluginActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasker_sensor)

        sp_sensor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) {
            }

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val isAxis = parent.getItemAtPosition(position).toString().contains("Axis")
                for (i in 0 until rg_sensor_axis.childCount) {
                    rg_sensor_axis.getChildAt(i).isEnabled = isAxis
                }
            }
        }
        btn_save.setOnClickListener { finish() }

        previousBundle?.let { data ->
            sp_sensor.setSelection(data.getInt(BUNDLE_EXTRA_INT_SENSOR))
            et_target.setText(data.getFloat(BUNDLE_EXTRA_VALUE_TARGET).toString())
            data.getInt(BUNDLE_EXTRA_SENSOR_AXIS, -1).let {
                if (it != -1) {
                    rg_sensor_axis.check(it)
                    for (i in 0 until rg_sensor_axis.childCount) {
                        rg_sensor_axis.getChildAt(i).isEnabled = true
                    }
                }
            }
            rg_trigger_mode.check(data.getInt(BUNDLE_EXTRA_TRIGGER_TYPE))
        }
    }

    override fun isBundleValid(bundle: Bundle): Boolean = BundleUtils.isBundleValid(bundle)

    override fun onPostCreateWithPreviousResult(bundle: Bundle, s: String) {
        et_target.setText(bundle.getFloat(BUNDLE_EXTRA_VALUE_TARGET).toString())
        findViewById<RadioButton>(bundle.getInt(BUNDLE_EXTRA_TRIGGER_TYPE)).isChecked = true
    }

    override fun getResultBundle(): Bundle {
        return Bundle().apply {
            putString(BUNDLE_EXTRA_STRING_SENSOR, sp_sensor.selectedItem.toString().let {
                if (it.contains("Axis")) {
                    putInt(BUNDLE_EXTRA_SENSOR_AXIS, rg_sensor_axis.checkedRadioButtonId)
                    it.replace(" ", "") + "_" + findViewById<RadioButton>(rg_sensor_axis.checkedRadioButtonId).text
                } else it
            })
            putInt(BUNDLE_EXTRA_INT_SENSOR, sp_sensor.selectedItemPosition)
            putFloat(BUNDLE_EXTRA_VALUE_TARGET, et_target.text.toString().toFloat())
            putInt(BUNDLE_EXTRA_TRIGGER_TYPE, rg_trigger_mode.checkedRadioButtonId)
        }
    }

    override fun getResultBlurb(bundle: Bundle): String {
        return String.format(
                "Sensor: %s\nTrigger: %.2f\nOnly if %s",
                bundle.getString(BUNDLE_EXTRA_STRING_SENSOR),
                bundle.getFloat(BUNDLE_EXTRA_VALUE_TARGET),
                findViewById<RadioButton>(bundle.getInt(BUNDLE_EXTRA_TRIGGER_TYPE)).text
        )
    }


    override fun finish() {
        if (et_target.text.isNotEmpty()) {
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(EXTRA_BUNDLE, resultBundle)
                putExtra(EXTRA_STRING_BLURB, getResultBlurb(resultBundle))
            })
        } else {
            setResult(Activity.RESULT_CANCELED)
        }
        super.finish()
    }

    companion object {
        val BUNDLE_EXTRA_STRING_SENSOR = "de.troido.sensorboard.tasker.extra.SENSOR"
        val BUNDLE_EXTRA_INT_SENSOR = "de.troido.sensorboard.tasker.extra.SENSOR_POSITION"
        val BUNDLE_EXTRA_SENSOR_AXIS = "de.troido.sensorboard.tasker.extra.SENSOR_AXIS"
        val BUNDLE_EXTRA_TRIGGER_TYPE = "de.troido.sensorboard.tasker.extra.TRIGGER_TYPE"
        val BUNDLE_EXTRA_CLASS_DATA_TYPE = "de.troido.sensorboard.tasker.extra.DATA_TYPE"
        val BUNDLE_EXTRA_VALUE_OLD = "de.troido.sensorboard.tasker.extra.VALUE_OLD"
        val BUNDLE_EXTRA_VALUE_NEW = "de.troido.sensorboard.tasker.extra.VALUE_NEW"
        val BUNDLE_EXTRA_VALUE_TARGET = "de.troido.sensorboard.tasker.extra.VALUE_TARGET"
    }
}
