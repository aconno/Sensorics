package com.aconno.sensorics.ui.bubble

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.ui.device_main.DeviceMainFragment
import com.aconno.sensorics.viewmodel.BluetoothScanningViewModel
import com.aconno.sensorics.viewmodel.PermissionViewModel
import com.google.gson.Gson
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class BubbleActivity : DaggerAppCompatActivity(), PermissionViewModel.PermissionCallbacks {

    @Inject
    lateinit var bluetoothScanningViewModel: BluetoothScanningViewModel

    lateinit var device: Device

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bubble)

        device = Gson().fromJson(
            intent!!.getStringExtra(KEY_DEVICE)
            , Device::class.java
        )

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, DeviceMainFragment.newInstance(device))
            .commit()
    }

    override fun onResume() {
        super.onResume()
        bluetoothScanningViewModel.startScanning(true)
    }

    override fun onPause() {
        bluetoothScanningViewModel.stopScanning()
        super.onPause()
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

    companion object {
        private const val KEY_DEVICE = "KEY_DEVICE"

        fun getIntent(context: Context, device: Device): Intent {
            return Intent(context, BubbleActivity::class.java).apply {
                putExtra(KEY_DEVICE, Gson().toJson(device))
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }
}