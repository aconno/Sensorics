package com.aconno.sensorics.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.aconno.sensorics.BluetoothScanningService
import com.aconno.sensorics.BuildConfig
import com.aconno.sensorics.R
import com.aconno.sensorics.adapter.viewpager.ViewPagerAdapter
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.scanning.BluetoothState
import com.aconno.sensorics.domain.scanning.ScanEvent
import com.aconno.sensorics.getRealName
import com.aconno.sensorics.model.DeviceActive
import com.aconno.sensorics.ui.dialogs.ScannedDevicesDialog
import com.aconno.sensorics.ui.dialogs.ScannedDevicesDialogListener
import com.aconno.sensorics.ui.settings.SettingsActivity
import com.aconno.sensorics.viewmodel.BluetoothScanningViewModel
import com.aconno.sensorics.viewmodel.BluetoothViewModel
import com.aconno.sensorics.viewmodel.DeviceViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_toolbar2.*
import kotlinx.android.synthetic.main.pager_tab_layout.view.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import javax.inject.Inject


class MainActivity2 : DaggerAppCompatActivity(),
    ScannedDevicesDialogListener, EasyPermissions.PermissionCallbacks {

    @Inject
    lateinit var bluetoothViewModel: BluetoothViewModel

    @Inject
    lateinit var bluetoothScanningViewModel: BluetoothScanningViewModel

    @Inject
    lateinit var deviceViewModel: DeviceViewModel

    private var mainMenu: Menu? = null

    private var filterByDevice: Boolean = true

    private var compositeDisposable = CompositeDisposable()

    private var deviceList = mutableListOf<DeviceActive>()
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar2)
        button_add_device?.setOnClickListener {
            stopScanning()
            startScanning(false)
            ScannedDevicesDialog().show(supportFragmentManager, "devices_dialog")
        }

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)
        //TODO Change Icon
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?.setIcon(R.mipmap.ic_launcher_rounded)

        compositeDisposable.add(
            deviceViewModel.getSavedDevicesFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    displayPreferredDevices(it)
                }
        )

        compositeDisposable.add(
            deviceViewModel.deviceActiveObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    displayPreferredDevices(it)
                }
        )

        bluetoothScanningViewModel.getScanEvent()
            .observe(this, Observer { handleScanEvent(it) })

        setupViewPager()

        savedInstanceState?.let {
            Timber.d("Extracting...")
            content_pager?.postDelayed({
                content_pager?.setCurrentItem(it.getInt(EXTRA_CURRENT_PAGE, 0), false)
            }, 100)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.d("Saving... ${content_pager?.currentItem}")
        outState.putInt(EXTRA_CURRENT_PAGE, content_pager?.currentItem ?: 0)
    }

    override fun onDevicesDialogItemClick(item: Device) {
        deviceViewModel.saveDevice(item)
    }

    override fun onDialogDismissed() {
        stopScanning()
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

    private fun setupViewPager() {
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        content_pager?.adapter = viewPagerAdapter
        content_pager?.offscreenPageLimit = 2
        tabLayout.setupWithViewPager(content_pager)
        content_pager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position != 0) {
                    if (deviceList[position - 1].device.connectable && BluetoothScanningService.isRunning()) {
                        stopScanning()
                    }
                }
            }
        })
    }

    private fun prepareTabView(deviceActive: DeviceActive): View {
        return layoutInflater.inflate(R.layout.pager_tab_layout, null).apply {
            tv_title?.text = deviceActive.device.getRealName()
            tv_count?.text = deviceActive.device.macAddress
        }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        compositeDisposable = CompositeDisposable()
        super.onDestroy()
    }

    private fun displayPreferredDevices(it: List<DeviceActive>) {
        deviceList = it.toMutableList()
        viewPagerAdapter.submitList(deviceList)

        // Iterate over all tabs and set the custom view
        for (i in 1 until (tabLayout.tabCount)) {
            val tab = tabLayout.getTabAt(i)
            tab?.customView = prepareTabView(deviceList[i - 1])
        }
    }

    override fun onResume() {
        super.onResume()
        bluetoothViewModel.observeBluetoothState()
        bluetoothViewModel.bluetoothState.observe(
            this,
            Observer { onBluetoothStateChange(it) })
    }

    private fun onBluetoothStateChange(bluetoothState: BluetoothState?) {
        when (bluetoothState?.state) {
            BluetoothState.BLUETOOTH_OFF -> onBluetoothOff()
            BluetoothState.BLUETOOTH_ON -> onBluetoothOn()
        }
    }

    private fun onBluetoothOn() {
        mainMenu?.let {
            val menuItem: MenuItem? = it.findItem(R.id.action_toggle_scan)
            menuItem?.setVisible(true)
        }
    }

    private fun onBluetoothOff() {
        mainMenu?.let {
            val menuItem: MenuItem? = it.findItem(R.id.action_toggle_scan)
            menuItem?.setVisible(false)
        }
    }

    override fun onPause() {
        super.onPause()
        bluetoothViewModel.stopObservingBluetoothState()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        mainMenu = menu
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

    private fun setScanMenuLabel(menuItem: MenuItem) {
        if (BluetoothScanningService.isRunning()) {
            menuItem.title = getString(R.string.stop_scan)
            menuItem.isChecked = true
        } else {
            menuItem.title = getString(R.string.start_scan)
            menuItem.isChecked = false
        }
    }

    fun startScanning(filterByDevice: Boolean = true) {
        this.filterByDevice = filterByDevice
        startScanningWithPermissions()
    }

    private fun stopScanning() {
        bluetoothScanningViewModel.stopScanning()
    }

    fun startScanOperation() {
        mainMenu.let {
            val menuItem: MenuItem = it!!.findItem(R.id.action_toggle_scan)
            toggleScanFromMenuItem(menuItem)
        }
    }

    private fun toggleScanFromMenuItem(item: MenuItem) {
        if (item.isChecked) {
            stopScanning()
        } else {
            startScanning()
        }
    }

    @AfterPermissionGranted(RC_LOCATION_AND_EXTERNAL)
    private fun startScanningWithPermissions() {
        val perms =
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        if (EasyPermissions.hasPermissions(this, *perms)) {
            bluetoothScanningViewModel.startScanning(filterByDevice)
            this@MainActivity2.filterByDevice = true
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                this, getString(R.string.snackbar_permission_message),
                RC_LOCATION_AND_EXTERNAL, *perms
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        //TODO: Make this nice...
        Snackbar.make(
            main_container,
            getString(R.string.snackbar_permission_message),
            Snackbar.LENGTH_LONG
        ).setAction(getString(R.string.snackbar_settings)) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
            startActivity(intent)
        }.setActionTextColor(ContextCompat.getColor(this, R.color.primaryColor))
            .show()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        //No-Op
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    fun removeCurrentDisplayedBeacon(macAddress: String) {
        deviceList.find { it.device.macAddress == macAddress }?.let {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.remove_device_dialog_title_format, it.device.name))
                .setMessage(R.string.remove_device_dialog_message)
                .setPositiveButton(R.string.yes) { dialog, _ ->
                    deviceViewModel.deleteDevice(it.device)
                    dialog.dismiss()
                }.setNegativeButton(R.string.condition_dialog_cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    @SuppressLint("InflateParams")
    fun showRenameDialog(macAddress: String) {
        deviceList.find { it.device.macAddress == macAddress }?.let { deviceToUpdate ->
            val view = layoutInflater.inflate(R.layout.layout_rename, null)
            val input = view.findViewById<EditText>(R.id.edit_name).apply {
                setText(deviceToUpdate.device.getRealName())
            }

            AlertDialog.Builder(this)
                .setView(view)
                .setTitle(R.string.rename_beacon)
                .setPositiveButton(R.string.rename) { dialog, _ ->
                    val newName = input.text.toString()
                    if (newName.isNotBlank()) {
                        renameDevice(deviceToUpdate, newName)
                        dialog.dismiss()
                    }
                }
                .setNegativeButton(R.string.condition_dialog_cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun renameDevice(deviceToUpdate: DeviceActive, newName: String) {
        deviceViewModel.updateDevice(deviceToUpdate.device, newName)
    }

    companion object {
        const val RC_LOCATION_AND_EXTERNAL = 12312
        const val EXTRA_CURRENT_PAGE = "EXTRA_CURRENT_PAGE"
    }

}