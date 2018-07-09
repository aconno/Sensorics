package com.aconno.sensorics.ui

import android.arch.lifecycle.Observer
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.BluetoothScanningService
import com.aconno.sensorics.BuildConfig
import com.aconno.sensorics.R
import com.aconno.sensorics.dagger.mainactivity.DaggerMainActivityComponent
import com.aconno.sensorics.dagger.mainactivity.MainActivityComponent
import com.aconno.sensorics.dagger.mainactivity.MainActivityModule
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.ScanEvent
import com.aconno.sensorics.domain.scanning.BluetoothState
import com.aconno.sensorics.model.SensoricsPermission
import com.aconno.sensorics.ui.devices.SavedDevicesFragment
import com.aconno.sensorics.ui.devices.SavedDevicesFragmentListener
import com.aconno.sensorics.ui.dialogs.ScannedDevicesDialogListener
import com.aconno.sensorics.ui.readings.GenericReadingListFragment
import com.aconno.sensorics.ui.sensors.SensorListFragment
import com.aconno.sensorics.ui.settings.SettingsActivity
import com.aconno.sensorics.viewmodel.BluetoothScanningViewModel
import com.aconno.sensorics.viewmodel.BluetoothViewModel
import com.aconno.sensorics.viewmodel.PermissionViewModel
import kotlinx.android.synthetic.main.activity_toolbar.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), PermissionViewModel.PermissionCallbacks,
    ScannedDevicesDialogListener, SavedDevicesFragmentListener {

    @Inject
    lateinit var bluetoothViewModel: BluetoothViewModel

    @Inject
    lateinit var bluetoothScanningViewModel: BluetoothScanningViewModel

    @Inject
    lateinit var permissionViewModel: PermissionViewModel

    private var mainMenu: Menu? = null

    private var snackbar: Snackbar? = null

    private var filterByDevice: Boolean = true

    val mainActivityComponent: MainActivityComponent by lazy {
        val sensoricsApplication: SensoricsApplication? = application as? SensoricsApplication
        DaggerMainActivityComponent.builder()
            .appComponent(sensoricsApplication?.appComponent)
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
            showSavedDevicesFragment()
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

    override fun onFABClicked() {
        mainMenu?.findItem(R.id.action_toggle_scan)?.let {
            if (!it.isChecked) {
                toggleScan(it)
                filterByDevice = false
            }
        }
    }

    override fun onDialogDismissed() {
        mainMenu?.findItem(R.id.action_toggle_scan)?.let {
            if (it.isChecked) {
                toggleScan(it)
            }
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

    fun showSensorValues(device: Device) {
        supportFragmentManager.beginTransaction()
            .replace(
                content_container.id,
                getReadingListFragment(device)
            )
            .addToBackStack(null)
            .commit()
    }

    private fun getReadingListFragment(device: Device): Fragment {
        return when (device.name) {
            "AcnSensa" -> SensorListFragment.newInstance(device.macAddress)
            else -> GenericReadingListFragment.newInstance(device.macAddress)
        }
    }

    private fun showSavedDevicesFragment() {
        supportFragmentManager.beginTransaction()
            .replace(content_container.id, SavedDevicesFragment())
            .commit()
    }

    override fun onDevicesDialogItemClick(item: Device) {
        supportFragmentManager.fragments.map {
            if (it is ScannedDevicesDialogListener) {
                it.onDevicesDialogItemClick(item)
            }
        }
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
            SettingsActivity.start(this)
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
        if (actionCode == SensoricsPermission.ACCESS_FINE_LOCATION.code) {
            // TODO: Why do we need READ_EXTERNAL_STORAGE for scanning??? @Sergio
            permissionViewModel.requestAccessToReadExternalStorage()
        } else {
            bluetoothScanningViewModel.startScanning(filterByDevice)
            filterByDevice = true
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