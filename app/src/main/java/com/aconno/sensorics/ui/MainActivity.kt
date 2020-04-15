package com.aconno.sensorics.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.work.*
import com.aconno.sensorics.*
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.scanning.BluetoothState
import com.aconno.sensorics.domain.scanning.ScanEvent
import com.aconno.sensorics.ui.dashboard.DashboardFragment
import com.aconno.sensorics.ui.device_main.DeviceMainFragment
import com.aconno.sensorics.ui.devices.SavedDevicesFragment
import com.aconno.sensorics.ui.devices.SavedDevicesFragmentListener
import com.aconno.sensorics.ui.dialogs.ScannedDevicesDialogListener
import com.aconno.sensorics.ui.livegraph.LiveGraphFragment
import com.aconno.sensorics.ui.livegraph.LiveGraphOpener
import com.aconno.sensorics.ui.settings.SettingsActivity
import com.aconno.sensorics.viewmodel.BluetoothScanningViewModel
import com.aconno.sensorics.viewmodel.BluetoothViewModel
import com.aconno.sensorics.viewmodel.MqttVirtualScanningViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_toolbar.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), EasyPermissions.PermissionCallbacks,
        ScannedDevicesDialogListener, SavedDevicesFragmentListener, LiveGraphOpener,
    SavedDevicesFragment.ItemSelectionStateListener{

    @Inject
    lateinit var bluetoothStateReceiver: BluetoothStateReceiver

    @Inject
    lateinit var bluetoothViewModel: BluetoothViewModel

    @Inject
    lateinit var bluetoothScanningViewModel: BluetoothScanningViewModel

    @Inject
    lateinit var mqttVirtualScanningViewModel: MqttVirtualScanningViewModel

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private var mainMenu: Menu? = null

    private lateinit var bluetoothStatusSnackbar: Snackbar

    private var filterByDevice: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar)

        bluetoothStatusSnackbar = Snackbar.make(
            content_container,
            R.string.bt_disabled,
            Snackbar.LENGTH_INDEFINITE
        ).setAction(R.string.enable) {
            bluetoothViewModel.enableBluetooth()
        }.setActionTextColor(ContextCompat.getColor(this, R.color.primaryColor))

        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            showSavedDevicesFragment()
        }

        scheduleWork()
        observeScanEvents()
    }

    private fun scheduleWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val build = PeriodicWorkRequestBuilder<SyncConfigurationWorker>(15, TimeUnit.MINUTES)
            .addTag(WORK_TAG)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, build)
    }

    override fun onResume() {
        super.onResume()

        val keepScreenOn = sharedPreferences.getBoolean("keep_screen_on", false)
        if (keepScreenOn) {
            //Enable Keep Screen On
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            //Disable Keep Screen On
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        registerReceiver(bluetoothStateReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        bluetoothViewModel.observeBluetoothState()
        bluetoothViewModel.bluetoothState.observe(this, Observer { onBluetoothStateChange(it) })
    }

    override fun onPause() {
        super.onPause()
        bluetoothViewModel.stopObservingBluetoothState()
        unregisterReceiver(bluetoothStateReceiver)
    }

    private fun onBluetoothStateChange(bluetoothState: BluetoothState?) {
        when (bluetoothState) {
            BluetoothState.BLUETOOTH_OFF -> onBluetoothOff()
            BluetoothState.BLUETOOTH_ON -> onBluetoothOn()
        }
    }

    private fun onBluetoothOff() {
        mainMenu?.findItem(R.id.action_toggle_scan)?.isVisible = false
        bluetoothStatusSnackbar.show()

        //Hide FAB
        (supportFragmentManager.findFragmentById(content_container.id)
                as? SavedDevicesFragment)?.onBluetoothOff()
    }

    private fun onBluetoothOn() {
        mainMenu?.findItem(R.id.action_toggle_scan)?.isVisible = true
        bluetoothStatusSnackbar.dismiss()

        //Show FAB
        (supportFragmentManager.findFragmentById(content_container.id)
                as? SavedDevicesFragment)?.onBluetoothOn()
    }

    override fun openLiveGraph(macAddress: String, sensorName: String) {
        supportFragmentManager.beginTransaction().add(
            content_container.id,
            LiveGraphFragment.newInstance(macAddress, sensorName)
        ).addToBackStack(null).commit()
    }

    override fun onFABClicked() {
        stopScanning()
        startScanning(false)
    }

    override fun onDialogDismissed() {
        stopScanning()
    }

    private fun observeScanEvents() {
        bluetoothScanningViewModel.getScanEvent()
            .observe(this, Observer { handleScanEvent(it) })
    }

    private fun handleScanEvent(scanEvent: ScanEvent?) {
        Timber.i("Scan event, message: ${scanEvent?.message}")
        when (scanEvent?.type) {
            ScanEvent.SCAN_START -> onScanStart()
            ScanEvent.SCAN_STOP -> onScanStop()
            ScanEvent.SCAN_FAILED_ALREADY_STARTED -> onScanFailedAlreadyStarted()
            ScanEvent.SCAN_FAILED -> onScanFailed()
        }
    }

    private fun onScanStart() {
        mainMenu?.findItem(R.id.action_toggle_scan)?.let {
            it.isChecked = true
            it.setTitle(getString(R.string.stop_scan))
        }
    }

    private fun onScanStop() {
        mainMenu?.findItem(R.id.action_toggle_scan)?.let {
            it.isChecked = false
            it.setTitle(getString(R.string.start_scan))
        }
    }

    private fun onScanFailedAlreadyStarted() {
        Snackbar.make(
            findViewById(android.R.id.content),
            getString(R.string.snackbar_scan_failed_already_started),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun onScanFailed() {
        Snackbar.make(
            findViewById(android.R.id.content),
            getString(R.string.snackbar_scan_failed),
            Snackbar.LENGTH_SHORT
        ).show()
        onScanStop()
    }

    //call the html page
    fun showSensorValues(device: Device) {
        supportFragmentManager.beginTransaction()
            .replace(content_container.id, getReadingListFragment(device))
            .addToBackStack(null)
            .commit()
    }

    private fun getReadingListFragment(device: Device): Fragment {
        if (device.connectable) {
            stopScanning()
            mainMenu?.findItem(R.id.action_toggle_scan)?.isChecked = false
        }

        return DeviceMainFragment.newInstance(device)
    }

    private fun showSavedDevicesFragment() {
        supportFragmentManager.beginTransaction()
            .replace(content_container.id, SavedDevicesFragment())
            .commit()
    }

    override fun onDevicesDialogItemClick(item: Device) {
        supportFragmentManager.fragments
            .filterIsInstance(ScannedDevicesDialogListener::class.java)
            .forEach { it.onDevicesDialogItemClick(item) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        mainMenu = menu
        menuInflater.inflate(R.menu.main_menu, menu)

        mainMenu?.findItem(R.id.action_toggle_scan)?.let {
            setScanMenuLabel(it)
            it.isVisible = bluetoothViewModel.bluetoothState.value == BluetoothState.BLUETOOTH_ON
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_toggle_scan -> {
                toggleScanFromMenuItem(item)
                return true
            }
            R.id.action_start_settings_activity -> {
                startSettingsActivity()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
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

    private fun toggleScanFromMenuItem(item: MenuItem) {
        if (item.isChecked) {
            stopScanning()
        } else {
            startScanning()
        }
    }

    private fun startScanning(filterByDevice: Boolean = true) {
        this.filterByDevice = filterByDevice
        startScanning()
    }

    private fun stopScanning() {
        bluetoothScanningViewModel.stopScanning()
        mqttVirtualScanningViewModel.stopScanning()
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
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(SCANNING_PERMISSION_REQUEST_CODE)
    private fun startScanning() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (EasyPermissions.hasPermissions(this, *permissions)) {
            bluetoothScanningViewModel.startScanning(filterByDevice)
            mqttVirtualScanningViewModel.startScanning()
            filterByDevice = true
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.scanning_permission_rationale),
                SCANNING_PERMISSION_REQUEST_CODE,
                *permissions
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Timber.d("Permissions denied, request code: $requestCode, permissions: $perms")
        Snackbar.make(content_container, R.string.snackbar_permission_message, Snackbar.LENGTH_LONG)
            .setAction(R.string.snackbar_settings) {
                startActivity(
                    Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                    }
                )
            }
            .setActionTextColor(ContextCompat.getColor(this, R.color.primaryColor))
            .show()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Timber.d("Permissions granted, request code: $requestCode, permissions: $perms")
    }


    fun onDashboardClicked() {
        supportFragmentManager.beginTransaction()
            .replace(
                content_container.id,
                DashboardFragment.newInstance()
            )
            .addToBackStack(null)
            .commit()
    }

    fun onUseCaseClicked(macAddress: String, deviceName: String) {
        supportFragmentManager.beginTransaction()
            .replace(
                content_container.id,
                UseCasesFragment.newInstance(macAddress, deviceName)
            )
            .addToBackStack(null)
            .commit()
    }

    fun isScanning(): Boolean {
        return BluetoothScanningService.isRunning()
    }

    fun startScanOperation() {
        mainMenu?.findItem(R.id.action_toggle_scan)?.let { item ->
            toggleScanFromMenuItem(item)
        }
    }

    fun stopScanOperation() {
        if (isScanning()) {
            changeToggleState()
            stopScanning()
        }
    }

    private fun changeToggleState() {
        mainMenu?.findItem(R.id.action_toggle_scan)?.isChecked = false
    }

    companion object {
        const val WORK_TAG = "RESOURCE_SYNC_WORKER"
        const val WORK_NAME = "Resource Synchronization"

        private const val SCANNING_PERMISSION_REQUEST_CODE = 65
    }

    override fun onBackPressed() {
        val devicesFragment = supportFragmentManager
            .findFragmentById(R.id.content_container) as SavedDevicesFragment?

        val handled = devicesFragment?.onBackButtonPressed() ?: false
        if (!handled) super.onBackPressed()
    }

    override fun onItemSelectionStateEntered() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelectionStateExited() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSelectedItemsCountChanged(selectedItems: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}