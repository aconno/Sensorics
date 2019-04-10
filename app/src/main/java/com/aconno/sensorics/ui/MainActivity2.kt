package com.aconno.sensorics.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.aconno.sensorics.BluetoothScanningService
import com.aconno.sensorics.BuildConfig
import com.aconno.sensorics.R
import com.aconno.sensorics.adapter.viewpager2.FragmentStateAdapter
import com.aconno.sensorics.adapter.viewpager2.TabLayoutMediator
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.scanning.BluetoothState
import com.aconno.sensorics.domain.scanning.ScanEvent
import com.aconno.sensorics.getRealName
import com.aconno.sensorics.model.DeviceActive
import com.aconno.sensorics.ui.device_main.DeviceMainFragment
import com.aconno.sensorics.ui.dialogs.ScannedDevicesDialog
import com.aconno.sensorics.ui.dialogs.ScannedDevicesDialogListener
import com.aconno.sensorics.ui.settings.SettingsActivity
import com.aconno.sensorics.viewmodel.BluetoothScanningViewModel
import com.aconno.sensorics.viewmodel.BluetoothViewModel
import com.aconno.sensorics.viewmodel.DeviceViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_toolbar2.*
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

    private val editedDeviceMap = hashMapOf<String, DeviceActive>()

    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar2)
        button_add_device?.setOnClickListener {
            stopScanning()
            startScanning(false)
            ScannedDevicesDialog().show(supportFragmentManager, "devices_dialog")
        }

        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)

        compositeDisposable.add(
                deviceViewModel.getSavedDevicesFlowable()
                        .subscribe {
                            displayPreferredDevices(it)
                        }
        )

        bluetoothScanningViewModel.getScanEvent()
                .observe(this, Observer { handleScanEvent(it) })

        setupViewPager()
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
        content_pager?.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPagerAdapter = ViewPagerAdapter()
        content_pager?.adapter = viewPagerAdapter
        content_pager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                for (i in 0..(viewPagerAdapter.itemCount - 1)) {
                    (viewPagerAdapter.getItem(position) as DeviceMainFragment).setMenuVisibility(
                            position == i
                    )
                }

                invalidateOptionsMenu()
            }
        })

        TabLayoutMediator(tabLayout, content_pager) { tab, position ->
            tab.text = deviceList[position].device.getRealName()
        }.attach()
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        compositeDisposable = CompositeDisposable()
        super.onDestroy()
    }

    private fun displayPreferredDevices(it: List<DeviceActive>) {
        if (deviceList.size != it.size) {
            deviceList = it.toMutableList()
            viewPagerAdapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        bluetoothViewModel.observeBluetoothState()
        bluetoothViewModel.bluetoothState.observe(this, Observer { onBluetoothStateChange(it) })
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

    fun isScanning(): Boolean {
        return BluetoothScanningService.isRunning()
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
        startScaninngWithPermissions()
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
    private fun startScaninngWithPermissions() {
        val perms =
                arrayOf<String>(
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

    companion object {
        const val RC_LOCATION_AND_EXTERNAL = 12312
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

    @SuppressLint("InflateParams")
    fun renameDevice(macAddress: String) {
        deviceList.find { it.device.macAddress == macAddress }?.let { deviceToUpdate ->
            val view = layoutInflater.inflate(R.layout.layout_rename, null)
            val input = view.findViewById<EditText>(R.id.edit_name).apply {
                setText(deviceToUpdate.device.getRealName())
            }

            AlertDialog.Builder(this)
                    .setView(view)
                    .setTitle(R.string.rename_beacon)
                    .setPositiveButton(R.string.yes) { dialog, _ ->
                        onRenameDeviceConfirmed(deviceToUpdate, input.text.toString())
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.no) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
        }
    }

    private fun onRenameDeviceConfirmed(deviceToUpdate: DeviceActive, deviceAlias: String?) {
        if (deviceAlias?.isNotBlank() == true) {
            val oldDevice = deviceToUpdate.device

            val newDevice = Device(oldDevice.name, deviceAlias, oldDevice.macAddress, oldDevice.icon)
            val newDeviceActive = DeviceActive(newDevice, deviceToUpdate.active)

            val position = deviceList.indexOf(deviceToUpdate)
            deviceList[position] = newDeviceActive
            viewPagerAdapter.notifyItemChanged(position)

            showUndoableSnackbar(R.string.beacon_renamed, View.OnClickListener {
                deviceList[position] = deviceToUpdate
                viewPagerAdapter.notifyItemChanged(position)
            }) {
                deviceViewModel.updateDevice(deviceToUpdate.device, deviceAlias)
            }
        }
    }

    private fun showUndoableSnackbar(@StringRes resId: Int, clickListener: View.OnClickListener,
                                     onSnackbarTimeout: () -> Unit) {
        Snackbar.make(main_container, resId, Snackbar.LENGTH_LONG).run {
            setAction(R.string.undo, clickListener)
            setActionTextColor(Color.WHITE)
            addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        onSnackbarTimeout()
                    }
                }
            })
            show()
        }
    }

    inner class ViewPagerAdapter : FragmentStateAdapter(this@MainActivity2) {
        override fun getItem(position: Int): Fragment {
            return DeviceMainFragment.newInstance(deviceList[position].device)
        }

        override fun getItemCount(): Int {
            return deviceList.size
        }
    }
}