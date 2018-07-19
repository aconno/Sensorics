package com.aconno.sensorics.ui.devicecon

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.sensorics.BluetoothConnectService
import com.aconno.sensorics.R
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.device.bluetooth.BluetoothGattCallback
import com.aconno.sensorics.domain.format.ConnectionCharacteristicsFinder
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.hexToByte
import com.aconno.sensorics.ui.base.BaseDialogFragment
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_device_connection.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class DeviceConnectionFragment : BaseDialogFragment() {

    @Inject
    lateinit var connectionCharacteristicsFinder: ConnectionCharacteristicsFinder

    private var connectResultDisposable: Disposable? = null

    private lateinit var mDevice: Device
    private var serviceConnect: BluetoothConnectService? = null

    var isServicesDiscovered = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            connectResultDisposable?.dispose()
            serviceConnect = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serviceConnect = (service as BluetoothConnectService.LocalBinder).getService()

            connectResultDisposable = serviceConnect!!.getConnectResults()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Timber.d(it.action)
                    var text = ""

                    when {
                        it.action == BluetoothGattCallback.ACTION_GATT_DEVICE_NOT_FOUND -> {
                            text = "DEVICE NOT FOUND"
                        }
                        it.action == BluetoothGattCallback.ACTION_GATT_CONNECTING -> {
                            text = "CONNECTING"
                        }
                        it.action == BluetoothGattCallback.ACTION_GATT_CONNECTED -> {
                            text = "CONNECTED"
                        }
                        it.action == BluetoothGattCallback.ACTION_GATT_SERVICES_DISCOVERED -> {
                            isServicesDiscovered = true
                            progressbar?.visibility = View.INVISIBLE
                            enableToggleViews()
                            text = "DISCOVERED"
                        }
                        it.action == BluetoothGattCallback.ACTION_GATT_DISCONNECTED -> {
                            isServicesDiscovered = false
                            progressbar?.visibility = View.INVISIBLE
                            disableToggleViews()
                            text = "DISCONNECTED"
                        }
                        it.action == BluetoothGattCallback.ACTION_GATT_ERROR -> {
                            isServicesDiscovered = false
                            text = "ERROR"
                        }
                        else -> {

                        }
                    }

                    lbl_status?.text = "Status : $text"
                }

            serviceConnect?.connect(mDevice.macAddress)
            progressbar?.visibility = View.VISIBLE
        }
    }

    private fun enableToggleViews() {
        btn_buzzer.isEnabled = true
        btn_redLight.isEnabled = true
        btn_greenLight.isEnabled = true
        btn_blueLight.isEnabled = true
    }

    private fun disableToggleViews() {
        btn_buzzer.isEnabled = false
        btn_redLight.isEnabled = false
        btn_greenLight.isEnabled = false
        btn_blueLight.isEnabled = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (context!!.applicationContext as SensoricsApplication).appComponent.inject(this)
        getParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_device_connection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gattServiceIntent = Intent(
            context, BluetoothConnectService::class.java
        )

        context!!.bindService(
            gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE
        )

        btn_buzzer.setOnCheckedChangeListener { _, isChecked ->
            toggleCharacteristic(0, isChecked)
        }

        btn_redLight.setOnCheckedChangeListener { _, isChecked ->
            toggleCharacteristic(1, isChecked)

        }

        btn_greenLight.setOnCheckedChangeListener { _, isChecked ->
            toggleCharacteristic(2, isChecked)
        }

        btn_blueLight.setOnCheckedChangeListener { _, isChecked ->
            toggleCharacteristic(3, isChecked)
        }
    }

    private fun toggleCharacteristic(index: Int, turnOn: Boolean) {
        serviceConnect!!.writeCharacteristic(
            UUID.fromString(mDevice.connectionWriteList!![index].serviceUUID),
            UUID.fromString(mDevice.connectionWriteList!![index].characteristicUUID),
            byteArrayOf(mDevice.connectionWriteList!![index].values[if (turnOn) 0 else 1].value.hexToByte())
        )
    }

    private fun getParams() {
        val device = Gson().fromJson(
            arguments!!.getString(KEY_DEVICE)
            , Device::class.java
        )

        mDevice = connectionCharacteristicsFinder.addCharacteristicsToDevice(device)
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.unbindService(serviceConnection)
        serviceConnect = null
    }

    companion object {
        private const val KEY_DEVICE = "KEY_DEVICE"

        fun newInstance(device: Device): DeviceConnectionFragment {

            val bundle = Bundle()
            bundle.putString(KEY_DEVICE, Gson().toJson(device))

            val fragment = DeviceConnectionFragment()
            fragment.arguments = bundle

            return fragment
        }
    }
}