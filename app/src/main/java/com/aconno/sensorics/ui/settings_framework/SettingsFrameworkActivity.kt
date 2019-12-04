package com.aconno.sensorics.ui.settings_framework

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.aconno.sensorics.BluetoothConnectService
import com.aconno.sensorics.R
import com.aconno.sensorics.device.beacon.Beacon
import com.aconno.sensorics.device.beacon.v2.BeaconImpl
import com.aconno.sensorics.device.bluetooth.BluetoothGattCallback
import com.aconno.sensorics.device.bluetooth.BluetoothTaskProcessorImpl
import com.aconno.sensorics.device.bluetooth.tasks.GenericTask
import com.aconno.sensorics.device.bluetooth.tasks.lock.LockStateRequestCallback
import com.aconno.sensorics.domain.model.GattCallbackPayload
import com.aconno.sensorics.domain.scanning.Bluetooth
import com.aconno.sensorics.domain.scanning.BluetoothTaskProcessor
import com.google.gson.Gson
import io.reactivex.disposables.Disposable
import timber.log.Timber

class SettingsFrameworkActivity : AppCompatActivity(), LockStateRequestCallback {
    private var bluetoothConnectService: BluetoothConnectService? = null
    private var connectResultDisposable: Disposable? = null
    private lateinit var macAddress: String
    private lateinit var taskProcessor: BluetoothTaskProcessor
    private lateinit var beacon: Beacon

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Timber.d("Disconnected")
            connectResultDisposable?.dispose()
            bluetoothConnectService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            bluetoothConnectService =
                (service as? BluetoothConnectService.LocalBinder)?.getService()
            Timber.d("Connected")

            bluetoothConnectService?.let { bluetoothService ->
                bluetoothService.getConnectResultsLiveData().observe(this@SettingsFrameworkActivity,
                    Observer<GattCallbackPayload> {
                        onNewPayload(it)
                        taskProcessor.accept(it)
                    })

                taskProcessor = BluetoothTaskProcessorImpl(bluetoothService.bluetooth)

                bluetoothService.startConnectionStream()

                bluetoothService.connect(macAddress)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getDeviceMacAddress(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        Intent(applicationContext, BluetoothConnectService::class.java).also {
            bindService(it, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(DEVICE_MAC_ADDRESS, macAddress)
    }

    private fun onNewPayload(payload: GattCallbackPayload) {
        Timber.i(payload.action)
        when (payload.action) {
            BluetoothGattCallback.ACTION_GATT_CONNECTED -> {
                Timber.d("Connected to GATT")
                bluetoothConnectService?.let {
                    beacon = BeaconImpl(this, taskProcessor)
                } ?: throw IllegalStateException(
                    "Something went wrong, connection service uninitialized!"
                )

                beacon.requestDeviceLockStatus(this)
            }
            BluetoothGattCallback.ACTION_GATT_DISCONNECTED -> {
                Toast.makeText(
                    this,
                    "Disconnected: ${payload.payload}",
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {
                return
            }
        }
    }

    override fun onDeviceLockStateRead(unlocked: Boolean) {
        if (unlocked) {
            Toast.makeText(
                this,
                "Device unlocked!",
                Toast.LENGTH_LONG
            ).show()

            beacon.read(object : GenericTask("On Read Completed Task") {
                override fun onSuccess() {
                    Timber.d("Test567")
                    Timber.d(Gson().toJson(beacon.toJson()))
                }

                override fun execute(bluetooth: Bluetooth): Boolean {
                    return true
                }
            })
        } else {
            runOnUiThread {
                createPasswordDialog(object : OnPasswordDialogAction {
                    override fun onPasswordEntered(password: String) {
                        beacon.unlock(password, this@SettingsFrameworkActivity) // TODO: Do this in a better way
                    }

                    override fun onDialogCancelled() {
                        Toast.makeText(
                            this@SettingsFrameworkActivity,
                            "Cannot connect without a password!",
                            Toast.LENGTH_LONG
                        ).show()
                        unbindService(serviceConnection)
                    }
                }).show()
            }
        }
    }

    override fun onError(e: Exception) {
    }

    private fun createPasswordDialog(onPasswordDialogAction: OnPasswordDialogAction): AlertDialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val view: View = layoutInflater.inflate(R.layout.dialog_password, null as ViewGroup?)
        builder.setView(view)
        val etPassword = view.findViewById<EditText>(R.id.et_password)
        builder.setPositiveButton("Confirm") { dialog, _ ->
            onPasswordDialogAction.onPasswordEntered(etPassword?.text.toString())
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            onPasswordDialogAction.onDialogCancelled()
            dialog.dismiss()
        }
        builder.setOnCancelListener { onPasswordDialogAction.onDialogCancelled() }
        return builder.create()
    }

    private fun getDeviceMacAddress(savedInstanceState: Bundle?) {
        macAddress = intent?.getStringExtra(DEVICE_MAC_ADDRESS)
            ?: savedInstanceState?.getString(DEVICE_MAC_ADDRESS)
                ?: ""

        if (macAddress.isBlank()) {
            Timber.e("MAC address not passed to activity!")
            finish()
        }
    }

    interface OnPasswordDialogAction {
        fun onPasswordEntered(password: String)
        fun onDialogCancelled()
    }

    companion object {
        private const val DEVICE_MAC_ADDRESS = "device_mac_address"
        fun start(context: Context, deviceMacAddress: String) {
            Intent(context, SettingsFrameworkActivity::class.java).also {
                it.putExtra(DEVICE_MAC_ADDRESS, deviceMacAddress)
                context.startActivity(it)
            }
        }
    }
}