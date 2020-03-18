package com.aconno.sensorics.ui.settings.publishers.selectpublish

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import com.aconno.sensorics.PublisherIntervalConverter
import com.aconno.sensorics.R
import com.aconno.sensorics.data.publisher.AzureMqttPublisher
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.model.AzureMqttPublishModel
import com.aconno.sensorics.model.BasePublishModel
import com.aconno.sensorics.model.mapper.AzureMqttPublishModelDataMapper
import com.aconno.sensorics.viewmodel.AzureMqttPublisherViewModel
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_azure_mqtt_publisher.*
import kotlinx.android.synthetic.main.layout_azure_mqtt.*
import kotlinx.android.synthetic.main.layout_datastring.*
import kotlinx.android.synthetic.main.layout_publisher_header.*
import javax.inject.Inject

class AzureMqttPublisherActivity : BaseMqttPublisherActivity<AzureMqttPublishModel>() {
    @Inject
    lateinit var azureMqttPublisherViewModel: AzureMqttPublisherViewModel

    override var progressBar: ProgressBar
        get() = progressbar
        set(_) {}

    override var deviceSelectFrameId: Int = R.id.devices_frame
    override var layoutId: Int = R.layout.activity_azure_mqtt_publisher
    override var publishModel: AzureMqttPublishModel? = null
    override var publisherKey: String = AZURE_MQTT_PUBLISHER_ACTIVITY_KEY
    override var updating: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (publishModel != null) {
            updating = true
        }
    }

    override fun initViews() {
        super.initViews()
        iot_credentials_info.setOnClickListener {
            createAndShowInfoDialog(R.string.iot_hub_info_text, R.string.iot_hub_info_title)
        }
    }


    override fun addOrUpdateRelation(deviceId: String, publisherId: Long): Completable {
        return azureMqttPublisherViewModel.addOrUpdatePublisherDeviceRelation(
            deviceId = deviceId,
            publisherId = publisherId
        )
    }

    override fun deleteRelation(deviceId: String, publisherId: Long): Completable {
        return azureMqttPublisherViewModel.deletePublishDeviceRelation(deviceId, publisherId)
    }

    override fun getPublisherFor(publishModel: AzureMqttPublishModel): Publisher {
        return AzureMqttPublisher(
            AzureMqttPublishModelDataMapper().toAzureMqttPublish(publishModel),
            listOf(Device("TestDevice", "Name", "Mac")),
            syncRepository
        )
    }

    override fun onTestConnectionFail(exception: Throwable?) {}

    override fun onTestConnectionSuccess() {}

    override fun savePublisher(publishModel: BasePublishModel): Single<Long> {
        return azureMqttPublisherViewModel.save(publishModel as AzureMqttPublishModel)
    }

    override fun setPublisherSpecificFields() {
        publishModel?.let { model ->
            edit_iot_hub_name.setText(model.iotHubName)
            edit_device_id.setText(model.deviceId)
            edit_shared_access_key.setText(model.sharedAccessKey)
            edit_datastring.setText(model.dataString)
        }
    }

    override fun toPublishModel(): AzureMqttPublishModel? {
        val name = edit_name.text.toString().trim()
        val iotHubName = edit_iot_hub_name.text.toString().trim()
        val deviceId = edit_device_id.text.toString().trim()
        val sharedAccessKey = edit_shared_access_key.text.toString().trim()
        val timeType = spinner_interval_time.selectedItem.toString()
        val timeCount = edit_interval_count.text.toString()
        val datastring = edit_datastring.text.toString()

        if (azureMqttPublisherViewModel.checkFieldsAreEmpty(
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

        val id = if (publishModel == null) 0 else publishModel!!.id
        val timeMillis = PublisherIntervalConverter.calculateMillis(this, timeCount, timeType)
        val lastTimeMillis = if (publishModel == null) 0 else publishModel!!.lastTimeMillis
        return AzureMqttPublishModel(
            id,
            name,
            iotHubName,
            deviceId,
            sharedAccessKey,
            publishModel?.enabled ?: true,
            timeType,
            timeMillis,
            lastTimeMillis,
            datastring
        )
    }


    companion object {
        private const val AZURE_MQTT_PUBLISHER_ACTIVITY_KEY = "AZURE_MQTT_PUBLISHER_ACTIVITY_KEY"

        fun start(context: Context, azureMqttPublishModel: AzureMqttPublishModel? = null) {
            val intent = Intent(context, AzureMqttPublisherActivity::class.java)

            azureMqttPublishModel?.let {
                intent.putExtra(
                    AZURE_MQTT_PUBLISHER_ACTIVITY_KEY,
                    azureMqttPublishModel
                )
            }

            context.startActivity(intent)
        }
    }
}
