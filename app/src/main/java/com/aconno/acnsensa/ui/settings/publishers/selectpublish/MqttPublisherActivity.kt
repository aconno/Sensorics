package com.aconno.acnsensa.ui.settings.publishers.selectpublish

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.R
import com.aconno.acnsensa.dagger.mqttpublisher.DaggerMqttPublisherComponent
import com.aconno.acnsensa.dagger.mqttpublisher.MqttPublisherComponent
import com.aconno.acnsensa.dagger.mqttpublisher.MqttPublisherModule
import com.aconno.acnsensa.data.converter.PublisherIntervalConverter
import com.aconno.acnsensa.data.publisher.MqttPublisher
import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.model.DeviceRelationModel
import com.aconno.acnsensa.model.MqttPublishModel
import com.aconno.acnsensa.model.mapper.MqttPublishModelDataMapper
import com.aconno.acnsensa.ui.base.BaseActivity
import com.aconno.acnsensa.viewmodel.MqttPublisherViewModel
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_mqtt_publisher.*
import kotlinx.android.synthetic.main.layout_mqtt.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MqttPublisherActivity : BaseActivity() {

    @Inject
    lateinit var mqttPublisherViewModel: MqttPublisherViewModel

    private var mqttPublishModel: MqttPublishModel? = null
    private lateinit var deviceList: List<DeviceRelationModel>
    private var isTestingAlreadyRunning: Boolean = false

    private val testConnectionCallback = object : Publisher.TestConnectionCallback {
        override fun onConnectionStart() {
            progressbar.visibility = View.INVISIBLE
            isTestingAlreadyRunning = false
            Toast.makeText(
                this@MqttPublisherActivity,
                getString(R.string.test_succeeded),
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onConnectionSuccess() {
            progressbar.visibility = View.INVISIBLE
            isTestingAlreadyRunning = false
            Toast.makeText(
                this@MqttPublisherActivity,
                getString(R.string.test_failed),
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onConnectionFail() {
            progressbar.visibility = View.VISIBLE
        }
    }

    private val mqttPublisherComponent: MqttPublisherComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? = application as? AcnSensaApplication

        DaggerMqttPublisherComponent
            .builder()
            .appComponent(acnSensaApplication?.appComponent)
            .mqttPublisherModule(MqttPublisherModule(this))
            .build()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mqtt_publisher)
        mqttPublisherComponent.inject(this)

        setSupportActionBar(custom_toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)

        initViews()
        if (intent.hasExtra(MQTT_PUBLISHER_ACTIVITY_KEY)) {
            mqttPublishModel =
                    intent.getParcelableExtra(MQTT_PUBLISHER_ACTIVITY_KEY)
            setFields()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.add_publish_menu, menu)

        if (menu != null) {
            val item = menu.findItem(R.id.action_publish_done)
            if (mqttPublishModel != null) {
                item.title = getString(R.string.update)
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id: Int? = item?.itemId
        when (id) {
            R.id.action_publish_done -> addOrUpdate()
            R.id.action_publish_test -> test()
            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initViews() {
        btn_info.setOnClickListener {
            val builder = AlertDialog.Builder(this)

            builder.setTitle(R.string.publisher_info_title)
                .setMessage(R.string.publisher_info_text)
                .setNeutralButton(
                    R.string.okey
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        //Load Devices
        val subscribe = mqttPublisherViewModel.getAllDevices()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer {
                deviceList = it!!
                addDevices(deviceList)

                if (mqttPublishModel != null) {
                    addDisposable(
                        mqttPublisherViewModel.getDevicesThatConnectedWithMqttPublish(
                            mqttPublishModel!!.id
                        )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(Consumer {
                                updateDeviceList(it)
                            })
                    )
                }
            })
        addDisposable(subscribe)
    }

    private fun addDevices(deviceList: List<DeviceRelationModel>) {
        layout_devices.removeAllViews()

        deviceList.forEach {
            layout_devices.addView(getDeviceView(it))
        }
    }

    private fun getDeviceView(it: DeviceRelationModel): View? {
        val inflatedView =
            layoutInflater.inflate(R.layout.item_device_switch, layout_devices, false)

        val nameView = inflatedView.findViewById<TextView>(R.id.name)
        val macAddressView = inflatedView.findViewById<TextView>(R.id.mac_address)
        val switchView = inflatedView.findViewById<Switch>(R.id.switch_device)

        nameView.text = it.name
        macAddressView.text = it.macAddress
        switchView.isChecked = it.related

        return inflatedView
    }

    private fun updateDeviceList(list: MutableList<DeviceRelationModel>) {
        if (list.size == 0) return

        deviceList.forEachIndexed { index, it ->
            list.forEach { related ->
                if (it.macAddress == related.macAddress) {
                    it.related = true
                    updateDeviceViewAt(index)
                    return@forEach
                }
            }
        }
    }

    private fun updateDeviceViewAt(index: Int) {
        val childAt = layout_devices.getChildAt(index)
        childAt.findViewById<Switch>(R.id.switch_device)
            .isChecked = true
    }

    private fun setFields() {
        edit_name.setText(mqttPublishModel?.name)

        edit_interval_count.setText(
            PublisherIntervalConverter.calculateCountFromMillis(
                mqttPublishModel!!.timeMillis,
                mqttPublishModel!!.timeType
            )
        )

        spinner_interval_time.setSelection(
            resources.getStringArray(R.array.PublishIntervals).indexOf(
                mqttPublishModel?.timeType
            )
        )

        if (mqttPublishModel!!.lastTimeMillis == 0L) {
            text_lastdatasent.visibility = View.GONE
        } else {
            text_lastdatasent.visibility = View.VISIBLE
            val str = getString(R.string.last_data_sent) + " " +
                    millisToFormattedDateString(
                        mqttPublishModel!!.lastTimeMillis
                    )
            text_lastdatasent.text = str
        }


        edit_url_mqtt.setText(mqttPublishModel!!.url)
        edit_clientid_mqtt.setText(mqttPublishModel!!.clientId)
        edit_username_mqtt.setText(mqttPublishModel!!.username)
        edit_password_mqtt.setText(mqttPublishModel!!.password)
        edit_topic_mqtt.setText(mqttPublishModel!!.topic)

        when (mqttPublishModel!!.qos) {
            0 -> qos_0.isChecked = true
            1 -> qos_1.isChecked = true
            2 -> qos_2.isChecked = true
        }

        edit_datastring.setText(mqttPublishModel?.dataString)
    }

    private fun millisToFormattedDateString(millis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss,SSS", Locale.US)
        val date = Date(millis)

        return sdf.format(date)
    }

    private fun addOrUpdate() {
        val mqttPublishModel = toMqttPublishModel()
        if (mqttPublishModel != null) {
            mqttPublisherViewModel.save(mqttPublishModel)
                .flatMapCompletable {
                    addRelationsToMqtt(it)
                }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        progressbar.visibility = View.INVISIBLE
                        finish()
                    }

                    override fun onSubscribe(d: Disposable) {
                        addDisposable(d)
                        progressbar.visibility = View.VISIBLE
                    }

                    override fun onError(e: Throwable) {
                        progressbar.visibility = View.INVISIBLE
                        Toast.makeText(
                            this@MqttPublisherActivity,
                            e.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                })
        }
    }

    private fun addRelationsToMqtt(it: Long): Completable {
        val count = layout_devices.childCount

        val setOfCompletable = mutableSetOf<Completable>()

        for (i in 0..(count - 1)) {
            val deviceRelationModel = deviceList[i]

            val isChecked =
                layout_devices.getChildAt(i).findViewById<Switch>(R.id.switch_device).isChecked

            if (isChecked) {
                setOfCompletable.add(
                    mqttPublisherViewModel.addOrUpdateMqttRelation(
                        deviceId = deviceRelationModel.macAddress,
                        mqttId = it
                    )
                )

            } else {
                setOfCompletable.add(
                    mqttPublisherViewModel.deleteRelationMqtt(
                        deviceId = deviceRelationModel.macAddress,
                        mqttId = it
                    )
                )
            }
        }

        return Completable.merge(setOfCompletable)
    }

    private fun toMqttPublishModel(): MqttPublishModel? {
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
            Toast.makeText(this, getString(R.string.please_fill_blanks), Toast.LENGTH_SHORT).show()
            return null
        }

        val id = if (mqttPublishModel == null) 0 else mqttPublishModel!!.id
        val timeMillis = PublisherIntervalConverter.calculateMillis(timeCount, timeType)
        val lastTimeMillis = if (mqttPublishModel == null) 0 else mqttPublishModel!!.lastTimeMillis
        return MqttPublishModel(
            id,
            name,
            url,
            clientId,
            username,
            password,
            topic,
            qos,
            false,
            timeType,
            timeMillis,
            lastTimeMillis,
            datastring
        )
    }

    private fun test() {
        if (!isTestingAlreadyRunning) {
            isTestingAlreadyRunning = true

            Toast.makeText(this, getString(R.string.testings_started), Toast.LENGTH_SHORT).show()

            val toMqttPublishModel = toMqttPublishModel()

            if (toMqttPublishModel == null) {
                isTestingAlreadyRunning = false
                return
            }

            testMqttConnection(toMqttPublishModel)
        }

    }

    private fun testMqttConnection(toMqttPublishModel: MqttPublishModel) {
        val publisher = MqttPublisher(
            applicationContext,
            MqttPublishModelDataMapper().toMqttPublish(toMqttPublishModel),
            listOf(Device("TestDevice", "Mac"))
        )

        testConnectionCallback.onConnectionStart()
        publisher.test(testConnectionCallback)
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
