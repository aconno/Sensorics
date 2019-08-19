package com.aconno.sensorics.ui

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.work.*
import com.aconno.sensorics.BluetoothScanningService
import com.aconno.sensorics.BuildConfig
import com.aconno.sensorics.R
import com.aconno.sensorics.SyncConfigurationWorker
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.scanning.BluetoothState
import com.aconno.sensorics.domain.scanning.ScanEvent
import com.aconno.sensorics.model.SensoricsPermission
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
import com.aconno.sensorics.viewmodel.PermissionViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_toolbar.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), PermissionViewModel.PermissionCallbacks,
    ScannedDevicesDialogListener, SavedDevicesFragmentListener, LiveGraphOpener {

    @Inject
    lateinit var bluetoothViewModel: BluetoothViewModel

    @Inject
    lateinit var bluetoothScanningViewModel: BluetoothScanningViewModel

    @Inject
    lateinit var permissionViewModel: PermissionViewModel

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private var mainMenu: Menu? = null

    private var snackbar: Snackbar? = null

    private var filterByDevice: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar)

        snackbar =
            Snackbar.make(content_container, R.string.bt_disabled, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.enable) { bluetoothViewModel.enableBluetooth() }

        snackbar?.setActionTextColor(ContextCompat.getColor(this, R.color.primaryColor))

        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)

        invalidateOptionsMenu()

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

        //Hide FAB
        val fragment = supportFragmentManager.findFragmentById(content_container.id)
        fragment?.let {
            if (it is SavedDevicesFragment) {
                it.onBluetoothOff()
            }
        }
    }

    private fun onBluetoothOn() {
        mainMenu?.let {
            val menuItem: MenuItem? = it.findItem(R.id.action_toggle_scan)
            menuItem?.setVisible(true)
        }
        snackbar?.dismiss()

        //Show FAB
        val fragment = supportFragmentManager.findFragmentById(content_container.id)
        fragment?.let {
            if (it is SavedDevicesFragment) {
                it.onBluetoothOn()
            }
        }
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
        mainMenu?.let { mainMenu ->
            val menuItem = mainMenu.findItem(R.id.action_toggle_scan)
            menuItem?.let {
                it.isChecked = true
                it.setTitle(getString(R.string.stop_scan))
            }
        }
    }

    private fun onScanStop() {
        mainMenu?.let { mainMenu ->
            val menuItem = mainMenu.findItem(R.id.action_toggle_scan)
            menuItem?.let {
                it.isChecked = false
                it.setTitle(getString(R.string.start_scan))
            }
        }
    }

    private fun onScanFailedAlreadyStarted() {
        // Do nothing TODO: Check if this is right
    }

    private fun onScanFailed() {
        onScanStop()
    }

    //call the html page
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
        if (device.connectable) {
            stopScanning()
            mainMenu?.findItem(R.id.action_toggle_scan)?.isChecked = false
        }

        return DeviceMainFragment.newInstance(
            device
        )
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
            R.id.action_toggle_scan -> toggleScanFromMenuItem(item)
            R.id.action_start_settings_activity -> startSettingsActivity()
        }

        return super.onOptionsItemSelected(item)
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

    fun startScanning(filterByDevice: Boolean = true) {
        this.filterByDevice = filterByDevice
        permissionViewModel.handlePermissionsRequest(
            SensoricsPermission.ACCESS_COARSE_LOCATION
        )
    }

    private fun stopScanning() {
        bluetoothScanningViewModel.stopScanning()
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
        permissionViewModel.checkGrantedPermissions(requestCode, permissions, grantResults)
    }

    override fun permissionAccepted(actionCode: Int) {
        Timber.d(actionCode.toString())
        if(actionCode and SensoricsPermission.ACCESS_COARSE_LOCATION.code != 0) {
            bluetoothScanningViewModel.startScanning(filterByDevice)
            filterByDevice = true
        }
    }

    override fun permissionDenied(actionCode: Int) {
        Timber.d(actionCode.toString())
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

    private fun buildRationaleString(permissions: List<SensoricsPermission>): String {
        val sb = StringBuilder(getString(R.string.the_app_needs_permission))
        sb.append(":\n\n")
        permissions.forEach {
            sb.append(getString(SensoricsPermission.RATIONALE_MAP.getValue(it.code)))
            sb.append(":\n")
        }
        return sb.toString()
    }

    override fun showRationale(
        needRationale : List<SensoricsPermission>,
        vararg needGrant: SensoricsPermission
    ) {
        val message = buildRationaleString(needRationale)

        AlertDialog.Builder(this)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.ok) { dialogInterface, id ->
                dialogInterface.dismiss()
                permissionViewModel.requestPermissions(*needGrant)
            }.create().show()
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

        mainMenu.let {
            val menuItem: MenuItem = it!!.findItem(R.id.action_toggle_scan)
            toggleScanFromMenuItem(menuItem)
        }
    }

    companion object {
        const val WORK_TAG = "My_WORKER_TAG_00123"
        const val WORK_NAME = "MyWorkName"
    }
}