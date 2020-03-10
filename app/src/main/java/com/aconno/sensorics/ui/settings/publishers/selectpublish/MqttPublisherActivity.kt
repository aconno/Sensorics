package com.aconno.sensorics.ui.settings.publishers.selectpublish

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.Toast
import com.aconno.sensorics.PublisherIntervalConverter
import com.aconno.sensorics.R
import com.aconno.sensorics.data.publisher.MqttPublisher
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.model.BasePublishModel
import com.aconno.sensorics.model.MqttPublishModel
import com.aconno.sensorics.model.mapper.MqttPublishModelDataMapper
import com.aconno.sensorics.viewmodel.MqttPublisherViewModel
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_mqtt_publisher.*
import kotlinx.android.synthetic.main.layout_datastring.*
import kotlinx.android.synthetic.main.layout_mqtt.*
import kotlinx.android.synthetic.main.layout_publisher_header.*
import javax.inject.Inject

class MqttPublisherActivity : BaseMqttPublisherActivity<MqttPublishModel>() {

    @Inject
    lateinit var mqttPublisherViewModel: MqttPublisherViewModel

    override var publishModel: MqttPublishModel? = null
    override var updating: Boolean = false
    override var publisherKey: String = MQTT_PUBLISHER_ACTIVITY_KEY

    override var progressBar: ProgressBar
        get() = progressbar
        set(_) {}
    override var layoutId: Int = R.layout.activity_mqtt_publisher
    override var deviceSelectFrameId: Int = R.id.frame

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (publishModel != null) {
            updating = true
        }
    }

    override fun onTestConnectionSuccess() {
        edit_url_mqtt?.error = null
    }

    override fun onTestConnectionFail(exception: Throwable?) {
        exception?.message?.let { m ->
            if (m.contains(edit_url_mqtt?.text.toString())) {
                edit_url_mqtt?.error = getString(R.string.mqtt_format)
            } else {
                edit_url_mqtt?.error = null
            }
        }
    }

    override fun setPublisherSpecificFields() {
        publishModel?.let { model ->
            edit_url_mqtt.setText(model.url)
            edit_clientid_mqtt.setText(model.clientId)
            edit_username_mqtt.setText(model.username)
            edit_password_mqtt.setText(model.password)
            edit_topic_mqtt.setText(model.topic)

            when (model.qos) {
                0 -> qos_0.isChecked = true
                1 -> qos_1.isChecked = true
                2 -> qos_2.isChecked = true
            }

            edit_datastring.setText(model.dataString)

        }

    }

    override fun savePublisher(publishModel: BasePublishModel): Single<Long> {
        return mqttPublisherViewModel.save(publishModel as MqttPublishModel)
    }

    override fun addOrUpdateRelation(deviceId: String, publisherId: Long): Completable {
        return mqttPublisherViewModel.addOrUpdateMqttRelation(
            deviceId = deviceId,
            mqttId = publisherId
        )
    }

    override fun deleteRelation(deviceId: String, publisherId: Long): Completable {
        return mqttPublisherViewModel.deleteRelationMqtt(deviceId, publisherId)
    }


    override fun toPublishModel(): MqttPublishModel? {
        val name = edit_name.text.toString().trim()
        val url = edit_url_mqtt.text.toString().trim()
        val clientId = edit_clientid_mqtt.text.toString().trim()
        val username = edit_username_mqtt.text.toString().trim()
        val password = edit_password_mqtt.text.toString().trim()
        val topic = edit_topic_mqtt.text.toString().trim()
        val qos =
            findViewById<RadioButton>(radio_group_mqtt.checkedRadioButtonId).text.toString().toInt()
        val timeType = spinner_interval_time.selectedItem.toString()
        val timeCount = edit_interval_count.text.toString()
        val datastring = edit_datastring.text.toString()

        if (mqttPublisherViewModel.checkFieldsAreEmpty(
                name,
                url,
                clientId,
                username,
                password,
                topic,
                timeType,
                timeCount,
                datastring
            )
        ) {
            Toast.makeText(
                this,
                getString(R.string.please_fill_blanks),
                Toast.LENGTH_SHORT
            ).show()
            return null
        } else {
            if (!isDataStringValid()) {
                Toast.makeText(
                    this,
                    getString(R.string.data_string_not_valid),
                    Toast.LENGTH_SHORT
                )
                    .show()

                return null
            }
        }

        val id = if (publishModel == null) 0 else publishModel!!.id
        val timeMillis = PublisherIntervalConverter.calculateMillis(this, timeCount, timeType)
        val lastTimeMillis = if (publishModel == null) 0 else publishModel!!.lastTimeMillis
        return MqttPublishModel(
            id,
            name,
            url,
            clientId,
            username,
            password,
            topic,
            qos,
            publishModel?.enabled ?: true,
            timeType,
            timeMillis,
            lastTimeMillis,
            datastring
        )
    }


    override fun getPublisherFor(publishModel: MqttPublishModel): Publisher {
        return MqttPublisher(
            applicationContext,
            MqttPublishModelDataMapper().toMqttPublish(publishModel),
            listOf(Device("TestDevice", "Name", "Mac")),
            syncRepository
        )
    }


    companion object {
        //This is used for the file selector intent
        private const val MQTT_PUBLISHER_ACTIVITY_KEY = "MQTT_PUBLISHER_ACTIVITY_KEY"

        fun start(context: Context, mqttPublishModel: MqttPublishModel? = null) {
            val intent = Intent(context, MqttPublisherActivity::class.java)

            mqttPublishModel?.let {
                intent.putExtra(
                    MQTT_PUBLISHER_ACTIVITY_KEY,
                    mqttPublishModel
                )
            }

            context.startActivity(intent)
        }
    }
}
