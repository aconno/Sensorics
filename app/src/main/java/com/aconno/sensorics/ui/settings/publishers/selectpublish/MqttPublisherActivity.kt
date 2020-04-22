package com.aconno.sensorics.ui.settings.publishers.selectpublish

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.RadioButton
import android.widget.Toast
import com.aconno.sensorics.PublisherIntervalConverter
import com.aconno.sensorics.R
import com.aconno.sensorics.data.publisher.MqttPublisher
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.model.MqttPublishModel
import com.aconno.sensorics.model.mapper.MqttPublishModelDataMapper
import com.aconno.sensorics.viewmodel.MqttPublisherViewModel
import com.aconno.sensorics.viewmodel.PublisherViewModel
import kotlinx.android.synthetic.main.activity_mqtt_publisher.*
import kotlinx.android.synthetic.main.layout_datastring.*
import kotlinx.android.synthetic.main.layout_mqtt.*
import kotlinx.android.synthetic.main.layout_publisher_header.*
import java.util.regex.Pattern
import javax.inject.Inject

class MqttPublisherActivity : BasePublisherActivity<MqttPublishModel>() {

    @Inject
    lateinit var mqttPublisherViewModel: MqttPublisherViewModel

    override val viewModel: PublisherViewModel<MqttPublishModel>
        get() = mqttPublisherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_mqtt_publisher)

        setSupportActionBar(custom_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        super.onCreate(savedInstanceState)
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

    fun validateMqttUrl(): Boolean {
        return VALID_MQTT_URL_PATTERN.matcher(edit_url_mqtt?.text?.toString()?.trim()).matches()
    }

    override fun setFields(model: MqttPublishModel) {
        super.setFields(model)

        edit_url_mqtt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!validateMqttUrl()) {
                    edit_url_mqtt?.error = getString(R.string.mqtt_format)
                } else {
                    edit_url_mqtt?.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

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
    }

    override fun toPublishModel(): MqttPublishModel? {
        val name = edit_name.text.toString().trim()
        val url = edit_url_mqtt.text.toString().trim()
        val clientId = edit_clientid_mqtt.text.toString().trim()
        val username = edit_username_mqtt.text.toString().trim()
        val password = edit_password_mqtt.text.toString().trim()
        val topic = edit_topic_mqtt.text.toString().trim()
        val qos = findViewById<RadioButton>(
            radio_group_mqtt.checkedRadioButtonId
        ).text.toString().toInt()
        val timeType = spinner_interval_time.selectedItem.toString()
        val timeCount = edit_interval_count.text.toString()
        val datastring = edit_datastring.text.toString()

        if (viewModel.checkFieldsAreEmpty(
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

        val id = model?.id ?: 0
        val timeMillis = PublisherIntervalConverter.calculateMillis(this, timeCount, timeType)
        val lastTimeMillis = model?.lastTimeMillis ?: 0
        return MqttPublishModel(
            id,
            name,
            url,
            clientId,
            username,
            password,
            topic,
            qos,
            model?.enabled ?: true,
            timeType,
            timeMillis,
            lastTimeMillis,
            datastring
        )
    }

    override fun getPublisherForModel(model: MqttPublishModel): Publisher<*> {
        return MqttPublisher(
            applicationContext,
            MqttPublishModelDataMapper().toMqttPublish(model),
            listOf(Device("TestDevice", "Name", "Mac")),
            syncRepository
        )
    }


    companion object {
        private const val VALID_MQTT_URL_REGEX: String =
            "(?:(?:tcp)|(?:ws)|(?:wss)):\\/\\/(?:(?:(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))|(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])):\\d+(?:\\/[A-Za-z0-9/]*)?"
        val VALID_MQTT_URL_PATTERN: Pattern = Pattern.compile(VALID_MQTT_URL_REGEX)

        fun start(context: Context, id: Long? = null) {
            val intent = Intent(context, MqttPublisherActivity::class.java)

            id?.let {
                intent.putExtra(
                    PUBLISHER_ID_KEY,
                    id
                )
            }

            context.startActivity(intent)
        }
    }
}
