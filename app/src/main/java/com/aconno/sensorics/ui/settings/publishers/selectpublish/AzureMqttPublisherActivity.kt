package com.aconno.sensorics.ui.settings.publishers.selectpublish

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.widget.ProgressBar
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.model.AzureMqttPublishModel
import com.aconno.sensorics.model.BasePublishModel
import com.aconno.sensorics.viewmodel.AzureMqttPublisherViewModel
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_azure_mqtt_publisher.*
import javax.inject.Inject

class AzureMqttPublisherActivity : BaseMqttPublisherActivity<AzureMqttPublishModel>() { //TODO: promijenit u AzureMqttPublishModel
    @Inject
    lateinit var azureMqttPublisherViewModel: AzureMqttPublisherViewModel

    private var azureMqttPublishModel: AzureMqttPublishModel? = null

    override var progressBar: ProgressBar
        get() = progressbar
        set(_) {}

    override var deviceSelectFrameId: Int = R.id.devices_frame
    override var layoutId: Int = R.layout.activity_azure_mqtt_publisher
    override var publishModel: AzureMqttPublishModel?
        get() = azureMqttPublishModel
        set(value) { azureMqttPublishModel = value}
    override var publisherKey: String = AZURE_MQTT_PUBLISHER_ACTIVITY_KEY
    override var updating: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(publishModel != null) {
            updating = true
        }
    }

    override fun addOrUpdateRelation(deviceId: String, publisherId: Long): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteRelation(deviceId: String, publisherId: Long): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPublisherFor(publishModel: AzureMqttPublishModel): Publisher {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTestConnectionFail(exception: Throwable?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTestConnectionSuccess() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun savePublisher(publishModel: BasePublishModel): Single<Long> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPublisherSpecificFields() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toPublishModel(): AzureMqttPublishModel? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



    companion object {
        private const val AZURE_MQTT_PUBLISHER_ACTIVITY_KEY = "AZURE_MQTT_PUBLISHER_ACTIVITY_KEY"

        fun start(context: Context, azureMqttPublishModel: Parcelable? = null) { //todo umjesto object stavit AzureMqttPublishModel
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
