package com.aconno.sensorics.ui.settings.publishers.selectpublish

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.aconno.sensorics.PublisherIntervalConverter
import com.aconno.sensorics.R
import com.aconno.sensorics.data.publisher.AzureMqttPublisher
import com.aconno.sensorics.databinding.*
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.model.AzureMqttPublishModel
import com.aconno.sensorics.model.mapper.AzureMqttPublishModelDataMapper
import com.aconno.sensorics.viewmodel.AzureMqttPublisherViewModel
import com.aconno.sensorics.viewmodel.PublisherViewModel
import javax.inject.Inject

class AzureMqttPublisherActivity : BasePublisherActivity<AzureMqttPublishModel>() {

    private lateinit var binding: ActivityAzureMqttPublisherBinding
    private lateinit var layoutAzureBinding: LayoutAzureMqttBinding
    private lateinit var layoutDatastringBinding: LayoutDatastringBinding
    private lateinit var layoutPublisherBinding: LayoutPublisherHeaderBinding

    @Inject
    lateinit var azureMqttPublisherViewModel: AzureMqttPublisherViewModel

    override val viewModel: PublisherViewModel<AzureMqttPublishModel>
        get() = azureMqttPublisherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityAzureMqttPublisherBinding.inflate(layoutInflater)
        layoutAzureBinding = LayoutAzureMqttBinding.inflate(layoutInflater)
        layoutDatastringBinding = LayoutDatastringBinding.inflate(layoutInflater)
        layoutPublisherBinding = LayoutPublisherHeaderBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.customToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        initViews()
        super.onCreate(savedInstanceState)
    }

    override fun initViews() {
        super.initViews()

        layoutAzureBinding.iotCredentialsInfo.setOnClickListener {
            createAndShowInfoDialog(R.string.iot_hub_info_title, R.string.iot_hub_info_text)
        }
    }

    override fun setFields(model: AzureMqttPublishModel) {
        super.setFields(model)

        layoutAzureBinding.editIotHubName.setText(model.iotHubName)
        layoutAzureBinding.editDeviceId.setText(model.deviceId)
        layoutAzureBinding.editSharedAccessKey.setText(model.sharedAccessKey)
        layoutDatastringBinding.editDatastring.setText(model.dataString)
    }

    override fun toPublishModel(): AzureMqttPublishModel? {
        val name = layoutPublisherBinding.editName.text.toString().trim()
        val iotHubName = layoutAzureBinding.editIotHubName.text.toString().trim()
        val deviceId = layoutAzureBinding.editDeviceId.text.toString().trim()
        val sharedAccessKey = layoutAzureBinding.editSharedAccessKey.text.toString().trim()
        val timeType = layoutPublisherBinding.spinnerIntervalTime.selectedItem.toString()
        val timeCount = layoutPublisherBinding.editIntervalCount.text.toString()
        val datastring = layoutDatastringBinding.editDatastring.text.toString()

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
