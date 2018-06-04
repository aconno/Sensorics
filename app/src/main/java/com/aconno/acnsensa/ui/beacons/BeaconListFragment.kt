package com.aconno.acnsensa.ui.beacons

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.acnsensa.R
import com.aconno.acnsensa.adapter.BeaconAdapter
import com.aconno.acnsensa.adapter.ItemClickListener
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.ui.MainActivity
import com.aconno.acnsensa.viewmodel.BeaconListViewModel
import kotlinx.android.synthetic.main.fragment_device_list.*
import javax.inject.Inject

class BeaconListFragment : Fragment(), ItemClickListener<Device> {

    @Inject
    lateinit var beaconListViewModel: BeaconListViewModel

    private lateinit var beaconAdapter: BeaconAdapter

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
        return inflater.inflate(R.layout.fragment_device_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        beacon_list.layoutManager = LinearLayoutManager(context)
        beaconAdapter = BeaconAdapter(mutableListOf(), this)
        beacon_list.adapter = beaconAdapter
    }

    override fun onResume() {
        super.onResume()
        beaconListViewModel.getBeaconsLiveData().observe(this, Observer {
            displayBeacons(it)
        })
    }

    private fun displayBeacons(beacons: MutableList<Device>?) {
        beacons?.let {
            if (beacons.isNotEmpty()) {
                empty_view.visibility = View.INVISIBLE
                beaconAdapter.setBeacons(it)
            } else {
                empty_view.visibility = View.VISIBLE
                beaconAdapter.clearBeacons()
            }
        }
    }

    override fun onItemClick(item: Device) {
        activity?.let {
            val mainActivity = it as MainActivity
            mainActivity.showSensorValues(item.macAddress)
        }
    }

    companion object {

        fun newInstance(): BeaconListFragment {
            return BeaconListFragment()
        }
    }
}