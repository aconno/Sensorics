package com.aconno.sensorics.ui.dialogs

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.sensorics.R
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.adapter.ScanDeviceAdapter
import com.aconno.sensorics.domain.interactor.repository.GetSavedDevicesMaybeUseCase
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.ScanDevice
import com.aconno.sensorics.ui.base.BaseDialogFragment
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_devices.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScannedDevicesDialog : BaseDialogFragment() {

    @Inject
    lateinit var scanDeviceStream: Flowable<ScanDevice>

    @Inject
    lateinit var savedDevicesUseCase: GetSavedDevicesMaybeUseCase

    private val adapter = ScanDeviceAdapter()

    private lateinit var listener: ScannedDevicesDialogListener

    private var savedDevices = mutableListOf<Device>()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val sensoricsApplication = activity?.application as SensoricsApplication
        sensoricsApplication.appComponent.inject(this)

        val activity = activity
        if (activity is ScannedDevicesDialogListener) {
            listener = activity
        } else {
            throw RuntimeException("$activity must implement ${ScannedDevicesDialogListener::class}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_devices, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list_devices.layoutManager = LinearLayoutManager(context)
        list_devices.adapter = adapter
        adapter.getClickedDevices()
            .take(1)
            .subscribe {
                listener.onDevicesDialogItemClick(it.device)
                dialog.dismiss()
            }

        addDisposable(
            savedDevicesUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    savedDevices.clear()
                    savedDevices.addAll(it)

                    addDisposable(
                        scanDeviceStream
                            .filter {
                                !savedDevices.contains(it.device)
                            }
                            .groupBy { it.device.macAddress }
                            .map { it.sample(1, TimeUnit.SECONDS) }
                            .flatMap { it }
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                text_empty.visibility = View.INVISIBLE
                                adapter.addScanDevice(it)
                            }
                    )
                }
        )
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        listener.onDialogDismissed()
    }
  
    override fun onItemClick(item: Device) {
        Timber.d("Item clicked, mac: ${item.macAddress}")
        listener.onDevicesDialogItemClick(item)
        savedDevices.add(item)
        adapter.deleteDevice(item)

        if (adapter.itemCount == 0) {
            text_empty.visibility = View.VISIBLE
        }
    }
}