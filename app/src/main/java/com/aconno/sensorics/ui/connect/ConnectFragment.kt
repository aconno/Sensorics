package com.aconno.sensorics.ui.connect

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.format.ConnectionCharacteristicsFinder
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.toHexByte
import com.aconno.sensorics.ui.Connectable
import com.aconno.sensorics.viewmodel.connection.ConnectionViewModel
import com.google.gson.Gson
import dagger.android.support.DaggerFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_connect.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class ConnectFragment : DaggerFragment(), BluetoothServiceConnection.ConnectionCallback {

    @Inject
    lateinit var connectionCharacteristicsFinder: ConnectionCharacteristicsFinder

    @Inject
    lateinit var connectionViewModel: ConnectionViewModel

    private lateinit var device: Device
    private lateinit var compositeDisposable: CompositeDisposable

    private var latestStatusStringRes = 0
    private var connectable: Connectable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        compositeDisposable = CompositeDisposable()

        loadParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.activity_connect, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWebView(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Connectable) {
            connectable = context
            connectable?.registerConnectionCallback(this)
        }
    }

    override fun onDetach() {
        connectable?.unRegisterConnectionCallback(this)
        connectable = null
        super.onDetach()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        web_view?.saveState(outState)
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

    private fun loadParams() {
        arguments?.getString(ConnectFragment.EXTRA_DEVICE)?.let {
            val tmpDevice = Gson().fromJson<Device>(it, Device::class.java)
            device = connectionCharacteristicsFinder.addCharacteristicsToDevice(tmpDevice)
            return
        }

        //If device is not provided, go back.
        activity?.onBackPressed()
    }

    override fun onStatusTextChanged(stringRes: Int) {
        latestStatusStringRes = stringRes
        web_view?.loadUrl("javascript:onStatusReading('${getString(stringRes)}')")
    }

    override fun onHasSettings() {
        //No-need
    }

    override fun onConnected() {
        //No-need
    }

    override fun onDisconnected() {
        //No-need
    }

    //Inner Classes
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

            connectable?.writeCharacteristic(
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
            connectable?.writeCharacteristic(
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

            connectable?.writeCharacteristic(
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

            connectable?.writeCharacteristic(
                UUID.fromString(deviceWrite.serviceUUID),
                UUID.fromString(deviceWrite.characteristicUUID),
                deviceWrite.values[turnOnIndex].type,
                byteArrayOf(deviceWrite.values[turnOnIndex].value.toHexByte())
            )
        }
    }

    companion object {
        const val EXTRA_DEVICE = "EXTRA_DEVICE"

        fun newInstance(device: Device) =
            ConnectFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_DEVICE, Gson().toJson(device))
                }
            }
    }
}