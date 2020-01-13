package com.aconno.sensorics.ui.settings_framework

import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.aconno.sensorics.*
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
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_configure.*
import kotlinx.android.synthetic.main.dialog_indeterminate_progress.*
import timber.log.Timber
import javax.inject.Inject

class SettingsFrameworkActivity : DaggerAppCompatActivity(), LockStateRequestCallback,
    BeaconGeneralFragmentListener {
    private var bluetoothConnectService: BluetoothConnectService? = null
    private var connectResultDisposable: Disposable? = null
    private lateinit var macAddress: String
    private lateinit var taskProcessor: BluetoothTaskProcessor
    private lateinit var beacon: Beacon
    private var isServiceBinded = false

    @Inject
    lateinit var beaconViewModel: BeaconSettingsViewModel

    var progressDialog: Dialog? = null
    var indefeniteSnackBar: Snackbar? = null
    var device: String = ""

    private val beaconSettingsPagerAdapter: BeaconSettingsPagerAdapter by lazy {
        BeaconSettingsPagerAdapter(supportFragmentManager).apply {
            beaconViewModel.beacon.observe(this@SettingsFrameworkActivity, Observer<Beacon> {
                it?.let {
                    tabs.visibility = View.VISIBLE
                    this.beacon = it
                }
            })
        }
    }

    private val serviceConnection = object : ServiceConnection {


        override fun onServiceDisconnected(name: ComponentName?) {
            Timber.d("Disconnected")
            connectResultDisposable?.dispose()
            bluetoothConnectService = null
            isServiceBinded = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            bluetoothConnectService =
                (service as? BluetoothConnectService.LocalBinder)?.getService()
            Timber.d("Connected")
            isServiceBinded = true

            bluetoothConnectService?.let { bluetoothService ->
                bluetoothService.getConnectResultsLiveData().observe(this@SettingsFrameworkActivity,
                    Observer<GattCallbackPayload> {
                        onNewPayload(it)
                        taskProcessor.accept(it)
                    })

                taskProcessor = BluetoothTaskProcessorImpl(bluetoothService.bluetooth)

                connectToDevice(bluetoothService)
            }
        }
    }

    private fun connectToDevice(bluetoothService: BluetoothConnectService) {
        bluetoothService.startConnectionStream()

        bluetoothService.connect(macAddress)

        progressDialog = showProgressDialog().apply {
            message.text = "Connecting..."
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configure)
        getDeviceMacAddress(savedInstanceState)

        device = intent?.extras?.getString(DEVICE_MAC_ADDRESS)
            ?: throw throw IllegalArgumentException("Device not provided.")

        setSupportActionBar(toolbar)

        setupUI()
    }

    private fun setupUI() {
        /*Set titlebar */
        supportActionBar?.subtitle = device

        vp_beacon.adapter = beaconSettingsPagerAdapter
        beaconSettingsPagerAdapter.notifyDataSetChanged()

        // Bind the tabs to the ViewPager
        tabs.setViewPager(vp_beacon)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.beacon_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        return false
    }

    override fun onStart() {
        super.onStart()
        Intent(applicationContext, BluetoothConnectService::class.java).also {
            bindService(it, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindServiceConnectionIfBounded()
        indefeniteSnackBar?.dismiss()
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
                progressDialog?.message?.text = "Connected. Unlocking..."
                bluetoothConnectService?.let {
                    beacon = BeaconImpl(this, taskProcessor)
                } ?: throw IllegalStateException(
                    "Something went wrong, connection service uninitialized!"
                )

                beacon.requestDeviceLockStatus(this)
            }
            BluetoothGattCallback.ACTION_GATT_DISCONNECTED -> {
                progressDialog?.dismiss()
                Timber.d("Gatt disconnected: ${payload.payload}")
                showTryAgainToConnectSnackBar()
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
                    Timber.d(Gson().toJson(beacon.toJson()))
                    beaconViewModel.beacon.value = beacon
                    tabs.visibility = View.VISIBLE
                }

                override fun execute(bluetooth: Bluetooth): Boolean {
                    return true
                }
            })
        } else {
            progressDialog?.apply {
                message.text = "Need password"
            }?.window?.decorView?.postDelayed({
                progressDialog?.dismiss()
                showPasswordDialog()
            }, 1500)
        }
    }

    override fun onError(e: Exception) {
    }

    private fun showPasswordDialog() {
        createPasswordDialog(object : OnPasswordDialogAction {
            override fun onPasswordEntered(password: String) {
                beacon.unlock(
                    password,
                    this@SettingsFrameworkActivity
                ) // TODO: Do this in a better way
                if (progressDialog?.isShowing == false) {
                    progressDialog = showProgressDialog().apply {
                        message.text = "Unlocking..."
                    }
                }
            }

            override fun onDialogCancelled() {
                showTryAgainPasswordSnackBar()
            }
        }).show()
    }

    private fun showTryAgainPasswordSnackBar() {
        indefeniteSnackBar = ll_settings_root.snack(
            "Cannot connect without a password!",
            Snackbar.LENGTH_INDEFINITE
        ) {
            action(
                "try again",
                ContextCompat.getColor(applicationContext, R.color.primaryColor)
            ) {
                showPasswordDialog()
            }
        }
    }


    private fun showTryAgainToConnectSnackBar() {
        indefeniteSnackBar = ll_settings_root.snack("Error occurred", Snackbar.LENGTH_INDEFINITE) {
            action(
                "try again",
                ContextCompat.getColor(applicationContext, R.color.primaryColor)
            ) {
                bluetoothConnectService?.let { connectToDevice(it) }
                    ?: showToast("Oops. Can't do anything")
            }
        }
    }

    private fun unbindServiceConnectionIfBounded() {
        if (isServiceBinded) {
            unbindService(serviceConnection)
            isServiceBinded = false;
        }
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
        builder.setCancelable(false)
        builder.setOnCancelListener { onPasswordDialogAction.onDialogCancelled() }
        return builder.create()
    }

    private fun showProgressDialog(): AlertDialog {
        return AlertDialog.Builder(this).apply {
            setView(
                layoutInflater.inflate(
                    R.layout.dialog_indeterminate_progress,
                    null as ViewGroup?
                )
            )
        }.create().apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }.also {
            if (!isFinishing and !isDestroyed) {
                it.show()
            }
        }
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