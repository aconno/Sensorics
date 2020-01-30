package com.aconno.sensorics.ui.settings_framework

import android.app.Activity
import android.app.Dialog
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
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
import com.aconno.sensorics.ui.dialogs.CancelBtnSchedulerProgressDialog
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_configure.*
import timber.log.Timber
import javax.inject.Inject

// TODO this activity is too complicated. Need refactoring (maybe put bluetooth logic in viewmodel and
//  make dialog creator classes)
class SettingsFrameworkActivity : DaggerAppCompatActivity(), LockStateRequestCallback,
    BeaconGeneralFragmentListener {
    private var bluetoothConnectService: BluetoothConnectService? = null
    private var connectResultDisposable: Disposable? = null
    private lateinit var macAddress: String
    private lateinit var taskProcessor: BluetoothTaskProcessor
    private var beacon: Beacon? = null
    private val gson: Gson = Gson()
    private var retries: Int = 0
    private val handler: Handler = Handler()

    @Inject
    lateinit var beaconViewModel: BeaconSettingsViewModel

    private var progressDialog: CancelBtnSchedulerProgressDialog? = null
    private var passwordDialog: Dialog? = null
    private var indefiniteSnackBar: Snackbar? = null
    var device: String = ""
    private var isConnectionServiceRegistered = false

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
                connectToDevice(bluetoothService)
            }
        }
    }

    private fun connectToDevice(bluetoothService: BluetoothConnectService) {
        bluetoothService.connect(macAddress)

        showProgressDialogIfNeeded()
        progressDialog?.progressMessage = getString(R.string.connecting_with_dots)
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
        when (item.itemId) {
            R.id.item_save -> saveChanges()
            else -> return false
        }
        return true
    }

    private fun saveChanges() {
        val intent = Intent(SAVE_CHANGES_BROADCAST)
        intent.putExtra(TARGET_FRAGMENT_ID_BROADCAST_EXTRA, vp_beacon.currentItem)
        LocalBroadcastManager.getInstance(this@SettingsFrameworkActivity).sendBroadcast(intent)
    }


    override fun onStart() {
        super.onStart()
        cancelScheduleEvents()
        bindServices()
    }

    private fun cancelScheduleEvents() = handler.removeCallbacksAndMessages(null)

    private fun bindServices() {
        registerConnectionServiceIfNeed()

        LocalBroadcastManager.getInstance(this).registerReceiver(
            beaconRequestBroadcastReceiver,
            IntentFilter(BEACON_JSON_REQUEST_BROADCAST)
        )
    }

    private fun registerConnectionServiceIfNeed() {
        if (!isConnectionServiceRegistered) {
            Intent(applicationContext, BluetoothConnectService::class.java).let {
                bindService(it, serviceConnection, Context.BIND_AUTO_CREATE)
                isConnectionServiceRegistered = true
            }
        }
    }

    private fun unregisterConnectionServiceIfNeed() {
        if (isConnectionServiceRegistered) {
            unbindService(serviceConnection)
            isConnectionServiceRegistered = false
        }
    }

    override fun onStop() {
        super.onStop()
        cancelScheduleEvents()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(beaconRequestBroadcastReceiver)
        scheduleCleaningServiceAndScreen(CLEAN_TIMER)
    }

    private fun scheduleCleaningServiceAndScreen(timeMs: Long) {
        handler.postDelayed({
            unregisterConnectionServiceIfNeed()
            closeProgressDialog()
            closePasswordDialog()
            clearPages()
            tabs.visibility = View.INVISIBLE
        }, timeMs)
    }

    private fun clearPages() {
        beaconSettingsPagerAdapter.clear()
        beaconSettingsPagerAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        cancelScheduleEvents()
        unregisterConnectionServiceIfNeed()
        super.onDestroy()
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

                retries = 0

                progressDialog?.progressMessage = getString(R.string.connected_unlocking)
                bluetoothConnectService?.let {
                    beacon = BeaconImpl(this, taskProcessor)
                } ?: throw IllegalStateException(
                    "Something went wrong, connection service uninitialized!"
                )

                beacon?.requestDeviceLockStatus(this)
            }
            BluetoothGattCallback.ACTION_GATT_DISCONNECTED -> {
                closeProgressDialog()
                if ((payload.payload as Int) == 0x85 && retries < MAX_RETRIES) { // TODO: Something smarter maybe?
                    Toast.makeText(
                        this,
                        "Failure...",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
//                    handler.postDelayed({
//                        bluetoothConnectService?.let {
//                            connectToDevice(it)
//                        }
//                        retries++
//                    }, 3000)
                } else {
                    Toast.makeText(
                        this,
                        "Disconnected: ${payload.payload}",
                        Toast.LENGTH_LONG
                    ).show()
                    finish() // TODO: Allow reconnecting in the future but service needs to be reworked
                }
            }
            else -> {
                return
            }
        }
    }

    private fun sendBeaconInfoBroadcast() {
        beacon?.let {
            val intent = Intent(BEACON_JSON_BROADCAST)
            intent.putExtra(BEACON_JSON_BROADCAST_EXTRA, gson.toJson(it.toJson()))
            LocalBroadcastManager.getInstance(this@SettingsFrameworkActivity).sendBroadcast(intent)
        }
    }

    override fun onDeviceLockStateRead(unlocked: Boolean) {
        if (unlocked) {
            Toast.makeText(
                this,
                R.string.device_unlocked,
                Toast.LENGTH_LONG
            ).show()

            beacon?.read(object : GenericTask("On Read Completed Task") {
                override fun onSuccess() {
                    beaconViewModel.beacon.value = beacon
                    closeProgressDialog()
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
            progressDialog?.progressMessage = getString(R.string.reading_with_dots)
        } else {
            progressDialog?.apply {
                progressMessage = getString(R.string.password_required)
            }?.window?.decorView?.postDelayed({
                closeProgressDialog()
                showPasswordDialog()
            }, 1500)
        }
    }

    private fun closeProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    private fun closePasswordDialog() {
        passwordDialog?.dismiss()
        progressDialog = null
    }

    override fun onError(e: Exception) {
    }

    private fun showPasswordDialog() {
        passwordDialog = createPasswordDialog(object : OnPasswordDialogAction {
            override fun onPasswordEntered(password: String) {
                hideKeyboard()
                beacon?.unlock(
                    password,
                    this@SettingsFrameworkActivity
                ) // TODO: Do this in a better way
                showProgressDialogIfNeeded()
                progressDialog?.progressMessage = getString(R.string.ulocking_with_dots)
            }

            override fun onDialogCancelled() {
                hideKeyboard()
                showTryAgainPasswordSnackBar()
            }
        }).apply {
            show()
        }
    }

    private fun showProgressDialogIfNeeded() {
        if (progressDialog?.isShowing != true) {
            progressDialog = CancelBtnSchedulerProgressDialog(
                this@SettingsFrameworkActivity,
                handler
            ) {
                if (notDisconnectedFromDevice()) {
                    // This might be invoke gatt disconnected event and trigger try again snackbar
                    bluetoothConnectService?.disconnect()
                } else {
                    showTryAgainToConnectSnackBar()
                }
            }.apply {
                show()
            }
        }

    }

    private fun notDisconnectedFromDevice() =
        bluetoothConnectService?.getConnectResultsLiveData()?.value?.action != BluetoothGattCallback.ACTION_GATT_DISCONNECTED

    private fun showTryAgainPasswordSnackBar() {
        indefiniteSnackBar = ll_settings_root.snack(
            R.string.cant_connect_without_password,
            Snackbar.LENGTH_INDEFINITE
        ) {
            action(
                R.string.try_again,
                ContextCompat.getColor(applicationContext, R.color.primaryColor)
            ) {
                showPasswordDialog()
            }
        }
    }


    private fun showTryAgainToConnectSnackBar() {
        indefiniteSnackBar =
            ll_settings_root.snack(R.string.error_occurred, Snackbar.LENGTH_INDEFINITE) {
                action(
                    R.string.try_again,
                    ContextCompat.getColor(applicationContext, R.color.primaryColor)
                ) {
                    bluetoothConnectService?.let { connectToDevice(it) }
                        ?: showToast(R.string.oops_cant_do_anything)
                }
            }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    private fun createPasswordDialog(onPasswordDialogAction: OnPasswordDialogAction): AlertDialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val view: View = layoutInflater.inflate(R.layout.dialog_password, null as ViewGroup?)
        builder.setView(view)
        val etPassword = view.findViewById<EditText>(R.id.et_password)
        builder.setPositiveButton(R.string.confirm) { dialog, _ ->
            onPasswordDialogAction.onPasswordEntered(etPassword?.text.toString())
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            onPasswordDialogAction.onDialogCancelled()
            dialog.dismiss()
        }
        builder.setCancelable(false)
        builder.setOnCancelListener { onPasswordDialogAction.onDialogCancelled() }
        return builder.create().apply {
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
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
        const val TARGET_FRAGMENT_ID_BROADCAST_EXTRA = "TARGET_FRAGMENT_ID_BROADCAST_EXTRA"
        const val SAVE_CHANGES_BROADCAST = "com.aconno.sensorics.settings.SAVE_CHANGES_BROADCAST"
        const val BEACON_JSON_BROADCAST =
            "com.aconno.sensorics.settings.BEACON_JSON_RESPONSE_BROADCAST"
        const val BEACON_JSON_REQUEST_BROADCAST =
            "com.aconno.sensorics.settings.BEACON_JSON_REQUEST_BROADCAST"
        const val BEACON_JSON_BROADCAST_EXTRA = "BEACON_JSON_BROADCAST_EXTRA"

        private const val MAX_RETRIES = 3
        private const val CLEAN_TIMER = 15000L
        private const val DEVICE_MAC_ADDRESS = "device_mac_address"
        fun start(context: Context, deviceMacAddress: String) {
            Intent(context, SettingsFrameworkActivity::class.java).also {
                it.putExtra(DEVICE_MAC_ADDRESS, deviceMacAddress)
                context.startActivity(it)
            }
        }
    }


    override fun updateFirmware() {
        showNotImplementedToast()
    }

    override fun resetFactory() {
        showNotImplementedToast()
    }

    override fun powerOff() {
        showNotImplementedToast()
    }

    override fun addPassword() {
        showNotImplementedToast()
    }

    private fun showNotImplementedToast() {
        showToast(R.string.feature_not_implemented)
    }
}