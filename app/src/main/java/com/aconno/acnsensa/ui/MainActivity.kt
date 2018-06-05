package com.aconno.acnsensa.ui

import android.arch.lifecycle.Observer
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.BluetoothScanningService
import com.aconno.acnsensa.BuildConfig
import com.aconno.acnsensa.R
import com.aconno.acnsensa.dagger.mainactivity.DaggerMainActivityComponent
import com.aconno.acnsensa.dagger.mainactivity.MainActivityComponent
import com.aconno.acnsensa.dagger.mainactivity.MainActivityModule
import com.aconno.acnsensa.domain.scanning.BluetoothState
import com.aconno.acnsensa.domain.model.ScanEvent
import com.aconno.acnsensa.model.AcnSensaPermission
import com.aconno.acnsensa.ui.devices.SavedDevicesFragment
import com.aconno.acnsensa.ui.sensors.SensorListFragment
import com.aconno.acnsensa.ui.settings.PublishListActivity
import com.aconno.acnsensa.viewmodel.BluetoothScanningViewModel
import com.aconno.acnsensa.viewmodel.BluetoothViewModel
import com.aconno.acnsensa.viewmodel.PermissionViewModel
import kotlinx.android.synthetic.main.activity_toolbar.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), PermissionViewModel.PermissionCallbacks {

    @Inject
    lateinit var bluetoothViewModel: BluetoothViewModel

    @Inject
    lateinit var bluetoothScanningViewModel: BluetoothScanningViewModel

    @Inject
    lateinit var permissionViewModel: PermissionViewModel

    private var mainMenu: Menu? = null

    private var snackbar: Snackbar? = null

    val mainActivityComponent: MainActivityComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? = application as? AcnSensaApplication
        DaggerMainActivityComponent.builder()
            .appComponent(acnSensaApplication?.appComponent)
            .mainActivityModule(MainActivityModule(this))
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar)

        mainActivityComponent.inject(this)

        snackbar =
                Snackbar.make(content_container, R.string.bt_disabled, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.enable) { bluetoothViewModel.enableBluetooth() }

        snackbar?.setActionTextColor(resources.getColor(R.color.primaryColor))

        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)

        invalidateOptionsMenu()

        if (savedInstanceState == null) {
            showBeaconsFragment()
        }
    }

    override fun onResume() {
        super.onResume()

        bluetoothScanningViewModel.getResult().observe(this, Observer { handleScanEvent(it) })
        bluetoothViewModel.observeBluetoothState()
        bluetoothViewModel.bluetoothState.observe(this, Observer { onBluetoothStateChange(it) })

    }

    override fun onPause() {
        super.onPause()
        bluetoothViewModel.stopObservingBluetoothState()
    }

    private fun onBluetoothStateChange(bluetoothState: BluetoothState?) {
        when (bluetoothState?.state) {
            BluetoothState.BLUETOOTH_OFF -> onBluetoothOff()
            BluetoothState.BLUETOOTH_ON -> onBluetoothOn()
        }
    }

    private fun onBluetoothOff() {
        mainMenu?.let {
            val menuItem: MenuItem? = it.findItem(R.id.action_toggle_scan)
            menuItem?.setVisible(false)
        }
        snackbar?.show()
    }

    private fun onBluetoothOn() {
        mainMenu?.let {
            val menuItem: MenuItem? = it.findItem(R.id.action_toggle_scan)
            menuItem?.setVisible(true)
        }
        snackbar?.dismiss()
    }

    private fun handleScanEvent(scanEvent: ScanEvent?) {
        val eventType: Int? = scanEvent?.type
        when (eventType) {
            ScanEvent.SCAN_FAILED_ALREADY_STARTED -> onScanFailedAlreadyStarted()
            ScanEvent.SCAN_FAILED -> onScanFailed()
            ScanEvent.SCAN_START -> onScanStart()
            ScanEvent.SCAN_STOP -> onScanStop()
        }
    }

    private fun onScanFailedAlreadyStarted() {
        //Do nothing.
    }

    private fun onScanFailed() {
        onScanStop()
    }

    private fun onScanStart() {
        mainMenu?.let {
            val menuItem: MenuItem? = it.findItem(R.id.action_toggle_scan)
            menuItem?.let {
                it.isChecked = true
                it.setTitle(getString(R.string.stop_scan))
            }
        }
    }

    private fun onScanStop() {
        mainMenu?.let {
            val menuItem: MenuItem? = it.findItem(R.id.action_toggle_scan)
            menuItem?.let {
                it.isChecked = false
                it.setTitle(getString(R.string.start_scan))
            }
        }
    }

    fun showSensorValues(macAddress: String) {
        supportFragmentManager.beginTransaction()
            .replace(content_container.id, SensorListFragment.newInstance(macAddress))
            .addToBackStack(null)
            .commit()
    }

    private fun showBeaconsFragment() {
        supportFragmentManager.beginTransaction()
            .replace(content_container.id, SavedDevicesFragment.newInstance())
            .commit()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        mainMenu = menu
        mainMenu?.clear()
        menuInflater.inflate(R.menu.main_menu, menu)

        mainMenu?.findItem(R.id.action_toggle_scan)?.let {
            setScanMenuLabel(it)
            val state = bluetoothViewModel.bluetoothState.value
            when (state?.state) {
                BluetoothState.BLUETOOTH_ON -> it.setVisible(true)
                else -> it.setVisible(false)
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id: Int? = item?.itemId
        when (id) {
            R.id.action_toggle_scan -> toggleScan(item)
            R.id.action_start_actions_activity -> startActionListActivity()
            R.id.action_start_settings_activity -> startSettingsActivity()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun startActionListActivity() {
        ActionListActivity.start(this)
    }

    private fun startSettingsActivity() {
        if (BluetoothScanningService.isRunning()) {
            Snackbar.make(
                findViewById(android.R.id.content),
                getString(R.string.snackbar_stop_scanning),
                Snackbar.LENGTH_SHORT
            ).show()
        } else {
            PublishListActivity.start(this)
        }
    }

    private fun toggleScan(item: MenuItem?) {
        item?.let {
            if (item.isChecked) {
                bluetoothScanningViewModel.stopScanning()
            } else {
                permissionViewModel.requestAccessFineLocation()
            }
        }
    }

    private fun setScanMenuLabel(menuItem: MenuItem) {
        if (BluetoothScanningService.isRunning()) {
            menuItem.title = getString(R.string.stop_scan)
            menuItem.isChecked = true
        } else {
            menuItem.title = getString(R.string.start_scan)
            menuItem.isChecked = false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionViewModel.checkGrantedPermission(grantResults, requestCode)
    }

    override fun permissionAccepted(actionCode: Int) {
        if (actionCode == AcnSensaPermission.ACCESS_FINE_LOCATION.code) {
            // TODO: Why do we need READ_EXTERNAL_STORAGE for scanning??? @Sergio
            permissionViewModel.requestAccessToReadExternalStorage()
        } else {
            bluetoothScanningViewModel.startScanning()
        }
    }

    override fun permissionDenied(actionCode: Int) {
        //TODO: Make this nice...
        Snackbar.make(
            content_container,
            getString(R.string.snackbar_permission_message),
            Snackbar.LENGTH_LONG
        ).setAction(getString(R.string.snackbar_settings)) {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
            startActivity(intent)
        }.setActionTextColor(ContextCompat.getColor(this, R.color.primaryColor))
            .show()
    }

    override fun showRationale(actionCode: Int) {
        //TODO: Show rationale
    }
}