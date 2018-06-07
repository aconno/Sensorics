package com.aconno.acnsensa.ui.dialogs

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.R
import com.aconno.acnsensa.adapter.DeviceAdapter
import com.aconno.acnsensa.adapter.ItemClickListener
import com.aconno.acnsensa.domain.model.Device
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_devices.*
import timber.log.Timber
import javax.inject.Inject

class ScannedDevicesDialog : DialogFragment(), ItemClickListener<Device> {

    @Inject
    lateinit var devices: Flowable<Device>
    private var disposable: Disposable? = null

    private lateinit var adapter: DeviceAdapter

    private lateinit var listener: ScannedDevicesDialogListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val acnSensaApplication = activity?.application as AcnSensaApplication
        acnSensaApplication.appComponent.inject(this)

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

        disposable = devices.distinct()
            .subscribe {
                text_empty.visibility = View.INVISIBLE
                adapter.addDevice(it)
            }
    }

    override fun onItemClick(item: Device) {
        Timber.d("Item clicked, mac: ${item.macAddress}")
        listener.onDevicesDialogItemClick(item)
        dialog.dismiss()
    }

    override fun onDetach() {
        super.onDetach()
        disposable?.dispose()
    }
}