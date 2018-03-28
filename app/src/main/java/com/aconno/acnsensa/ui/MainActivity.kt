package com.aconno.acnsensa.ui

import android.Manifest
import android.arch.lifecycle.Observer
import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.BluetoothScanningService
import com.aconno.acnsensa.R
import com.aconno.acnsensa.dagger.mainactivity.DaggerMainActivityComponent
import com.aconno.acnsensa.dagger.mainactivity.MainActivityComponent
import com.aconno.acnsensa.dagger.mainactivity.MainActivityModule
import com.aconno.acnsensa.domain.model.ScanEvent
import com.aconno.acnsensa.viewmodel.BluetoothScanningViewModel
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var bluetoothScanningViewModel: BluetoothScanningViewModel

    private var mainMenu: Menu? = null

    val mainActivityComponent: MainActivityComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? = application as? AcnSensaApplication
        DaggerMainActivityComponent.builder()
            .appComponent(acnSensaApplication?.appComponent)
            .mainActivityModule(MainActivityModule(this))
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivityComponent.inject(this)
        custom_toolbar.title = getString(R.string.app_name)
        setSupportActionBar(custom_toolbar)

        invalidateOptionsMenu()

        if (savedInstanceState == null) {
            addFragment()
        }
    }

    override fun onResume() {
        super.onResume()

        bluetoothScanningViewModel.getResult().observe(this, Observer { handleScanEvent(it) })

        if (!hasPermissions()) {
            requestPermissions()
        } else if (!isBtEnabled()) {
            Timber.e("BT enabled")
            Snackbar.make(activity_container, R.string.bt_disabled, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.enable) { enableBt() }
                .show()
        }
    }

    private fun handleScanEvent(scanEvent: ScanEvent?) {
        Timber.d("Handle scan event ${scanEvent?.message}")
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

    private fun addFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(activity_container.id, SensorListFragment())
        transaction.commit()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        mainMenu = menu
        mainMenu?.clear()
        menuInflater.inflate(R.menu.main_menu, menu)

        mainMenu?.findItem(R.id.action_toggle_scan)?.let {
            setScanMenuLabel(it)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id: Int? = item?.itemId
        when (id) {
            R.id.action_toggle_scan -> toggleScan(item)
            R.id.action_start_actions_activity -> startActionListActivity()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun startActionListActivity() {
        ActionListActivity.start(this)
    }

    private fun toggleScan(item: MenuItem?) {
        item?.let {
            if (item.isChecked) {
                bluetoothScanningViewModel.stopScanning()
            } else {
                bluetoothScanningViewModel.startScanning()
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

    /**
     * Checks if all the permissions are granted
     *
     * @return true if all the permissions are granted
     */
    private fun hasPermissions(): Boolean {
        val result = PERMISSIONS.sumBy { ContextCompat.checkSelfPermission(this, it) }
        return result == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Requests all the permissions needed
     */
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSIONS)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSIONS) {
            val result = grantResults.sum()

            if (result != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, R.string.grant_permissions, Toast.LENGTH_LONG)
                    .show()
                finish()
            }
        }
    }

    private fun isBtEnabled(): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return bluetoothAdapter?.isEnabled ?: false
    }

    private fun enableBt() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothAdapter.enable()
    }

    companion object {

        const val REQUEST_PERMISSIONS = 0x0001

        val PERMISSIONS = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
}

