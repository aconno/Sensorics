package com.aconno.sensorics.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.aconno.sensorics.BluetoothScanningService
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.scanning.BluetoothState
import com.aconno.sensorics.domain.scanning.ScanEvent
import com.aconno.sensorics.model.DeviceActive
import com.aconno.sensorics.ui.device_main.DeviceMainFragment
import com.aconno.sensorics.ui.dialogs.ScannedDevicesDialog
import com.aconno.sensorics.ui.dialogs.ScannedDevicesDialogListener
import com.aconno.sensorics.ui.settings.SettingsActivity
import com.aconno.sensorics.viewmodel.BluetoothScanningViewModel
import com.aconno.sensorics.viewmodel.BluetoothViewModel
import com.aconno.sensorics.viewmodel.DeviceViewModel
import com.aconno.sensorics.viewmodel.PermissionViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_toolbar2.*
import timber.log.Timber
import javax.inject.Inject

class MainActivity2 : DaggerAppCompatActivity(), PermissionViewModel.PermissionCallbacks,
    ScannedDevicesDialogListener {

    @Inject
    lateinit var bluetoothViewModel: BluetoothViewModel

    @Inject
    lateinit var bluetoothScanningViewModel: BluetoothScanningViewModel

    @Inject
    lateinit var deviceViewModel: DeviceViewModel

    private var mainMenu: Menu? = null

    private var filterByDevice: Boolean = true

    private var compositeDisposable = CompositeDisposable()

    private var deviceList = listOf<DeviceActive>()
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

        invalidateOptionsMenu()
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
                invalidateFragmentMenus(content_pager.currentItem)
            }
        })
    }

    private fun invalidateFragmentMenus(position: Int) {
        for (i in 0 until viewPagerAdapter.itemCount) {
            viewPagerAdapter.getItem(i).setHasOptionsMenu(i == position)
        }
        invalidateOptionsMenu() //or respectively its support method.
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        compositeDisposable = CompositeDisposable()
        super.onDestroy()
    }

    private fun displayPreferredDevices(it: List<DeviceActive>) {
        if (deviceList.size != it.size) {
            deviceList = it
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
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
        //Permissions skipped
        bluetoothScanningViewModel.startScanning(filterByDevice)
        this@MainActivity2.filterByDevice = true
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

    override fun permissionAccepted(actionCode: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun permissionDenied(actionCode: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showRationale(actionCode: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    inner class ViewPagerAdapter : FragmentStateAdapter(this) {
        override fun getItem(position: Int): Fragment {
            return DeviceMainFragment.newInstance(deviceList[position].device)
        }

        override fun getItemCount(): Int {
            return deviceList.size
        }
    }
}