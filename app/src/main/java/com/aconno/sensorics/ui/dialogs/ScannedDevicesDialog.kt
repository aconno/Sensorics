package com.aconno.sensorics.ui.dialogs

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.R
import com.aconno.sensorics.adapter.DeviceAdapter
import com.aconno.sensorics.adapter.ItemClickListener
import com.aconno.sensorics.domain.interactor.repository.GetSavedDevicesMaybeUseCase
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.ui.base.BaseDialogFragment
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_devices.*
import timber.log.Timber
import javax.inject.Inject

class ScannedDevicesDialog : BaseDialogFragment(), ItemClickListener<Device> {

    @Inject
    lateinit var devices: Flowable<Device>

    @Inject
    lateinit var savedDevicesUseCase: GetSavedDevicesMaybeUseCase

    private lateinit var adapter: DeviceAdapter

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
        adapter = DeviceAdapter(mutableListOf(), this)
        list_devices.adapter = adapter

        addDisposable(
            savedDevicesUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    savedDevices.clear()
                    savedDevices.addAll(it)

                    addDisposable(
                        devices.distinct()
                            .filter {
                                !savedDevices.contains(it)
                            }
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                text_empty.visibility = View.INVISIBLE
                                adapter.addDevice(it)
                            }
                    )
                }
        )
    }

    override fun onItemClick(item: Device) {
        Timber.d("Item clicked, mac: ${item.macAddress}")
        listener.onDevicesDialogItemClick(item)
        dialog.dismiss()
    }
}