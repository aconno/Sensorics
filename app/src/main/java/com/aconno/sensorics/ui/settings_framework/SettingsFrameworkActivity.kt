package com.aconno.sensorics.ui.settings_framework

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
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
import com.aconno.sensorics.ui.configure.BeaconGeneralFragmentListener
import com.google.gson.Gson
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_configure.*
import timber.log.Timber
import javax.inject.Inject

class SettingsFrameworkActivity : DaggerAppCompatActivity(), LockStateRequestCallback ,
    BeaconGeneralFragmentListener {
    private var bluetoothConnectService: BluetoothConnectService? = null
    private var connectResultDisposable: Disposable? = null
    private lateinit var macAddress: String
    private lateinit var taskProcessor: BluetoothTaskProcessor
    private var beacon: Beacon? = null
    private val gson : Gson = Gson()

    @Inject
    lateinit var beaconViewModel: BeaconSettingsViewModel
    var device: String = ""

    private val beaconSettingsPagerAdapter: BeaconSettingsPagerAdapter by lazy {
        BeaconSettingsPagerAdapter(supportFragmentManager).apply {
            beaconViewModel.beacon.observe(this@SettingsFrameworkActivity, Observer<Beacon> {
                it?.let {
                    this@SettingsFrameworkActivity.beacon = it
                }
            })
        }

    }

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

    private val beaconRequestBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            sendBeaconInfoBroadcast()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configure)
        getDeviceMacAddress(savedInstanceState)

        if (intent.extras != null && intent.extras!!.containsKey(DEVICE_MAC_ADDRESS)) {
            intent.extras!!.getString(DEVICE_MAC_ADDRESS)?.let {
                device = it
            }

        } else {
            throw IllegalArgumentException("Device not provided.")
        }

        setSupportActionBar(toolbar)

        vp_beacon.adapter = beaconSettingsPagerAdapter
        beaconSettingsPagerAdapter.notifyDataSetChanged()

        // Bind the tabs to the ViewPager
        tabs.setViewPager(vp_beacon)

        /*Set titlebar */
        supportActionBar?.subtitle = device

    }

    private fun onBeaconParamsLoaded() {
        beaconSettingsPagerAdapter.slotCount = beacon!!.slotCount.toInt()
        beaconSettingsPagerAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.beacon_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.item_save -> saveChanges()
            else -> return false
        }
        return true
    }

    private fun saveChanges() {
        val intent = Intent(SAVE_CHANGES_BROADCAST)
        intent.putExtra(TARGET_FRAGMENT_ID_BROADCAST_EXTRA,vp_beacon.currentItem)
        LocalBroadcastManager.getInstance(this@SettingsFrameworkActivity).sendBroadcast(intent)
    }


    override fun onStart() {
        super.onStart()
        Intent(applicationContext, BluetoothConnectService::class.java).also {
            bindService(it, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(beaconRequestBroadcastReceiver,
                IntentFilter(BEACON_JSON_REQUEST_BROADCAST))
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(beaconRequestBroadcastReceiver)
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

                beacon?.requestDeviceLockStatus(this)
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

    private fun sendBeaconInfoBroadcast() {
        beacon?.let {
            val intent = Intent(BEACON_JSON_BROADCAST)
            intent.putExtra(BEACON_JSON_BROADCAST_EXTRA,gson.toJson(it.toJson()))
            LocalBroadcastManager.getInstance(this@SettingsFrameworkActivity).sendBroadcast(intent)
        }
    }

    override fun onDeviceLockStateRead(unlocked: Boolean) {
        if (unlocked) {
            Toast.makeText(
                this,
                "Device unlocked!",
                Toast.LENGTH_LONG
            ).show()

            beacon?.read(object : GenericTask("On Read Completed Task") {
                override fun onSuccess() {
                    beaconViewModel.beacon.value = beacon
                    Timber.d(Gson().toJson(beacon!!.toJson()))

                    onBeaconParamsLoaded()

                    tabs.visibility = View.VISIBLE
                    vp_beacon.visibility = View.VISIBLE

                    sendBeaconInfoBroadcast()
                }
                override fun execute(bluetooth: Bluetooth): Boolean {
                    return true
                }
            })
        } else {
            runOnUiThread {
                createPasswordDialog(object : OnPasswordDialogAction {
                    override fun onPasswordEntered(password: String) {
                        beacon?.unlock(password, this@SettingsFrameworkActivity) // TODO: Do this in a better way
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
        const val TARGET_FRAGMENT_ID_BROADCAST_EXTRA = "TARGET_FRAGMENT_ID_BROADCAST_EXTRA"
        const val SAVE_CHANGES_BROADCAST = "com.aconno.sensorics.settings.SAVE_CHANGES_BROADCAST"
        const val BEACON_JSON_BROADCAST = "com.aconno.sensorics.settings.BEACON_JSON_RESPONSE_BROADCAST"
        const val BEACON_JSON_REQUEST_BROADCAST = "com.aconno.sensorics.settings.BEACON_JSON_REQUEST_BROADCAST"
        const val BEACON_JSON_BROADCAST_EXTRA = "BEACON_JSON_BROADCAST_EXTRA"

        private const val DEVICE_MAC_ADDRESS = "device_mac_address"
        fun start(context: Context, deviceMacAddress: String) {
            Intent(context, SettingsFrameworkActivity::class.java).also {
                it.putExtra(DEVICE_MAC_ADDRESS, deviceMacAddress)
                context.startActivity(it)
            }
        }
    }


    override fun updateFirmware() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun resetFactory() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun powerOff() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addPassword() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}