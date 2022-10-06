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
import com.aconno.sensorics.databinding.ActivityMqttPublisherBinding
import com.aconno.sensorics.databinding.LayoutDatastringBinding
import com.aconno.sensorics.databinding.LayoutMqttBinding
import com.aconno.sensorics.databinding.LayoutPublisherHeaderBinding
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.model.MqttPublishModel
import com.aconno.sensorics.model.mapper.MqttPublishModelDataMapper
import com.aconno.sensorics.viewmodel.MqttPublisherViewModel
import com.aconno.sensorics.viewmodel.PublisherViewModel
import java.util.regex.Pattern
import javax.inject.Inject

class MqttPublisherActivity : BasePublisherActivity<MqttPublishModel>() {

    private lateinit var binding: ActivityMqttPublisherBinding
    private lateinit var layoutDatastringBinding: LayoutDatastringBinding
    private lateinit var layoutMqttBinding: LayoutMqttBinding
    private lateinit var layoutPublisherHeaderBinding: LayoutPublisherHeaderBinding

    @Inject
    lateinit var mqttPublisherViewModel: MqttPublisherViewModel

    override val viewModel: PublisherViewModel<MqttPublishModel>
        get() = mqttPublisherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityMqttPublisherBinding.inflate(layoutInflater)
        layoutDatastringBinding = LayoutDatastringBinding.inflate(layoutInflater)
        layoutMqttBinding = LayoutMqttBinding.inflate(layoutInflater)
        layoutPublisherHeaderBinding = LayoutPublisherHeaderBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.customToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        super.onCreate(savedInstanceState)
    }

    override fun onTestConnectionSuccess() {
        layoutMqttBinding.editUrlMqtt.error = null
    }

    override fun onTestConnectionFail(exception: Throwable?) {
        exception?.message?.let { m ->
            if (m.contains(layoutMqttBinding.editUrlMqtt.text.toString())) {
                layoutMqttBinding.editUrlMqtt.error = getString(R.string.mqtt_format)
            } else {
                layoutMqttBinding.editUrlMqtt.error = null
            }
        }
    }

    fun validateMqttUrl(): Boolean {
        return layoutMqttBinding.editUrlMqtt.text?.toString()?.trim()?.let { text ->
            VALID_MQTT_URL_PATTERN.matcher(text).takeIf {
                it.matches()
            }?.toMatchResult()?.let { result ->
                result.group(0) == text
            }
        } ?: false
    }

    override fun setFields(model: MqttPublishModel) {
        super.setFields(model)

        layoutMqttBinding.editUrlMqtt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!validateMqttUrl()) {
                    layoutMqttBinding.editUrlMqtt.error = getString(R.string.mqtt_format)
                } else {
                    layoutMqttBinding.editUrlMqtt.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        layoutMqttBinding.editUrlMqtt.setText(model.url)
        layoutMqttBinding.editClientIdMqtt.setText(model.clientId)
        layoutMqttBinding.editUsernameMqtt.setText(model.username)
        layoutMqttBinding.editPasswordMqtt.setText(model.password)
        layoutMqttBinding.editTopicMqtt.setText(model.topic)

        when (model.qos) {
            0 -> layoutMqttBinding.qos0.isChecked = true
            1 -> layoutMqttBinding.qos1.isChecked = true
            2 -> layoutMqttBinding.qos2.isChecked = true
        }
    }

    override fun toPublishModel(): MqttPublishModel? {
        val name = layoutPublisherHeaderBinding.editName.text.toString().trim()
        val url = layoutMqttBinding.editUrlMqtt.text.toString().trim()
        val clientId = layoutMqttBinding.editClientIdMqtt.text.toString().trim()
        val username = layoutMqttBinding.editUsernameMqtt.text.toString().trim()
        val password = layoutMqttBinding.editPasswordMqtt.text.toString().trim()
        val topic = layoutMqttBinding.editTopicMqtt.text.toString().trim()
        val qos = findViewById<RadioButton>(
            layoutMqttBinding.radioGroupMqtt.checkedRadioButtonId
        ).text.toString().toInt()
        val timeType = layoutPublisherHeaderBinding.spinnerIntervalTime.selectedItem.toString()
        val timeCount = layoutPublisherHeaderBinding.editIntervalCount.text.toString()
        val datastring = layoutDatastringBinding.editDatastring.text.toString()

        if (viewModel.checkFieldsAreEmpty(
                name,
                url,
                clientId,
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
            "(?:(?:tcp)|(?:ws)|(?:wss)):\\/\\/(?:(?:(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))|(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9]))+(:[0-9]+)?(?:\\/[A-Za-z0-9/]*)?"
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
