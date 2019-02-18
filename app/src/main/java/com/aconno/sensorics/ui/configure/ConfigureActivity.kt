package com.aconno.sensorics.ui.configure

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.aconno.bluetooth.BluetoothDeviceService
import com.aconno.bluetooth.TasksCompleteListener
import com.aconno.bluetooth.beacon.Beacon
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.getRealName
import com.google.gson.Gson
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_configure.*
import kotlinx.android.synthetic.main.dialog_configure.*
import timber.log.Timber
import javax.inject.Inject

class ConfigureActivity : DaggerAppCompatActivity(),
    BeaconGeneralFragment.OnBeaconGeneralFragmentInteractionListener {

    @Inject
    lateinit var beaconViewModel: BeaconViewModel

    private val beaconPagerAdapter: BeaconPagerAdapter by lazy {
        BeaconPagerAdapter(supportFragmentManager).apply {
            beaconViewModel.beacon.observe(this@ConfigureActivity, Observer<Beacon> {
                it?.let {
                    this.beacon = it
                }
            })
        }
    }
    lateinit var device: Device

    private var serviceConnect: BluetoothDeviceService? = null
    private var connectResultDisposable: Disposable? = null
    private lateinit var dialog: AlertDialog

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Timber.d("Disconnected")
            connectResultDisposable?.dispose()
            serviceConnect = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serviceConnect = (service as BluetoothDeviceService.LocalBinder).getService()
            Timber.d("Connected")

//          Create connection
            openConnection()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configure)

        if (intent.extras != null && intent.extras!!.containsKey(EXTRA_DEVICE)) {
            device = Gson().fromJson(
                intent.extras!!.getString(EXTRA_DEVICE)
                , Device::class.java
            )
        } else {
            throw IllegalArgumentException("Device not provided.")
        }

        setSupportActionBar(toolbar)

        //Set titlebar
        supportActionBar?.title = device.getRealName()
        supportActionBar?.subtitle = device.macAddress

        dialog = showProgressDialog()
        vp_beacon.adapter = beaconPagerAdapter
        beaconPagerAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_configuration, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_save -> writeConfig()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun writeConfig(incremental: Boolean = false) {
        val dialog: AlertDialog = showProgressDialog()
        dialog.setOnCancelListener {
            Toast.makeText(
                this@ConfigureActivity,
                "Write cancelled, clearing tasks!",
                Toast.LENGTH_LONG
            ).show()
            serviceConnect?.clearQueue()
        }

        serviceConnect?.saveConfig(
            object : TasksCompleteListener() {
                @SuppressLint("SetTextI18n")
                override fun onTaskComplete(tasksCompleted: Int, tasksTotal: Int) {
                    Timber.e("Task Complete")
                    runOnUiThread {
                        dialog.message.text = "$tasksCompleted/$tasksTotal"
                    }
                }

                override fun onTasksComplete() {
                    Timber.e("All Tasks Complete")
                    runOnUiThread {
                        Toast.makeText(
                            this@ConfigureActivity.applicationContext, "Done writing...",
                            Toast.LENGTH_LONG
                        ).show()
                        dialog.dismiss()
                    }
                }
            }
        )

        beaconViewModel.beacon.value?.write(incremental)
    }

    @SuppressLint("InflateParams")
    private fun showProgressDialog(): AlertDialog {
        return AlertDialog.Builder(this).apply {
            setView(layoutInflater.inflate(R.layout.dialog_configure, null))
        }.create().apply {
            setCanceledOnTouchOutside(false)
        }.also {
            if (!this.isFinishing or !this.isDestroyed) {
                it.show()
            }
        }
    }

    private fun openConnection() {
        serviceConnect?.connectToBluetoothDevice(object :
            BluetoothDeviceService.LoadingTasksUIInterface {
            @SuppressLint("SetTextI18n")
            override fun onTaskComplete(tasksCompleted: Int, tasksTotal: Int) {
                runOnUiThread {
                    dialog.message.text = "Progress: $tasksCompleted/$tasksTotal"
                }
            }

            override fun onTasksComplete(beacon: Beacon) {
                runOnUiThread {
                    beaconViewModel.beacon.value = beacon
                    dialog.dismiss()
                }
            }

            override fun onTasksCancelled() {
                runOnUiThread {
                    dialog.setOnCancelListener {
                        Toast.makeText(
                            this@ConfigureActivity,
                            "Terminating connection, going back to the scanner!",
                            Toast.LENGTH_LONG
                        ).show()
                        serviceConnect?.disconnect()
                    }
                }
            }
        }, device.macAddress)
    }

    override fun onStart() {
        super.onStart()
        Intent(this, BluetoothDeviceService::class.java).also {
            bindService(
                it, serviceConnection, Context.BIND_AUTO_CREATE
            )
        }
    }

    override fun onStop() {
        unbindService(serviceConnection)
        closeConnection()
        super.onStop()
    }

    private fun closeConnection() {
        Timber.e("Close Connection")
        serviceConnect?.disconnect()
    }

    companion object {
        private const val EXTRA_DEVICE = "EXTRA_DEVICE"

        fun start(context: Context, device: Device) {
            Intent(context, ConfigureActivity::class.java).apply {
                putExtra(EXTRA_DEVICE, Gson().toJson(device))
            }.also {
                context.startActivity(it)
            }
        }
    }

    //BeaconGeneralFragment
    override fun onDataUpdated(bundle: Bundle) {
        bundle.keySet().forEach {
            when (it) {
                BeaconGeneralFragment.EXTRA_BEACON_CONNECTIBLE ->
                    beaconViewModel.beacon.value?.connectible = bundle.getBoolean(it)
            }
        }
    }

    override fun updateFirmware() {
        Toast.makeText(this, "This feature has not been implemented yet!", Toast.LENGTH_SHORT)
            .show()
    }

    override fun resetFactory() {
        Toast.makeText(this, "This feature has not been implemented yet!", Toast.LENGTH_SHORT)
            .show()
    }

    override fun powerOff() {
        Toast.makeText(this, "This feature has not been implemented yet!", Toast.LENGTH_SHORT)
            .show()
    }

    override fun addPassword() {
        Toast.makeText(this, "This feature has not been implemented yet!", Toast.LENGTH_SHORT)
            .show()
    }
}
