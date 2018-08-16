package com.aconno.sensorics.ui.settings.publishers.selectpublish

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.R
import com.aconno.sensorics.dagger.mqttpublisher.DaggerMqttPublisherComponent
import com.aconno.sensorics.dagger.mqttpublisher.MqttPublisherComponent
import com.aconno.sensorics.dagger.mqttpublisher.MqttPublisherModule
import com.aconno.sensorics.data.converter.NewDataStringConverter
import com.aconno.sensorics.data.converter.PublisherIntervalConverter
import com.aconno.sensorics.data.publisher.MqttPublisher
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.model.MqttPublishModel
import com.aconno.sensorics.model.mapper.MqttPublishModelDataMapper
import com.aconno.sensorics.ui.base.BaseActivity
import com.aconno.sensorics.ui.settings.publishers.DeviceSelectFragment
import com.aconno.sensorics.viewmodel.MqttPublisherViewModel
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_mqtt_publisher.*
import kotlinx.android.synthetic.main.layout_datastring.*
import kotlinx.android.synthetic.main.layout_mqtt.*
import kotlinx.android.synthetic.main.layout_publisher_header.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MqttPublisherActivity : BaseActivity() {

    @Inject
    lateinit var mqttPublisherViewModel: MqttPublisherViewModel

    private var mqttPublishModel: MqttPublishModel? = null
    private var isTestingAlreadyRunning: Boolean = false

    private val testConnectionCallback = object : Publisher.TestConnectionCallback {
        override fun onConnectionStart() {
            progressbar.visibility = View.VISIBLE
            isTestingAlreadyRunning = false
            Toast.makeText(
                this@MqttPublisherActivity,
                getString(R.string.testings_started),
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onConnectionSuccess() {
            progressbar.visibility = View.INVISIBLE
            isTestingAlreadyRunning = false
            Toast.makeText(
                this@MqttPublisherActivity,
                getString(R.string.test_succeeded),
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onConnectionFail() {
            progressbar.visibility = View.INVISIBLE
            isTestingAlreadyRunning = false
            Toast.makeText(
                this@MqttPublisherActivity,
                getString(R.string.test_failed),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val mqttPublisherComponent: MqttPublisherComponent by lazy {
        val sensoricsApplication: SensoricsApplication? = application as? SensoricsApplication

        DaggerMqttPublisherComponent
            .builder()
            .appComponent(sensoricsApplication?.appComponent)
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

        val fragment = DeviceSelectFragment.newInstance(mqttPublishModel)
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, fragment)
            .commit()
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
            createAndShowInfoDialog()
        }
    }

    private fun createAndShowInfoDialog() {
        val view = View.inflate(this, R.layout.dialog_alert, null)
        val textView = view.findViewById<TextView>(R.id.message)
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setText(R.string.publisher_info_text)

        val builder = AlertDialog.Builder(this)

        builder.setTitle(R.string.publisher_info_title)
            .setView(view)
            .setNeutralButton(
                R.string.close
            ) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
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

    private fun addRelationsToMqtt(mId: Long): Completable? {
        val fragment = supportFragmentManager.findFragmentById(R.id.frame) as DeviceSelectFragment
        val devices = fragment.getDevices()

        val setOfCompletable: MutableSet<Completable> = mutableSetOf()

        devices.forEach {
            val completable = if (it.related) {
                mqttPublisherViewModel.addOrUpdateMqttRelation(
                    deviceId = it.macAddress,
                    mqttId = mId
                )
            } else {
                mqttPublisherViewModel.deleteRelationMqtt(
                    deviceId = it.macAddress,
                    mqttId = mId
                )
            }

            setOfCompletable.add(
                completable
            )
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

    private fun isDataStringValid(): Boolean {

        val converter = NewDataStringConverter()

        val dataString = edit_datastring.text.toString()
        return converter.parseAndValidateDataString(dataString)
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
            listOf(Device("TestDevice", "Name", "Mac"))
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
