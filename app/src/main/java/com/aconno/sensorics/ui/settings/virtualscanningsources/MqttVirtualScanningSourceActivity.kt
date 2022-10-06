package com.aconno.sensorics.ui.settings.virtualscanningsources

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.aconno.sensorics.R
import com.aconno.sensorics.databinding.ActivityMqttVirtualScanningSourceBinding
import com.aconno.sensorics.databinding.MqttVirtualScanningSourceFormBinding
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


class MqttVirtualScanningSourceActivity : BaseActivity() {

    private lateinit var binding: ActivityMqttVirtualScanningSourceBinding
    private lateinit var mqttVirtualScanningSourceBinding: MqttVirtualScanningSourceFormBinding

    private val maxPortNumber = 0xFFFF
    private var isTestingAlreadyRunning = false

    @Inject
    lateinit var mqttSourceViewModel: MqttVirtualScanningSourceViewModel
    private var mqttVirtualScanningSourceModel: MqttVirtualScanningSourceModel? = null

    @Inject
    lateinit var mqttMapper: MqttVirtualScanningSourceModelDataMapper

    @Inject
    lateinit var mqttVirtualScanner: MqttVirtualScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMqttVirtualScanningSourceBinding.inflate(layoutInflater)
        mqttVirtualScanningSourceBinding =
            MqttVirtualScanningSourceFormBinding.inflate(layoutInflater)

        setContentView(R.layout.activity_mqtt_virtual_scanning_source)

        setSupportActionBar(binding.mqttSourceToolbar)

        if (intent.hasExtra(MQTT_VIRTUAL_SCANNING_SOURCE_ACTIVITY_KEY)) {
            mqttVirtualScanningSourceModel =
                intent.getParcelableExtra(MQTT_VIRTUAL_SCANNING_SOURCE_ACTIVITY_KEY)
            setFields()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.add_virtual_scanning_source_menu, menu)

        val item = menu.findItem(R.id.add_virtual_scanning_source_action)
        if (mqttVirtualScanningSourceModel != null) {
            item.title = getString(R.string.update)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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


    private fun setProgressComponentsVisibility(visibility: Int) {
        binding.progressBar.visibility = visibility
        binding.progressBarMessage.visibility = visibility
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

            mqttVirtualScanner.testConnection(testConnectionCallback, scanningSource)

        }
    }


    private fun addOrUpdate() {
        val model = toMqttVirtualScanningSourceModel()
        if (model != null) {
            mqttSourceViewModel.save(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Long> {
                    override fun onSuccess(t: Long) {
                        binding.progressBar.visibility = View.INVISIBLE
                        finish()
                    }

                    override fun onSubscribe(d: Disposable) {
                        addDisposable(d)
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    override fun onError(e: Throwable) {
                        binding.progressBar.visibility = View.INVISIBLE
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
            mqttVirtualScanningSourceBinding.mqttVirtualScanningSourceName.setText(model.name)

            when (model.protocol) {
                MqttVirtualScanningSourceProtocol.TCP -> mqttVirtualScanningSourceBinding.radioGroupProtocol.check(
                    mqttVirtualScanningSourceBinding.protocolTcp.id
                )
                MqttVirtualScanningSourceProtocol.WEBSOCKET -> mqttVirtualScanningSourceBinding.radioGroupProtocol.check(
                    mqttVirtualScanningSourceBinding.protocolWebsocket.id
                )
            }

            mqttVirtualScanningSourceBinding.mqttSourceAddressInput.setText(model.address)
            mqttVirtualScanningSourceBinding.mqttSourcePortInput.setText(model.port.toString())
            mqttVirtualScanningSourceBinding.mqttSourcePathInput.setText(model.path)
            mqttVirtualScanningSourceBinding.editClientIdMqtt.setText(model.clientId)
            mqttVirtualScanningSourceBinding.editUsernameMqtt.setText(model.username)
            mqttVirtualScanningSourceBinding.editPasswordMqtt.setText(model.password)

            when (model.qualityOfService) {
                0 -> mqttVirtualScanningSourceBinding.qos0.isChecked = true
                1 -> mqttVirtualScanningSourceBinding.qos1.isChecked = true
                2 -> mqttVirtualScanningSourceBinding.qos2.isChecked = true
            }

        }
    }

    private fun toMqttVirtualScanningSourceModel(): MqttVirtualScanningSourceModel? {
        val name =
            mqttVirtualScanningSourceBinding.mqttVirtualScanningSourceName.text.toString().trim()

        val protocol =
            when (mqttVirtualScanningSourceBinding.radioGroupProtocol.checkedRadioButtonId) {
                mqttVirtualScanningSourceBinding.protocolTcp.id -> MqttVirtualScanningSourceProtocol.TCP
                mqttVirtualScanningSourceBinding.protocolWebsocket.id -> MqttVirtualScanningSourceProtocol.WEBSOCKET
                else -> return null

            }

        val address = mqttVirtualScanningSourceBinding.mqttSourceAddressInput.text.toString().trim()
        val port = mqttVirtualScanningSourceBinding.mqttSourcePortInput.text.toString().trim()
        val path = mqttVirtualScanningSourceBinding.mqttSourcePathInput.text.toString().trim()
        val clientId = mqttVirtualScanningSourceBinding.editClientIdMqtt.text.toString().trim()
        val username = mqttVirtualScanningSourceBinding.editUsernameMqtt.text.toString().trim()
        val password = mqttVirtualScanningSourceBinding.editPasswordMqtt.text.toString().trim()
        val qos = when (mqttVirtualScanningSourceBinding.radioGroupMqttQos.checkedRadioButtonId) {
            mqttVirtualScanningSourceBinding.qos0.id -> 0
            mqttVirtualScanningSourceBinding.qos1.id -> 1
            mqttVirtualScanningSourceBinding.qos2.id -> 2
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

        val id =
            if (mqttVirtualScanningSourceModel == null) 0 else mqttVirtualScanningSourceModel!!.id
        val enabled = mqttVirtualScanningSourceModel?.enabled != false

        val portAsInteger = port.toInt()
        if (portAsInteger < 0 || portAsInteger > maxPortNumber) {
            mqttVirtualScanningSourceBinding.mqttSourcePortInput.apply {
                error = getString(R.string.please_valid_port)
                requestFocus()
            }
            return null
        }

        return MqttVirtualScanningSourceModel(
            id,
            name,
            enabled,
            protocol,
            address,
            portAsInteger,
            path,
            clientId,
            username,
            password,
            qos
        )
    }


    companion object {
        private const val MQTT_VIRTUAL_SCANNING_SOURCE_ACTIVITY_KEY =
            "MQTT_VIRTUAL_SCANNING_SOURCE_ACTIVITY_KEY"

        fun start(
            context: Context,
            mqttVirtualScanningSourceModel: MqttVirtualScanningSourceModel? = null
        ) {
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
