package com.aconno.sensorics.ui.settings.publishers.selectpublish

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.aconno.sensorics.PublisherIntervalConverter
import com.aconno.sensorics.R
import com.aconno.sensorics.data.publisher.AzureMqttPublisher
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.model.AzureMqttPublishModel
import com.aconno.sensorics.model.mapper.AzureMqttPublishModelDataMapper
import com.aconno.sensorics.viewmodel.AzureMqttPublisherViewModel
import com.aconno.sensorics.viewmodel.PublisherViewModel
import kotlinx.android.synthetic.main.activity_azure_mqtt_publisher.*
import kotlinx.android.synthetic.main.layout_azure_mqtt.*
import kotlinx.android.synthetic.main.layout_datastring.*
import kotlinx.android.synthetic.main.layout_publisher_header.*
import javax.inject.Inject

class AzureMqttPublisherActivity : BasePublisherActivity<AzureMqttPublishModel>() {
    @Inject
    lateinit var azureMqttPublisherViewModel: AzureMqttPublisherViewModel

    override val viewModel: PublisherViewModel<AzureMqttPublishModel>
        get() = azureMqttPublisherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_azure_mqtt_publisher)

        setSupportActionBar(custom_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        initViews()
        super.onCreate(savedInstanceState)
    }

    override fun initViews() {
        super.initViews()

        iot_credentials_info.setOnClickListener {
            createAndShowInfoDialog(R.string.iot_hub_info_title, R.string.iot_hub_info_text)
        }
    }

    override fun setFields(model: AzureMqttPublishModel) {
        super.setFields(model)

        edit_iot_hub_name.setText(model.iotHubName)
        edit_device_id.setText(model.deviceId)
        edit_shared_access_key.setText(model.sharedAccessKey)
        edit_datastring.setText(model.dataString)
    }

    override fun toPublishModel(): AzureMqttPublishModel? {
        val name = edit_name.text.toString().trim()
        val iotHubName = edit_iot_hub_name.text.toString().trim()
        val deviceId = edit_device_id.text.toString().trim()
        val sharedAccessKey = edit_shared_access_key.text.toString().trim()
        val timeType = spinner_interval_time.selectedItem.toString()
        val timeCount = edit_interval_count.text.toString()
        val datastring = edit_datastring.text.toString()

        if (viewModel.checkFieldsAreEmpty(
                name,
                iotHubName,
                deviceId,
                sharedAccessKey,
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
        return AzureMqttPublishModel(
            id,
            name,
            iotHubName,
            deviceId,
            sharedAccessKey,
            model?.enabled ?: true,
            timeType,
            timeMillis,
            lastTimeMillis,
            datastring
        )
    }

    override fun getPublisherForModel(model: AzureMqttPublishModel): Publisher<*> {
        return AzureMqttPublisher(
            AzureMqttPublishModelDataMapper().toAzureMqttPublish(model),
            listOf(Device("TestDevice", "Name", "Mac")),
            syncRepository
        )
    }


    companion object {
        fun start(context: Context, id: Long? = null) {
            val intent = Intent(context, AzureMqttPublisherActivity::class.java)

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
