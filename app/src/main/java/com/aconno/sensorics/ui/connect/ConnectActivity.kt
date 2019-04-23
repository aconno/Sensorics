package com.aconno.sensorics.ui.connect

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import com.aconno.sensorics.BluetoothConnectService
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.format.ConnectionCharacteristicsFinder
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.getRealName
import com.aconno.sensorics.toHexByte
import com.aconno.sensorics.ui.configure.ConfigureActivity
import com.aconno.sensorics.viewmodel.connection.ConnectionViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_connect.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class ConnectActivity : DaggerAppCompatActivity(), BluetoothServiceConnection.ConnectionCallback {

    private val bluetoothServiceConnection = BluetoothServiceConnection()

    @Inject
    lateinit var connectionCharacteristicsFinder: ConnectionCharacteristicsFinder

    @Inject
    lateinit var connectionViewModel: ConnectionViewModel

    private lateinit var device: Device
    private lateinit var compositeDisposable: CompositeDisposable

    private var hasSettings = false
    private var isConnectedOrConnecting = true
    private var shouldStopService = false
    private var latestStatusStringRes = 0

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(EXTRA_CONNECTION_STATUS, isConnectedOrConnecting)
        outState.putBoolean(EXTRA_HAS_SETTINGS, hasSettings)
        web_view?.saveState(outState)
    }

    override fun onStart() {
        super.onStart()
        BluetoothConnectService.start(this, bluetoothServiceConnection, device)
    }

    override fun onStop() {
        super.onStop()
        applicationContext.unbindService(bluetoothServiceConnection)

        if (shouldStopService) {
            applicationContext.startService(BluetoothConnectService.getStopIntent(this))
        }
    }

    override fun onBackPressed() {
        if (isConnectedOrConnecting) {
            val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)

            alertDialogBuilder.setTitle(resources.getString(R.string.dialog_close_connection))
            alertDialogBuilder
                .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                    shouldStopService = true
                    dialog.cancel()
                    super.onBackPressed()

                }
                .setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
                    shouldStopService = false
                    dialog.cancel()
                    super.onBackPressed()
                }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)
        setSupportActionBar(toolbar)

        loadParams()

        savedInstanceState?.let {
            loadSavedInstanceState(it)
        }

        compositeDisposable = CompositeDisposable()
        bluetoothServiceConnection.connectionCallback = this

        if (!device.connectable) {
            //Device is not connectable go back
            finish()
        }

        setupWebView(savedInstanceState)
    }

    private fun loadSavedInstanceState(it: Bundle) {
        isConnectedOrConnecting = it.getBoolean(EXTRA_CONNECTION_STATUS, true)
        hasSettings = it.getBoolean(EXTRA_HAS_SETTINGS, false)
    }

    private fun loadParams() {
        val deviceJson = intent.getStringExtra(EXTRA_DEVICE)
        device = Gson().fromJson<Device>(deviceJson, Device::class.java)
        device = connectionCharacteristicsFinder.addCharacteristicsToDevice(device)
        supportActionBar?.title = device.getRealName()
        supportActionBar?.subtitle = device.macAddress
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(savedInstanceState: Bundle?) {
        web_view.webChromeClient = WebChromeClient()
        web_view.webViewClient = MyWebViewClient()
        web_view.addJavascriptInterface(WebViewJavaScriptInterface(), "app")
        web_view.settings.javaScriptEnabled = true

        if (savedInstanceState != null) {
            web_view.restoreState(savedInstanceState)
        } else {
            val getResourceDisposable = connectionViewModel.getConnectionResourcePath(device.name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { resourcePath ->
                        text_error_message.visibility = View.INVISIBLE
                        web_view.loadUrl(resourcePath)
                    },
                    { throwable ->
                        text_error_message.visibility = View.VISIBLE
                        text_error_message.text = throwable.message
                    })

            compositeDisposable.add(getResourceDisposable)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(R.menu.activity_connect, menu)

        menu?.let {
            it.findItem(R.id.action_toggle_connect).isVisible = device.connectable
            it.findItem(R.id.action_start_config_activity).isVisible = hasSettings
            it.findItem(R.id.action_start_logging_activity).isVisible = hasSettings

            if (isConnectedOrConnecting) {
                with(it.findItem(R.id.action_toggle_connect)) {
                    title = getString(com.aconno.sensorics.R.string.disconnect)
                    isChecked = true
                }
            } else {
                with(it.findItem(R.id.action_toggle_connect)) {
                    title = getString(com.aconno.sensorics.R.string.connect)
                    isChecked = false
                }
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_toggle_connect ->
                if (item.isChecked) {
                    bluetoothServiceConnection.disconnect()
                    item.isChecked = false
                    item.title = getString(R.string.connect)
                } else {
                    item.isChecked = true
                    bluetoothServiceConnection.connect(device.macAddress)
                    item.title = getString(R.string.disconnect)
                }
            R.id.action_start_config_activity -> {
                ConfigureActivity.start(this, device = device)
            }
            R.id.action_start_logging_activity -> {
                Snackbar.make(
                    web_view,
                    "Functionality coming soon.",
                    Snackbar.LENGTH_SHORT
                ).show()
                //TODO: Implement Logger functionality
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    override fun onStatusTextChanged(stringRes: Int) {
        runOnUiThread {
            latestStatusStringRes = stringRes
            web_view?.loadUrl("javascript:onStatusReading('${getString(stringRes)}')")
        }
    }

    override fun onHasSettings() {
        hasSettings = true
    }

    override fun onConnected() {
        isConnectedOrConnecting = true
        invalidateOptionsMenu()
    }

    override fun onDisconnected() {
        isConnectedOrConnecting = false
        invalidateOptionsMenu()
    }

    inner class MyWebViewClient : WebViewClient() {

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            if (latestStatusStringRes == 0) {
                onStatusTextChanged(R.string.connecting)
            } else {
                onStatusTextChanged(latestStatusStringRes)
            }
        }
    }

    inner class WebViewJavaScriptInterface {

        @JavascriptInterface
        fun buzzerPlay(checked: Boolean) {
            toggleBuzzerCharacteristic(checked)
        }

        @JavascriptInterface
        fun changeColorOfFreight(color: String) {
            writeColorCharacteristic(color)
        }

    }

    // This class should not know what color or buzzer is
    //TODO Take this methods inside HTML
    private fun writeColorCharacteristic(hex: String) {
        //val hex = Integer.toHexString(color)
        Timber.i("hex value $hex hex $hex")
        val red: Byte
        val green: Byte
        val blue: Byte
        if (hex.length >= 7) {

            red = "0x${hex.subSequence(1, 3)}".toHexByte()
            green = "0x${hex.subSequence(3, 5)}".toHexByte()
            blue = "0x${hex.subSequence(5, 7)}".toHexByte()
        } else {
            red = "0x00".toHexByte()
            green = "0x00".toHexByte()
            blue = "0x00".toHexByte()
        }


        device.connectionWriteList?.get(1)?.let {
            val serviceUUID: UUID = UUID.fromString(it.serviceUUID)
            val charUUID: UUID = UUID.fromString(it.characteristicUUID)

            val type: String = it.values[1].type
            val value: ByteArray = byteArrayOf(red)

            bluetoothServiceConnection.writeCharacteristic(
                serviceUUID,
                charUUID,
                type,
                value
            )
        }

        device.connectionWriteList?.get(2)?.let {
            val serviceUUID: UUID = UUID.fromString(it.serviceUUID)
            val charUUID: UUID = UUID.fromString(it.characteristicUUID)
            val type: String = it.values[1].type
            val value: ByteArray = byteArrayOf(green)
            bluetoothServiceConnection.writeCharacteristic(
                serviceUUID,
                charUUID,
                type,
                value
            )
        }

        device.connectionWriteList?.get(3)?.let {
            val serviceUUID: UUID = UUID.fromString(it.serviceUUID)
            val charUUID: UUID = UUID.fromString(it.characteristicUUID)
            val type: String = it.values[1].type
            val value: ByteArray = byteArrayOf(blue)

            bluetoothServiceConnection.writeCharacteristic(
                serviceUUID,
                charUUID,
                type,
                value
            )
        }
    }

    // This class should not know what color or buzzer is
    //TODO Take this methods inside HTML
    private fun toggleBuzzerCharacteristic(turnOn: Boolean) {
        device.connectionWriteList?.get(0)?.let { deviceWrite ->
            val turnOnIndex = if (turnOn) 0 else 1

            bluetoothServiceConnection.writeCharacteristic(
                UUID.fromString(deviceWrite.serviceUUID),
                UUID.fromString(deviceWrite.characteristicUUID),
                deviceWrite.values[turnOnIndex].type,
                byteArrayOf(deviceWrite.values[turnOnIndex].value.toHexByte())
            )
        }
    }

    companion object {
        const val EXTRA_DEVICE = "EXTRA_DEVICE"
        private const val EXTRA_CONNECTION_STATUS = "EXTRA_CONNECTION_STATUS"
        private const val EXTRA_HAS_SETTINGS = "EXTRA_HAS_SETTINGS"

        fun start(context: Context, device: Device) {
            Intent(context, ConnectActivity::class.java).apply {
                putExtra(EXTRA_DEVICE, Gson().toJson(device))
                context.startActivity(this)
            }
        }
    }
}