package com.aconno.sensorics.ui.settings.publishers.selectpublish

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.aconno.sensorics.R
import kotlinx.android.synthetic.main.activity_select_publisher.*

class SelectPublisherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_publisher)
        setSupportActionBar(custom_toolbar)

        google_cloud_iot.setOnClickListener {
            GoogleCloudPublisherActivity.start(
                this@SelectPublisherActivity
            )
        }

        http_backend.setOnClickListener {
            RestPublisherActivity.start(
                this@SelectPublisherActivity
            )
        }

        mqtt_backend.setOnClickListener {
            MqttPublisherActivity.start(
                this@SelectPublisherActivity
            )
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SelectPublisherActivity::class.java)
            context.startActivity(
                intent,
                ActivityOptions.makeSceneTransitionAnimation(context as Activity?).toBundle()
            )
        }
    }
}
