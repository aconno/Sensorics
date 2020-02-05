package com.aconno.sensorics.ui.settings.virtualscanningsources

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt.MqttVirtualScanningSourceProtocol
import com.aconno.sensorics.domain.mqtt.MqttVirtualScanner
import com.aconno.sensorics.model.MqttVirtualScanningSourceModel
import com.aconno.sensorics.model.mapper.MqttVirtualScanningSourceModelDataMapper
import com.aconno.sensorics.ui.base.BaseActivity
import com.aconno.sensorics.viewmodel.MqttVirtualScanningSourceViewModel
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_mqtt_virtual_scanning_source.*
import kotlinx.android.synthetic.main.mqtt_virtual_scanning_source_form.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class MqttVirtualScanningSourceActivity : BaseActivity() {
    private var isTestingAlreadyRunning = false

    @Inject
    lateinit var mqttSourceViewModel: MqttVirtualScanningSourceViewModel
    private var mqttVirtualScanningSourceModel: MqttVirtualScanningSourceModel? = null
    @Inject
    lateinit var mqttMapper : MqttVirtualScanningSourceModelDataMapper
    @Inject
    lateinit var mqttVirtualScanner: MqttVirtualScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mqtt_virtual_scanning_source)

        setSupportActionBar(mqtt_source_toolbar)

        if (intent.hasExtra(MQTT_VIRTUAL_SCANNING_SOURCE_ACTIVITY_KEY)) {
            mqttVirtualScanningSourceModel =
                    intent.getParcelableExtra(MQTT_VIRTUAL_SCANNING_SOURCE_ACTIVITY_KEY)
            setFields()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.add_virtual_scanning_source_menu, menu)

        if (menu != null) {
            val item = menu.findItem(R.id.add_virtual_scanning_source_action)
            if (mqttVirtualScanningSourceModel != null) {
                item.title = getString(R.string.update)
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.add_virtual_scanning_source_action -> addOrUpdate()
            R.id.test_virtual_scanning_source_action -> testSource()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    private fun testSource() {
        if (!isTestingAlreadyRunning) {
            isTestingAlreadyRunning = true

            val model = toMqttVirtualScanningSourceModel()

            if (model == null) {
                isTestingAlreadyRunning = false
                return
            }

            testConnection(model)
        }
    }



    private fun setProgressComponentsVisibility(visibility : Int) {
        progress_bar.visibility = visibility
        progress_bar_message.visibility = visibility
    }


    private val testConnectionCallback = object : MqttVirtualScanner.TestConnectionCallback {
        override fun onConnectionStart() {
            GlobalScope.launch(Dispatchers.Main) {
                setProgressComponentsVisibility(View.VISIBLE)
                isTestingAlreadyRunning = false
            }
        }

        override fun onConnectionSuccess() {
            GlobalScope.launch(Dispatchers.Main) {
                setProgressComponentsVisibility(View.INVISIBLE)
                isTestingAlreadyRunning = false
                Toast.makeText(
                        this@MqttVirtualScanningSourceActivity,
                        getString(R.string.test_succeeded),
                        Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onConnectionFail(exception: Throwable?) {
            GlobalScope.launch(Dispatchers.Main) {
                setProgressComponentsVisibility(View.INVISIBLE)
                isTestingAlreadyRunning = false

                Toast.makeText(
                        this@MqttVirtualScanningSourceActivity,
                        getString(R.string.test_failed),
                        Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun testConnection(model: MqttVirtualScanningSourceModel) {
        GlobalScope.launch(Dispatchers.Default) {
            val scanningSource = mqttMapper
                    .toMqttVirtualScanningSource(model)

            testConnectionCallback.onConnectionStart()

            mqttVirtualScanner.testConnection(testConnectionCallback,scanningSource)

        }
    }


    private fun addOrUpdate() {
        val model = toMqttVirtualScanningSourceModel()
        if(model != null) {
            mqttSourceViewModel.save(model)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<Long> {
                        override fun onSuccess(t: Long) {
                            progress_bar.visibility = View.INVISIBLE
                            finish()
                        }

                        override fun onSubscribe(d: Disposable) {
                            addDisposable(d)
                            progress_bar.visibility = View.VISIBLE
                        }

                        override fun onError(e: Throwable) {
                            progress_bar.visibility = View.INVISIBLE
                            Toast.makeText(
                                    this@MqttVirtualScanningSourceActivity,
                                    e.message,
                                    Toast.LENGTH_SHORT
                            )
                                    .show()
                        }
                    })
        }
    }

    private fun setFields() {
        mqttVirtualScanningSourceModel?.let { model ->
            mqtt_virtual_scanning_source_name.setText(model.name)

            when(model.protocol) {
                MqttVirtualScanningSourceProtocol.TCP -> radio_group_protocol.check(protocol_tcp.id)
                MqttVirtualScanningSourceProtocol.WEBSOCKET -> radio_group_protocol.check(protocol_websocket.id)
            }

            mqtt_source_address_input.setText(model.address)
            mqtt_source_port_input.setText(model.port.toString())
            mqtt_source_path_input.setText(model.path)
            edit_clientid_mqtt.setText(model.clientId)
            edit_username_mqtt.setText(model.username)
            edit_password_mqtt.setText(model.password)

            when (model.qualityOfService) {
                0 -> qos_0.isChecked = true
                1 -> qos_1.isChecked = true
                2 -> qos_2.isChecked = true
            }

        }
    }

    private fun toMqttVirtualScanningSourceModel(): MqttVirtualScanningSourceModel? {
        val name = mqtt_virtual_scanning_source_name.text.toString().trim()

        val protocol = when(radio_group_protocol.checkedRadioButtonId) {
            protocol_tcp.id -> MqttVirtualScanningSourceProtocol.TCP
            protocol_websocket.id -> MqttVirtualScanningSourceProtocol.WEBSOCKET
            else -> return null

        }

        val address = mqtt_source_address_input.text.toString().trim()
        val port = mqtt_source_port_input.text.toString().trim()
        val path = mqtt_source_path_input.text.toString().trim()
        val clientId = edit_clientid_mqtt.text.toString().trim()
        val username = edit_username_mqtt.text.toString().trim()
        val password = edit_password_mqtt.text.toString().trim()
        val qos = when(radio_group_mqtt_qos.checkedRadioButtonId) {
            qos_0.id -> 0
            qos_1.id -> 1
            qos_2.id -> 2
            else -> return null
        }

        if (mqttSourceViewModel.checkFieldsAreEmpty(
                        name,
                        address,
                        port,
                        clientId
                )
        ) {
            Toast.makeText(
                    this,
                    getString(R.string.please_fill_blanks),
                    Toast.LENGTH_SHORT
            ).show()
            return null
        }

        val id = if (mqttVirtualScanningSourceModel == null) 0 else mqttVirtualScanningSourceModel!!.id
        val enabled = mqttVirtualScanningSourceModel?.enabled != false

        return MqttVirtualScanningSourceModel(
                id,
                name,
                enabled,
                protocol,
                address,
                port.toInt(),
                path,
                clientId,
                username,
                password,
                qos
        )
    }



    companion object {
        private const val MQTT_VIRTUAL_SCANNING_SOURCE_ACTIVITY_KEY = "MQTT_VIRTUAL_SCANNING_SOURCE_ACTIVITY_KEY"

        fun start(context: Context, mqttVirtualScanningSourceModel: MqttVirtualScanningSourceModel? = null) {
            val intent = Intent(context, MqttVirtualScanningSourceActivity::class.java)

            mqttVirtualScanningSourceModel?.let {
                intent.putExtra(
                        MQTT_VIRTUAL_SCANNING_SOURCE_ACTIVITY_KEY,
                        mqttVirtualScanningSourceModel
                )
            }

            context.startActivity(intent)
        }
    }
}
