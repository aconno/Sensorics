package com.aconno.acnsensa.ui.devices

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.acnsensa.R
import com.aconno.acnsensa.adapter.DeviceAdapter
import com.aconno.acnsensa.adapter.ItemClickListener
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.ui.MainActivity
import com.aconno.acnsensa.viewmodel.DeviceViewModel
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_scanned_devices.*
import timber.log.Timber
import javax.inject.Inject

class ScannedDevicesFragment : Fragment(), ItemClickListener<Device> {

    @Inject
    lateinit var deviceViewModel: DeviceViewModel

    @Inject
    lateinit var devices: Flowable<Device>

    private var scannedDevicesDisposable: Disposable? = null

    private lateinit var deviceAdapter: DeviceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainActivity: MainActivity? = activity as MainActivity
        mainActivity?.mainActivityComponent?.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scanned_devices, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list_devices.layoutManager = LinearLayoutManager(context)
        deviceAdapter = DeviceAdapter(mutableListOf(), this)
        list_devices.adapter = deviceAdapter

        scannedDevicesDisposable = devices.subscribe {
            empty_view?.visibility = View.INVISIBLE
            deviceAdapter.addDevice(it)
        }
    }

    override fun onResume() {
        super.onResume()
        val mainActivity: MainActivity? = context as MainActivity
        mainActivity?.supportActionBar?.title = "Add device"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scannedDevicesDisposable?.dispose()
    }

    override fun onItemClick(item: Device) {
        activity?.let {
            Timber.d("On item click, name: ${item.name}, mac: ${item.macAddress}")
            deviceViewModel.saveDevice(item)
            it.supportFragmentManager.popBackStack()
        }
    }

    companion object {

        fun newInstance(): ScannedDevicesFragment {
            return ScannedDevicesFragment()
        }
    }
}