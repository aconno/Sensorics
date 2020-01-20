package com.aconno.sensorics.ui.device_main

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.*
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.aconno.sensorics.*
import com.aconno.sensorics.device.bluetooth.BluetoothGattCallback
import com.aconno.sensorics.domain.format.ConnectionCharacteristicsFinder
import com.aconno.sensorics.domain.interactor.filter.FilterByMacUseCase
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.GattCallbackPayload
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.ui.ActionListActivity
import com.aconno.sensorics.ui.MainActivity
import com.aconno.sensorics.ui.cache.CacheActivity
import com.aconno.sensorics.ui.devicecon.WriteCommand
import com.aconno.sensorics.ui.dfu.DfuActivity
import com.aconno.sensorics.ui.livegraph.LiveGraphOpener
import com.aconno.sensorics.ui.settings_framework.SettingsFrameworkActivity
import com.aconno.sensorics.viewmodel.resources.MainResourceViewModel
import com.google.android.material.snackbar.Snackbar
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

    @Inject
    lateinit var mainResourceViewModel: MainResourceViewModel

    private lateinit var mDevice: Device

    private val writeCommandQueue: Queue<WriteCommand> = ArrayDeque()

    private var sensorReadingFlowDisposable: Disposable? = null

    private var getResourceDisposable: Disposable? = null

    private var webViewBundle: Bundle? = null

    private var bluetoothConnectService: BluetoothConnectService? = null

    private var isServicesDiscovered = false

    private var hasSettings: Boolean = false

    private var hasCache: Boolean = false

    var menu: Menu? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Timber.d("Disconnected")
            bluetoothConnectService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            bluetoothConnectService =
                (service as? BluetoothConnectService.LocalBinder)
                    ?.getService()
                    ?.also { bluetoothService ->
                        Timber.d("Connected")

                        activity?.takeIf { isAdded }?.let { fragmentActivity ->
                            bluetoothService.getConnectResultsLiveData()
                                .observe(fragmentActivity, Observer<GattCallbackPayload> {
                                    onConnectionPayloadReceived(it)
                                })

                            bluetoothService.startConnectionStream()

                            bluetoothService.connect(mDevice.macAddress)
                        }

                    }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
        getParams()
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
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (!mainActivity.isScanning() &&
            !mDevice.connectable &&
            bluetoothAdapter != null &&
            bluetoothAdapter.isEnabled) {
            showAlertDialog(mainActivity)
        }
    }

    override fun onDetach() {
        if (mDevice.connectable)
            context?.unbindService(serviceConnection)
        super.onDetach()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (mDevice.connectable) {
            menu.clear()
            this.menu = menu
        }

        activity?.menuInflater?.inflate(R.menu.menu_readings, menu)
        setMenuItemsVisibility(menu)
    }

    private fun setMenuItemsVisibility(menu: Menu?) {
        menu?.let {
            it.findItem(R.id.action_start_usecases_activity).isVisible =
                BuildConfig.FLAVOR == DEV_BUILD_FLAVOR
            it.findItem(R.id.action_toggle_connect).isVisible = mDevice.connectable
            it.findItem(R.id.action_start_logging_activity).isVisible = hasSettings
            it.findItem(R.id.action_dfu).isVisible = hasSettings
            it.findItem(R.id.action_settings_framework).isVisible = hasSettings
            it.findItem(R.id.action_cache).isVisible = hasCache
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        context?.let { context ->
            when (item.itemId) {
                R.id.action_toggle_connect ->
                    if (item.isChecked) {
                        item.isChecked = false
                        bluetoothConnectService?.disconnect()
                        item.title = getString(R.string.connect)
                        true
                    } else {
                        item.isChecked = true
                        bluetoothConnectService?.connect(mDevice.macAddress)
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
                R.id.action_dfu -> {
                    DfuActivity.start(context, mDevice.macAddress)
                    return true
                }
                R.id.action_cache -> {
                    CacheActivity.start(context, mDevice.macAddress)
                    return true
                }
                R.id.action_settings_framework -> {
                    (context as? MainActivity)?.stopScanOperation()
                    SettingsFrameworkActivity.start(context, mDevice.macAddress)
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

    private fun onConnectionPayloadReceived(gattCallbackPayload: GattCallbackPayload) {
        Timber.i(gattCallbackPayload.action)
        val text: String = when (gattCallbackPayload.action) {
            BluetoothGattCallback.ACTION_GATT_DEVICE_NOT_FOUND -> {
                setToggleActionText(R.string.connect)

                getString(R.string.device_not_found)
            }
            BluetoothGattCallback.ACTION_GATT_CONNECTING -> {
                setToggleActionText(R.string.disconnect)

                getString(R.string.connecting)
            }
            BluetoothGattCallback.ACTION_GATT_CONNECTED -> {
                setToggleActionText(R.string.disconnect)

                getString(R.string.connected)
            }
            BluetoothGattCallback.ACTION_GATT_SERVICES_DISCOVERED -> {
                setToggleActionText(R.string.disconnect)
                isServicesDiscovered = true

                getString(R.string.discovered)
            }
            BluetoothGattCallback.ACTION_GATT_DISCONNECTED -> {
                setToggleActionText(R.string.connect)
                isServicesDiscovered = false
                bluetoothConnectService?.close()

                getString(R.string.disconnected)
            }
            BluetoothGattCallback.ACTION_GATT_ERROR -> {
                setToggleActionText(R.string.connect)
                isServicesDiscovered = false
                getString(R.string.error)
            }
            BluetoothGattCallback.ACTION_GATT_CHAR_WRITE -> {
                setToggleActionText(R.string.disconnect)
                writeCommandQueue.poll()
                writeCharacteristics(writeCommandQueue.peek())
                getString(R.string.connected)

            }
            BluetoothGattCallback.ACTION_BEACON_HAS_SETTINGS -> {
                hasSettings = true
                activity?.invalidateOptionsMenu()

                ""
            }
            BluetoothGattCallback.ACTION_BEACON_HAS_CACHE -> {
                Timber.i("Services discovered")
                onBeaconHasCache()

                ""
            }
            else -> ""
        }

        text.takeIf {
            it.isNotBlank()
        }.let {
            web_view.loadUrl("javascript:onStatusReading('$text')")
        }
    }

    private fun setToggleActionText(@StringRes resId: Int) {
        menu?.let {
            it.findItem(R.id.action_toggle_connect)?.title = getString(resId)
        }
    }

    private fun setupConnectionForFreight() {
        context?.let { appContext ->
            Intent(
                appContext, BluetoothConnectService::class.java
            ).let {
                appContext.bindService(it, serviceConnection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    private fun subscribeOnSensorReadings() {
        sensorReadingFlowDisposable = sensorReadingFlow
            .concatMap { filterByMacUseCase.execute(it, mDevice.macAddress).toFlowable() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { readings ->

                val jsonValues = generateJsonArray(readings)
                setHasSettings(readings)

                web_view.loadUrl("javascript:onSensorReadings('$jsonValues')")
            }
    }

    private fun setHasSettings(readings: List<Reading>) {
        hasSettings = readings[0].device.hasSettings
    }

    private fun generateJsonArray(readings: List<Reading>?): String {

        val jsonObject = JSONObject()

        readings?.forEach {
            if (!it.value.toDouble().isNaN()) {
                jsonObject.put(it.name, it.value)
            }
        }

        readings?.firstOrNull()?.let {
            jsonObject.put("rssi", it.rssi)
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

        @JavascriptInterface
        fun writeCharacteristic(characteristicName: String, value: Byte, type: String) {
            mDevice.connectionWriteList?.find {
                it.characteristicName == characteristicName
            }?.let {
                writeCharacteristics(
                    WriteCommand(
                        UUID.fromString(it.serviceUUID),
                        UUID.fromString(it.characteristicUUID),
                        type,
                        byteArrayOf(value)
                    )
                )
            }
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
            bluetoothConnectService!!.writeCharacteristic(
                it.serviceUUID,
                it.charUUID,
                it.type,
                it.value
            )
        }
    }

    private fun onBeaconHasCache() {
        hasCache = true
    }

    companion object {

        private const val KEY_DEVICE = "KEY_DEVICE"
        private const val DEV_BUILD_FLAVOR = "dev"

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