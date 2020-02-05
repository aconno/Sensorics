package com.aconno.sensorics.ui.settings_framework

import android.app.Activity
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
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
import com.aconno.sensorics.*
import com.aconno.sensorics.dagger.settings_framework.BeaconGeneralFragmentListener
import com.aconno.sensorics.device.beacon.Beacon
import com.aconno.sensorics.device.beacon.v2.BeaconImpl
import com.aconno.sensorics.device.bluetooth.BluetoothGattCallback
import com.aconno.sensorics.device.bluetooth.BluetoothTaskProcessorImpl
import com.aconno.sensorics.device.bluetooth.tasks.GenericTask
import com.aconno.sensorics.device.bluetooth.tasks.lock.LockStateRequestCallback
import com.aconno.sensorics.domain.model.GattCallbackPayload
import com.aconno.sensorics.domain.scanning.Bluetooth
import com.aconno.sensorics.domain.scanning.BluetoothTaskProcessor
import com.aconno.sensorics.model.mapper.WebViewAppBeaconMapper
import com.aconno.sensorics.ui.dialogs.CancelBtnSchedulerProgressDialog
import com.aconno.sensorics.viewmodel.SettingsActivitySharedViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonParser
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_settings_framework.*
import org.apache.commons.text.StringEscapeUtils
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

    private var progressDialog: CancelBtnSchedulerProgressDialog? = null
    private var passwordDialog: Dialog? = null
    private var indefiniteSnackBar: Snackbar? = null
    var device: String = ""
    private var isConnectionServiceRegistered = false

    private val beaconSettingsPagerAdapter: BeaconSettingsPagerAdapter by lazy {
        BeaconSettingsPagerAdapter(supportFragmentManager)
    }

    @Inject
    lateinit var settingsSharedViewModel: SettingsActivitySharedViewModel
    @Inject
    lateinit var webViewAppBeaconMapper: WebViewAppBeaconMapper

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

                connectToDevice(bluetoothService)
            }
        }
    }

    private fun connectToDevice(bluetoothService: BluetoothConnectService) {
        bluetoothService.startConnectionStream()

        bluetoothService.connect(macAddress)

        showProgressDialogIfNeeded()
        progressDialog?.progressMessage = getString(R.string.connecting_with_dots)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_framework)
        getDeviceMacAddress(savedInstanceState)

        device = intent?.extras?.getString(DEVICE_MAC_ADDRESS)
            ?: throw throw IllegalArgumentException("Device not provided.")

        setSupportActionBar(toolbar)

        setupUI()
        subscribeOnData()
    }

    private fun subscribeOnData() {
        settingsSharedViewModel.beaconUpdatedJsonLiveDataForActivity.observe(this, Observer {
            it?.let { beaconJson ->
                beacon?.run {
                    loadChangesFromJson(JsonParser().parse(beaconJson).asJsonObject)
                    sendBeaconInfoAndReturnToNormalState(alreadyPreparedForWebView = true)
                }
            }
        })
    }

    private fun sendBeaconInfoAndReturnToNormalState(alreadyPreparedForWebView: Boolean = false) {
        beacon?.let {
            if (!alreadyPreparedForWebView) {
                webViewAppBeaconMapper.prepareAdContentForWebView(it)
            }

            settingsSharedViewModel.sendBeaconJsonToObservers(
                StringEscapeUtils.escapeJson(gson.toJson(it.toJson()))
            )
            // again return slot advertisement content to normal state (represent data as binary)
            webViewAppBeaconMapper.prepareAdContentForApp(it)
        }
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
            R.id.item_save -> {
                //NOP
            }
            else -> return false
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        cancelScheduleEvents()
        registerConnectionServiceIfNeed()
    }

    private fun cancelScheduleEvents() = handler.removeCallbacksAndMessages(null)

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
                closePasswordDialog()
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
                R.string.device_unlocked,
                Toast.LENGTH_LONG
            ).show()

            beacon?.read(object : GenericTask("On Read Completed Task") {
                override fun onSuccess() {
                    closeProgressDialog()
                    Timber.d(Gson().toJson(beacon!!.toJson()))

                    onBeaconParamsLoaded()

                    tabs.visibility = View.VISIBLE
                    vp_beacon.visibility = View.VISIBLE

                    sendBeaconInfoAndReturnToNormalState()
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