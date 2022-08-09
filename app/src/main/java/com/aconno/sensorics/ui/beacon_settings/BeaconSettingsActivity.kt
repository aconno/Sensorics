package com.aconno.sensorics.ui.beacon_settings

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.aconno.sensorics.*
import com.aconno.sensorics.dagger.beacon_settings.BeaconSettingsFragmentListener
import com.aconno.sensorics.ui.beacon_settings.fragments.BeaconSettingsFragment
import com.aconno.sensorics.ui.dialogs.CancelBtnSchedulerProgressDialog
import com.aconno.sensorics.ui.dialogs.PasswordDialog
import com.aconno.sensorics.viewmodel.BeaconSettingsState
import com.aconno.sensorics.viewmodel.BeaconSettingsTransporterSharedViewModel
import com.aconno.sensorics.viewmodel.BeaconSettingsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_settings_framework.*
import timber.log.Timber
import javax.inject.Inject

class BeaconSettingsActivity : DaggerAppCompatActivity(), BeaconSettingsFragmentListener {

    private val handler: Handler = Handler(Looper.myLooper() ?: Looper.getMainLooper())
    private var progressDialog: CancelBtnSchedulerProgressDialog? = null
    private var passwordDialog: Dialog? = null
    private var indefiniteSnackBar: Snackbar? = null
    private var deviceMac: String = ""

    @Inject
    lateinit var settingsTransporter: BeaconSettingsTransporterSharedViewModel

    @Inject
    lateinit var viewModel: BeaconSettingsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_framework)

        if (savedInstanceState == null) {
            if (supportFragmentManager.findFragmentById(R.id.content_container) == null) {
                BeaconSettingsFragment.newInstance().let { fragment ->
                    supportFragmentManager.beginTransaction()
                        .add(R.id.content_container, fragment)
                        .commit()
                }
            }
        }


        setupUI()
        subscribeOnData()
        deviceMac = intent?.extras?.getString(DEVICE_MAC_ADDRESS)
            ?: throw throw IllegalArgumentException("Device not provided.")
    }

    override fun onStart() {
        super.onStart()
        // Some events could be scheduled in onStop
        cancelScheduleEvents()
        if (viewModel.isFirstTime) {
            viewModel.connect(deviceMac)
        }
    }

    override fun onStop() {
        super.onStop()
        // Some events, such as dialog appearing could be scheduled
        cancelScheduleEvents()
        scheduleCleaningServiceAndScreen(CLEAN_TIMER)
    }

    override fun onDestroy() {
        cancelScheduleEvents()
        closeAllDialogs()
        if (isFinishing) {
            disconnectFromDeviceIfNeeded()
        }
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.beacon_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_save -> {
                if (viewModel.isConnected) {
                    viewModel.writeSettingsToDevice()
                } else {
                    showToast(R.string.device_not_connected)
                }
            }
            else -> return false
        }
        return true
    }

    private fun setupUI() {
        setSupportActionBar(toolbar)
        /*Set titlebar */
        supportActionBar?.subtitle = deviceMac

        /*Set titlebar */
        supportActionBar?.subtitle = deviceMac

    }

    private fun subscribeOnData() {
        viewModel.connectionResultEvent.observe(this, {
            it?.let { event ->
                newEventReceived(event)
            }
        })
        viewModel.beaconLiveData.observe(this, {
            it?.let { beaconData ->
                Timber.i("handle beacon data. slots count ${beaconData.slotCount}")
                settingsTransporter.sendBeaconJsonToObservers(beaconData.beaconJson)
            }
        })
        settingsTransporter.beaconUpdatedJsonLiveDataForActivity.observe(this, {
            it?.let { beaconJson ->
                viewModel.beaconJsonUpdated(beaconJson)
            }
        })
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun newEventReceived(event: BeaconSettingsState) {
        Timber.i("handle event $event")
        when (event) {
            is BeaconSettingsState.Connecting -> getProgressDialog {
                progressMessage = getString(R.string.connecting_with_dots)
            }

            is BeaconSettingsState.Connected -> getProgressDialog {
                progressMessage = getString(R.string.connected_with_dots)
            }

            is BeaconSettingsState.Unlocking -> getProgressDialog {
                progressMessage = getString(R.string.connected_unlocking)
            }

            is BeaconSettingsState.Unlocked -> {
                showToast(R.string.device_unlocked)
                getProgressDialog {
                    progressMessage = getString(R.string.device_unlocked)
                }
            }
            is BeaconSettingsState.Reading -> getProgressDialog {
                progressMessage = getString(R.string.reading_with_dots)
            }

            is BeaconSettingsState.RequireUnlock -> {
                getProgressDialog {
                    progressMessage = getString(R.string.password_required)
                }
                handler.postDelayed({
                    closeProgressDialog()
                    showPasswordDialog()
                }, 400)
            }
            is BeaconSettingsState.Writing -> getProgressDialog {
                progressMessage = getString(R.string.writing_with_dots)
            }

            is BeaconSettingsState.SettingsWritten -> {
                closeProgressDialog()
                showToast(R.string.settings_has_been_written_successfully)
                finish()
            }
            is BeaconSettingsState.Done -> {
                closeProgressDialog()
            }
            is BeaconSettingsState.ErrorOccurred -> {
                showToast(R.string.error_occurred)
            }
            is BeaconSettingsState.Disconnected -> {
                closeProgressDialog()
                closePasswordDialog()
                showTryAgainToConnectSnackBar()
            }
        }.exhaustive
    }

    private fun cancelScheduleEvents() = handler.removeCallbacksAndMessages(null)

    private fun scheduleCleaningServiceAndScreen(timeMs: Long) {
        handler.postDelayed({
            disconnectFromDeviceIfNeeded()
            closeAllDialogs()
        }, timeMs)
    }

    private fun disconnectFromDeviceIfNeeded() {
        if (viewModel.isConnected) {
            viewModel.disconnect()
        }
    }


    private fun closeAllDialogs() {
        closeProgressDialog()
        closePasswordDialog()
        indefiniteSnackBar?.dismiss()
        indefiniteSnackBar = null
    }

    private fun getProgressDialog(doAfter: CancelBtnSchedulerProgressDialog.() -> Unit) {
        if (progressDialog == null) {
            progressDialog = CancelBtnSchedulerProgressDialog(
                this@BeaconSettingsActivity,
                handler
            ) {
                viewModel.disconnect()
            }.apply {
                show()
            }
        }
        doAfter(progressDialog!!)
    }

    private fun closeProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    private fun closePasswordDialog() {
        passwordDialog?.dismiss()
        progressDialog = null
    }

    private fun showPasswordDialog() {
        passwordDialog = PasswordDialog.create(
            context = this,
            action = object : PasswordDialog.OnPasswordDialogAction {
                override fun onPasswordEntered(password: String) {
                    hideKeyboard()
                    viewModel.unlockBeacon(password)
                }

                override fun onDialogCancelled() {
                    hideKeyboard()
                    showTryAgainPasswordSnackBar()
                }
            }).apply { show() }
    }

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
                    viewModel.connect(deviceMac)
                }
            }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
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

    companion object {
        private const val CLEAN_TIMER = 15000L
        private const val DEVICE_MAC_ADDRESS = "device_mac_address"
        fun start(context: Context, deviceMacAddress: String) {
            Intent(context, BeaconSettingsActivity::class.java).also {
                it.putExtra(DEVICE_MAC_ADDRESS, deviceMacAddress)
                context.startActivity(it)
            }
        }
    }
}