package com.aconno.acnsensa.sensorlist

import android.app.Application
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.BluetoothScanningViewModel
import com.aconno.acnsensa.R
import com.aconno.acnsensa.domain.Bluetooth
import com.aconno.acnsensa.domain.model.ScanEvent
import kotlinx.android.synthetic.main.activity_main.*

//TODO: This needs refactoring.
class SensorListActivity : AppCompatActivity() {

    private var mainMenu: Menu? = null

    private lateinit var bluetoothScanningViewModel: BluetoothScanningViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("oncreate", "oncreate")
        setContentView(R.layout.activity_main)

        custom_toolbar.title = "AcnSensa"
        setSupportActionBar(custom_toolbar)

        invalidateOptionsMenu()

        if (savedInstanceState == null) {
            addFragment()
        }

        val acnSensaApplication: AcnSensaApplication? = application as? AcnSensaApplication
        acnSensaApplication?.let {
            val bluetoothScanningViewModelFactory =
                BluetoothScanningViewModelFactory(it.bluetooth, application)

            bluetoothScanningViewModel =
                    ViewModelProviders.of(this, bluetoothScanningViewModelFactory)
                        .get(BluetoothScanningViewModel::class.java)
        }

        Log.e("oncreatefinished", "oncreatefinished")


    }

    override fun onStart() {
        super.onStart()
        Log.e("onstart", "onstart")

    }

    override fun onResume() {
        super.onResume()
        bluetoothScanningViewModel.getResult().observe(this, Observer { handleScanEvent(it) })
    }

    private fun handleScanEvent(scanEvent: ScanEvent?) {
        Log.e("HANDLER", "handle ${scanEvent?.message}")
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
                it.setTitle("Stop scanning")
            }
        }
    }

    private fun onScanStop() {
        mainMenu?.let {
            val menuItem: MenuItem? = it.findItem(R.id.action_toggle_scan)
            menuItem?.let {
                it.isChecked = false
                it.setTitle("Scan")
            }
        }
    }

    private fun addFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(activity_container.id, SensorListFragment())
        transaction.commit()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        Log.e("onprepare", "prepate")
        mainMenu = menu
        menuInflater.inflate(R.menu.main_menu, menu)

        handleScanEvent(bluetoothScanningViewModel.getResult().value)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id: Int? = item?.itemId
        when (id) {
            R.id.action_toggle_scan -> toggleScan(item)
        }

        return super.onOptionsItemSelected(item)
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
}

class BluetoothScanningViewModelFactory(val bluetooth: Bluetooth, val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val ret: T? = BluetoothScanningViewModel(bluetooth, application) as? T
        ret?.let {
            return ret
        }

        throw IllegalArgumentException("Invalid cast")
    }
}