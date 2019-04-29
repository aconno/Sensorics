package com.aconno.sensorics.ui.dfu

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.OpenableColumns
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.aconno.sensorics.DfuService
import com.aconno.sensorics.R
import com.aconno.sensorics.action
import com.aconno.sensorics.snack
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_dfu.*
import no.nordicsemi.android.dfu.DfuBaseService
import no.nordicsemi.android.dfu.DfuBaseService.EXTRA_DEVICE_ADDRESS
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter
import no.nordicsemi.android.dfu.DfuServiceInitiator
import no.nordicsemi.android.dfu.DfuServiceListenerHelper
import timber.log.Timber
import java.io.File


class DfuActivity : DaggerAppCompatActivity() {

    private lateinit var deviceAddress: String
    private var dfuFileUri: Uri? = null
    private var canGoBack = false
    private var isUpdating = false
        set(value) {
            field = value
            updateUI(value)
        }

    //Dfu Related params
    private lateinit var starterDFU: DfuServiceInitiator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dfu)
        loadParams(savedInstanceState)
        setupToolbar()

        iv_dfu_selectFile?.setOnClickListener {
            openFileChooser()
        }

        btn_dfu_flash?.setOnClickListener {
            flash()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DfuServiceInitiator.createDfuNotificationChannel(applicationContext)
        }
    }

    private fun setupToolbar() {
        toolbar.title = getString(R.string.dfu)
        toolbar.subtitle = deviceAddress
        setSupportActionBar(toolbar)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(EXTRA_UPDATE_STATUS, isUpdating)
        dfuFileUri?.let {
            outState.putParcelable(EXTRA_FILE_PATH, it)
        }
    }

    private fun loadParams(savedInstanceState: Bundle?) {
        deviceAddress = intent.getStringExtra(EXTRA_DEVICE_ADDRESS)
        dfuFileUri = savedInstanceState?.getParcelable(EXTRA_FILE_PATH)
        isUpdating = savedInstanceState?.getBoolean(EXTRA_UPDATE_STATUS) ?: false

        if (intent.hasExtra(DfuBaseService.EXTRA_DEVICE_NAME)
            && intent.hasExtra(DfuBaseService.EXTRA_PROGRESS)
        ) {
            val progress = intent.getIntExtra(DfuBaseService.EXTRA_PROGRESS, 0)
            setStatusText(getString(R.string.dfuactivity_status_updating))
            prg_dfu_progress.progress = progress

            isUpdating = true
        }
    }

    private fun updateUI(value: Boolean) {
        if (value) {
            canGoBack = false
            vs_dfu_container?.displayedChild = 1
        } else {
            vs_dfu_container?.displayedChild = 0
        }
    }

    override fun onResume() {
        super.onResume()
        DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener)
    }

    override fun onPause() {
        super.onPause()
        DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener)
    }

    override fun onBackPressed() {
        if (isUpdating) {
            if (canGoBack) {
                isUpdating = false
            } else {
                vs_dfu_container?.snack(getString(R.string.dfu_snack_msg_work_in_progress)) {
                    action(
                        getString(R.string.dfu_snack_action_goback),
                        ContextCompat.getColor(applicationContext, R.color.primaryDarkColor)
                    ) {
                        isUpdating = false
                    }
                }
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun flash() {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 321)
            return
        }

        if (dfuFileUri != null) {
            isUpdating = true

            Timber.d(deviceAddress)
            starterDFU = DfuServiceInitiator(deviceAddress)
                .setKeepBond(false)

            // If you want to have experimental buttonless DFU feature supported call additionally:
            starterDFU.setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true)

            starterDFU.setZip(dfuFileUri!!)
            starterDFU.start(applicationContext, DfuService::class.java)
        } else {
            ll_dfu_root?.snack(getString(R.string.dfu_snack_msg_file_selection)) {
                action(
                    getString(R.string.dfu_snack_action_select),
                    ContextCompat.getColor(applicationContext, R.color.primaryDarkColor)
                ) {
                    openFileChooser()
                }
            }
        }
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = DfuBaseService.MIME_TYPE_ZIP
        }

        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                Timber.tag("DfuActivity").i("Uri: $uri")
                selectFile(uri)
            }
        }
    }

    private fun selectFile(uri: Uri) {
        dfuFileUri = uri

        dfuFileUri?.scheme?.let {
            if (it == URI_SCHEMA_FILE) {
                txt_dfu_selectedFile.text = File(uri.path).name
            } else if (it == URI_SCHEMA_CONTENT) {
                getFileName(uri)?.let {
                    txt_dfu_selectedFile.text = it
                }
            }
        }
    }

    private fun getFileName(uri: Uri): String? {
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }

        return null
    }

    private val mDfuProgressListener = object : DfuProgressListenerAdapter() {
        override fun onDeviceConnecting(deviceAddress: String) {
            setStatusText(getString(R.string.dfuactivity_status_connecting))
        }

        override fun onDeviceConnected(deviceAddress: String) {
            setStatusText(getString(R.string.dfuactivity_status_connected))
        }

        override fun onDfuProcessStarting(deviceAddress: String) {
            setStatusText(getString(R.string.dfuactivity_status_upload_starting))
        }

        override fun onEnablingDfuMode(deviceAddress: String) {
            setStatusText(getString(R.string.dfuactivity_status_enabling_dfu_mode))
        }

        override fun onDeviceDisconnecting(deviceAddress: String) {
            setStatusText(getString(R.string.dfuactivity_status_disconnecting))
        }

        override fun onDeviceDisconnected(deviceAddress: String) {
            setStatusText(getString(R.string.dfuactivity_status_disconnected))
        }

        override fun onDfuAborted(deviceAddress: String) {
            setStatusText(getString(R.string.dfuactivity_status_dfu_aborted))
            onComplete()
        }

        override fun onDfuCompleted(deviceAddress: String) {
            setStatusText(getString(R.string.dfuactivity_status_complete))
            onComplete()
        }

        override fun onFirmwareValidating(deviceAddress: String) {
            setStatusText(getString(R.string.dfuactivity_status_firmwarevalidating))
        }

        override fun onDfuProcessStarted(deviceAddress: String) {
            setStatusText(getString(R.string.dfuactivity_status_uploading))
        }

        override fun onProgressChanged(
            deviceAddress: String,
            percent: Int,
            speed: Float,
            avgSpeed: Float,
            currentPart: Int,
            partsTotal: Int
        ) {
            super.onProgressChanged(
                deviceAddress,
                percent,
                speed,
                avgSpeed,
                currentPart,
                partsTotal
            )

            setStatusText(getString(R.string.dfuactivity_status_updating))
            prg_dfu_progress.progress = percent

            txt_dfu_speed.text = getString(R.string.speed_format, speed)
            txt_dfu_avgSpeed.text = getString(R.string.avg_speed_format, avgSpeed)
            txt_dfu_parts.text = getString(R.string.parts_format, currentPart, partsTotal)
        }

        override fun onError(deviceAddress: String, error: Int, errorType: Int, message: String) {
            val errorMsg = "Error $error => $message"
            Timber.d("Status : Error $error => $message")
            setStatusText(errorMsg, R.color.red)
            onComplete()
        }
    }

    private fun onComplete() {
        canGoBack = true
        val vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(500)
        }
    }

    private fun setStatusText(
        status: String,
        @ColorRes textColor: Int = R.color.primaryTextColor
    ) {
        txt_dfu_status.text = getString(R.string.status_format, status)
        txt_dfu_status.setTextColor(ContextCompat.getColor(applicationContext, textColor))
    }

    companion object {
        private const val EXTRA_FILE_PATH = "EXTRA_FILE_PATH"
        private const val EXTRA_UPDATE_STATUS = "EXTRA_UPDATE_STATUS"
        private const val READ_REQUEST_CODE = 12332

        private const val URI_SCHEMA_FILE = "file"
        private const val URI_SCHEMA_CONTENT = "content"

        fun start(context: Context, deviceMacAddress: String) {
            with(Intent(context, DfuActivity::class.java)) {
                putExtra(DfuBaseService.EXTRA_DEVICE_ADDRESS, deviceMacAddress)
                context.startActivity(this)
            }
        }
    }
}
