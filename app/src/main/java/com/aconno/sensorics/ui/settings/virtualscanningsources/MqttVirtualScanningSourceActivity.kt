package com.aconno.sensorics.ui.settings.virtualscanningsources

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.aconno.sensorics.R
import com.aconno.sensorics.model.MqttVirtualScanningSourceModel
import com.aconno.sensorics.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_mqtt_virtual_scanning_source.*

class MqttVirtualScanningSourceActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mqtt_virtual_scanning_source)

        setSupportActionBar(mqtt_source_toolbar)
    }

    companion object {
        private const val MQTT_VIRTUAL_SCANNING_SOURCE_ACTIVITY_KEY = "MQTT_VIRTUAL_SCANNING_SOURCE_ACTIVITY_KEY"
        fun start(context: Context, mqttVirtualScanningSourceModel: MqttVirtualScanningSourceModel? = null) {
            val intent = Intent(context, MqttVirtualScanningSourceActivity::class.java)

            mqttVirtualScanningSourceModel?.let {
                intent.putExtra(
                        MQTT_VIRTUAL_SCANNING_SOURCE_ACTIVITY_KEY,
                        mqttVirtualScanningSourceModel
                )
            }

            context.startActivity(intent)
        }
    }
}
