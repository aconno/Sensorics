package com.aconno.sensorics.ui.device_main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.Snackbar
import android.view.*
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.aconno.sensorics.*
import com.aconno.sensorics.device.bluetooth.BluetoothGattCallback
import com.aconno.sensorics.domain.format.ConnectionCharacteristicsFinder
import com.aconno.sensorics.domain.interactor.filter.FilterByMacUseCase
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.ui.ActionListActivity
import com.aconno.sensorics.ui.MainActivity
import com.aconno.sensorics.ui.configure.ConfigureActivity
import com.aconno.sensorics.ui.devicecon.WriteCommand
import com.aconno.sensorics.ui.livegraph.LiveGraphOpener
import com.aconno.sensorics.viewmodel.resources.MainResourceViewModel
import com.google.gson.Gson
import dagger.android.support.DaggerFragment
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_device_main.*
import org.json.JSONObject
import timber.log.Timber
import java.util.*
import javax.inject.Inject


@SuppressLint("SetJavaScriptEnabled")
class DeviceMainFragment : DaggerFragment() {

    @Inject
    lateinit var connectionCharacteristicsFinder: ConnectionCharacteristicsFinder

    @Inject
    lateinit var sensorReadingFlow: Flowable<List<Reading>> //TODO: Move this to the view model

    @Inject
    lateinit var filterByMacUseCase: FilterByMacUseCase

    private var sensorReadingFlowDisposable: Disposable? = null

    @Inject
    lateinit var mainResourceViewModel: MainResourceViewModel
    private var getResourceDisposable: Disposable? = null

    private lateinit var mDevice: Device
    private var webViewBundle: Bundle? = null

    private val writeCommandQueue: Queue<WriteCommand> = ArrayDeque<WriteCommand>()
    private var serviceConnect: BluetoothConnectService? = null
    private var connectResultDisposable: Disposable? = null

    private var isServicesDiscovered = false
    var menu: Menu? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Timber.d("Disconnected")
            connectResultDisposable?.dispose()
            serviceConnect = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serviceConnect = (service as BluetoothConnectService.LocalBinder).getService()
            Timber.d("Connected")

            connectResultDisposable = serviceConnect!!.getConnectResults()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (!isAdded) {
                        return@subscribe
                    }

                    Timber.d(it.action)
                    val text: String

                    when {
                        it.action == BluetoothGattCallback.ACTION_GATT_DEVICE_NOT_FOUND -> {
                            Timber.i("Device not found")
                            if (menu != null) {
                                val item: MenuItem = menu!!.findItem(R.id.action_toggle_connect)
                                item.title = getString(R.string.connect)
                            }
                            text = getString(R.string.device_not_found)
                        }
                        it.action == BluetoothGattCallback.ACTION_GATT_CONNECTING -> {
                            Timber.i("Device connecting")
                            if (menu != null) {
                                val item: MenuItem = menu!!.findItem(R.id.action_toggle_connect)
                                item.title = getString(R.string.disconnect)
                            }
                            text = getString(R.string.connecting)
                        }
                        it.action == BluetoothGattCallback.ACTION_GATT_CONNECTED -> {
                            Timber.i("Device connected")

                            if (menu != null) {
                                val item: MenuItem = menu!!.findItem(R.id.action_toggle_connect)
                                item.title = getString(R.string.disconnect)
                            }
                            text = getString(R.string.connected)
                        }
                        it.action == BluetoothGattCallback.ACTION_GATT_SERVICES_DISCOVERED -> {
                            Timber.i("Device discovered")
                            if (menu != null) {
                                val item: MenuItem = menu!!.findItem(R.id.action_toggle_connect)
                                item.title = getString(R.string.disconnect)
                            }
                            isServicesDiscovered = true
                            //progressbar?.visibility = View.INVISIBLE
                            //enableToggleViews()
                            text = getString(R.string.discovered)
                        }
                        it.action == BluetoothGattCallback.ACTION_GATT_DISCONNECTED -> {
                            Timber.i("Device disconnected")
                            if (menu != null) {
                                val item: MenuItem = menu!!.findItem(R.id.action_toggle_connect)
                                item.title = getString(R.string.connect)
                            }
                            isServicesDiscovered = false
                            //progressbar?.visibility = View.INVISIBLE
                            //disableToggleViews()
                            text = getString(R.string.disconnected)

                            serviceConnect?.close()
                        }
                        it.action == BluetoothGattCallback.ACTION_GATT_ERROR -> {
                            Timber.i("Device Error")
                            if (menu != null) {
                                val item: MenuItem = menu!!.findItem(R.id.action_toggle_connect)
                                item.title = getString(R.string.connect)
                            }
                            isServicesDiscovered = false
                            text = getString(R.string.error)
                        }
                        it.action == BluetoothGattCallback.ACTION_GATT_CHAR_WRITE -> {
                            Timber.i("Device write")
                            if (menu != null) {
                                val item: MenuItem = menu!!.findItem(R.id.action_toggle_connect)
                                item.title = getString(R.string.disconnect)
                            }
                            writeCommandQueue.poll()
                            writeCharacteristics(writeCommandQueue.peek())
                            text = getString(R.string.connected)

                        }
                        else -> {
                            return@subscribe
                        }
                    }
                    web_view.loadUrl("javascript:onStatusReading('$text')")
                }

            serviceConnect?.connect(mDevice.macAddress)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
        getParams()
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_device_main, container, false)
    }

    override fun onResume() {
        super.onResume()
        val mainActivity: MainActivity = context as MainActivity
        mainActivity.supportActionBar?.title = mDevice.getRealName()
        mainActivity.supportActionBar?.subtitle = mDevice.macAddress

        if (!mainActivity.isScanning() && !mDevice.connectable) {
            showAlertDialog(mainActivity)
        }
    }

    override fun onDetach() {
        if (mDevice.connectable)
            context?.unbindService(serviceConnection)
        super.onDetach()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        if (mDevice.connectable) {
            menu?.clear()
            this.menu = menu
        }

        activity?.menuInflater?.inflate(R.menu.menu_readings, menu)
        menu?.findItem(R.id.action_start_usecases_activity)?.isVisible = BuildConfig.FLAVOR == "dev"
        menu?.findItem(R.id.action_toggle_connect)?.isVisible = mDevice.connectable
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        context?.let { context ->
            when (item.itemId) {
                R.id.action_toggle_connect ->
                    if (item.isChecked) {
                        item.isChecked = false
                        serviceConnect?.disconnect()
                        item.title = getString(R.string.connect)
                        true
                    } else {
                        item.isChecked = true
                        serviceConnect?.connect(mDevice.macAddress)
                        item.title = getString(R.string.disconnect)
                        true
                    }

                R.id.action_start_actions_activity -> {
                    ActionListActivity.start(context)
                    return true
                }
                R.id.action_start_usecases_activity -> {
                    (activity as MainActivity).onUseCaseClicked(mDevice.macAddress, mDevice.name)
                    return true
                }
                R.id.action_start_config_activity -> {
                    this.view?.let {
                        Snackbar.make(it, "Functionality coming soon.", Snackbar.LENGTH_SHORT)
                            .show()
                    }

                    getContext()?.let {
                        ConfigureActivity.start(it, device = mDevice)
                    }
                    return true
                }
                R.id.action_start_logging_activity -> {
                    this.view?.let {
                        Snackbar.make(
                            it,
                            "Functionality coming soon.",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    //TODO: Implement Logger functionality
                    return true
                }
                else -> {
                    //Do nothing
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupWebView()
        if (mDevice.connectable)
            setupConnectionForFreight()
    }

    private fun setupWebView() {
        web_view.webChromeClient = WebChromeClient()
        web_view.webViewClient = MyWebViewClient()
        web_view.addJavascriptInterface(WebViewJavaScriptInterface(), "app")
        web_view.settings.javaScriptEnabled = true

        if (webViewBundle != null) {
            web_view.restoreState(webViewBundle)
        } else {
            getResourceDisposable = mainResourceViewModel.getResourcePath(mDevice.name)
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
        }

    }

    private fun setupConnectionForFreight() {

        val gattServiceIntent = Intent(
            context, BluetoothConnectService::class.java
        )
        context!!.bindService(
            gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE
        )
    }

    private fun subscribeOnSensorReadings() {
        sensorReadingFlowDisposable = sensorReadingFlow
            .concatMap { filterByMacUseCase.execute(it, mDevice.macAddress).toFlowable() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { readings ->

                var jsonValues = generateJsonArray(readings)

                web_view.loadUrl("javascript:onSensorReadings('$jsonValues')")
            }
    }

    private fun generateJsonArray(readings: List<Reading>?): String {

        val jsonObject = JSONObject()

        readings?.forEach {

            if (!it.value.toDouble().isNaN()) {
                jsonObject.put(it.name, it.value)
            }


        }

        return jsonObject.toString()
    }

    override fun onDestroyView() {
        webViewBundle = Bundle()
        web_view.saveState(webViewBundle)

        super.onDestroyView()
        getResourceDisposable?.dispose()
        sensorReadingFlowDisposable?.dispose()
    }

    inner class MyWebViewClient : WebViewClient() {

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            subscribeOnSensorReadings()
        }
    }

    inner class WebViewJavaScriptInterface {

        @JavascriptInterface
        fun openLiveGraph(sensorName: String) {
            activity?.let {
                if (it is LiveGraphOpener) {
                    (it as LiveGraphOpener).openLiveGraph(mDevice.macAddress, sensorName)
                }
            }
        }

        @JavascriptInterface
        fun buzzerPlay(checked: Boolean) {
            Timber.i("test the buzzer value $checked")
            toggleBuzzerCharacteristic(checked)
        }

        @JavascriptInterface
        fun changeColorOfFreight(color: String) {
            Timber.i("test the color value $color")
            writeColorCharacteristic(color)
        }

    }

    private fun getParams() {
        val device = Gson().fromJson(
            arguments!!.getString(KEY_DEVICE)
            , Device::class.java
        )
        Timber.i("device is $device")

        mDevice = connectionCharacteristicsFinder.addCharacteristicsToDevice(device)
    }

    private fun showAlertDialog(mainActivity: MainActivity) {

        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(
            mainActivity
        )

        alertDialogBuilder.setTitle(resources.getString(R.string.start_scan_popup))
        alertDialogBuilder
            .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->

                mainActivity.startScanOperation()
                dialog.cancel()

            }
            .setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->

                dialog.cancel()
            }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    //should be called when the buzzer in acnfreight is pressed
    private fun toggleBuzzerCharacteristic(turnOn: Boolean) {
        val deviceWrite = mDevice.connectionWriteList!![0]
        val turnOnIndex = if (turnOn) 0 else 1

        addWriteCommand(
            UUID.fromString(deviceWrite.serviceUUID),
            UUID.fromString(deviceWrite.characteristicUUID),
            deviceWrite.values[turnOnIndex].type,
            byteArrayOf(deviceWrite.values[turnOnIndex].value.toHexByte())
        )
    }

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


        var deviceWrite = mDevice.connectionWriteList?.get(1)
        deviceWrite?.let {

            Timber.i("Service UUId is ${it.serviceUUID}")

            val serviceUUID: UUID = UUID.fromString(it.serviceUUID)
            val charUUID: UUID = UUID.fromString(it.characteristicUUID)

            val type: String = it.values[1].type
            val value: ByteArray = byteArrayOf(red)

            addWriteCommand(
                serviceUUID,
                charUUID,
                type,
                value
            )
        }


        deviceWrite = mDevice.connectionWriteList?.get(2)
        deviceWrite?.let {

            val serviceUUID: UUID = UUID.fromString(it.serviceUUID)
            val charUUID: UUID = UUID.fromString(it.characteristicUUID)
            val type: String = it.values[1].type
            val value: ByteArray = byteArrayOf(green)
            addWriteCommand(
                serviceUUID,
                charUUID,
                type,
                value
            )
        }

        deviceWrite = mDevice.connectionWriteList?.get(3)
        deviceWrite?.let {

            val serviceUUID: UUID = UUID.fromString(it.serviceUUID)
            val charUUID: UUID = UUID.fromString(it.characteristicUUID)
            val type: String = it.values[1].type
            val value: ByteArray = byteArrayOf(blue)

            addWriteCommand(
                serviceUUID,
                charUUID,
                type,
                value
            )
        }
    }

    private fun addWriteCommand(serviceUUID: UUID, charUUID: UUID, type: String, value: ByteArray) {
        val writeCommand = WriteCommand(serviceUUID, charUUID, type, value)
        writeCommandQueue.add(writeCommand)
        writeCharacteristics(writeCommandQueue.peek())
    }


    private fun writeCharacteristics(cmd: WriteCommand?) {
        cmd?.let {
            serviceConnect!!.writeCharacteristic(
                it.serviceUUID,
                it.charUUID,
                it.type,
                it.value
            )
        }
    }

    companion object {

        private const val KEY_DEVICE = "KEY_DEVICE"

        fun newInstance(
            device: Device
        ): DeviceMainFragment {
            val deviceMainFragment = DeviceMainFragment()
            deviceMainFragment.arguments = Bundle().apply {
                putString(KEY_DEVICE, Gson().toJson(device))
            }
            return deviceMainFragment
        }
    }
}