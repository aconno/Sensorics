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
import com.aconno.sensorics.device.bluetooth.BluetoothGattCallback
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.ui.base.BaseDialogFragment
import com.google.gson.Gson
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_device_connection.*
import timber.log.Timber
import java.util.*

class DeviceConnectionFragment : BaseDialogFragment() {
    private var connectResultDisposable: Disposable? = null

    private lateinit var device: Device
    private var serviceConnect: BluetoothConnectService? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            connectResultDisposable?.dispose()
            serviceConnect = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serviceConnect = (service as BluetoothConnectService.LocalBinder).getService()

            connectResultDisposable = serviceConnect!!.getConnectResults()
                .subscribe {
                    Timber.d(it.action)

                    if (it.action == BluetoothGattCallback.ACTION_GATT_CONNECTED) {
                        isConnected = true
                    } else if (it.action == BluetoothGattCallback.ACTION_GATT_DISCONNECTED) {
                        isConnected = false
                    }
                }

            serviceConnect?.connect("")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getParams()

        val gattServiceIntent = Intent(
            context, BluetoothConnectService::class.java
        )

        context!!.bindService(
            gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_device_connection, container, false)
    }

    var isOn = false
    var isConnected = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_press.setOnClickListener {

            if (isConnected) {
                if (isOn) {
                    serviceConnect?.writeCharacteristic(
                        UUID.fromString(""),
                        UUID.fromString(""),
                        byteArrayOf(0x00)
                    )
                } else {
                    serviceConnect?.writeCharacteristic(
                        UUID.fromString(""),
                        UUID.fromString(""),
                        byteArrayOf(0x01)
                    )
                }

                isOn = !isOn
            }
        }
    }

    private fun getParams() {
        device = Gson().fromJson(
            arguments!!.getString(KEY_DEVICE)
            , Device::class.java
        )
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