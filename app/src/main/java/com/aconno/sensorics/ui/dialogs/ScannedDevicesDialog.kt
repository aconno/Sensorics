package com.aconno.sensorics.ui.dialogs

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.aconno.sensorics.R
import com.aconno.sensorics.adapter.ScanDeviceAdapter
import com.aconno.sensorics.domain.interactor.resources.GetIconUseCase
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.ScanDevice
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_devices.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScannedDevicesDialog : DisposerDialogFragment() {

    @Inject
    lateinit var scanDeviceStream: Flowable<ScanDevice>

    @Inject
    lateinit var savedDevicesUseCase: Flowable<List<Device>>

    @Inject
    lateinit var getIconUseCase: GetIconUseCase

    private val adapter = ScanDeviceAdapter()

    private var listener: ScannedDevicesDialogListener? = null

    private var savedDevices = mutableListOf<Device>()

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            listener = context as ScannedDevicesDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement ScannedDevicesDialogListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        removeTitleSpacing()
        return inflater.inflate(R.layout.dialog_devices, container)
    }

    private fun removeTitleSpacing() {
        // Required for Lollipop (maybe others too) devices
        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_empty.setText(R.string.message_no_scanned_devices)

        list_devices.layoutManager = LinearLayoutManager(context)
        list_devices.adapter = adapter

        button_cancel.setOnClickListener {
            dismiss()
        }

        addDisposable(
            adapter.getClickedDeviceStream()
                .subscribe {
                    Timber.d("Item clicked, mac: ${it.device.macAddress}")
                    listener?.onDevicesDialogItemClick(it.device)
                    savedDevices.add(it.device)
                    adapter.removeScanDevice(it)

                    if (adapter.itemCount == 0) {
                        text_empty.visibility = View.VISIBLE
                    }
                }
        )

        addDisposable(
            savedDevicesUseCase
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    savedDevices.clear()
                    savedDevices.addAll(it)
                }
        )

        addDisposable(
            scanDeviceStream
                .groupBy { it.device.macAddress }
                .map { it.sample(1, TimeUnit.SECONDS) }
                .flatMap { it }
                .filter { !savedDevices.contains(it.device) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    text_empty.visibility = View.INVISIBLE
                    adapter.addScanDevice(it)
                    adapter.setIcon(getIconUseCase.execute(it.device.name).toString())
                }
        )
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        listener?.onDialogDismissed()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun getIconsForDevices() {

    }

}