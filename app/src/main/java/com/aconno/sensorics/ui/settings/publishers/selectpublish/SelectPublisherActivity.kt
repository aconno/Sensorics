package com.aconno.sensorics.ui.settings.publishers.selectpublish

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aconno.sensorics.databinding.ActivitySelectPublisherBinding

class SelectPublisherActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectPublisherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectPublisherBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.customToolbar)

        binding.googleCloudIot.setOnClickListener {
            GoogleCloudPublisherActivity.start(
                this@SelectPublisherActivity
            )
        }

        binding.httpBackend.setOnClickListener {
            RestPublisherActivity.start(
                this@SelectPublisherActivity
            )
        }

        binding.mqttBackend.setOnClickListener {
            MqttPublisherActivity.start(
                this@SelectPublisherActivity
            )
        }

        binding.azureMqttBackend.setOnClickListener {
            AzureMqttPublisherActivity.start(this@SelectPublisherActivity)
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
