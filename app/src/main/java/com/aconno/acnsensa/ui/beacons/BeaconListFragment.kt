package com.aconno.acnsensa.ui.beacons

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.acnsensa.R
import com.aconno.acnsensa.adapter.BeaconAdapter
import com.aconno.acnsensa.adapter.ItemClickListener
import com.aconno.acnsensa.domain.model.Device
import kotlinx.android.synthetic.main.fragment_beacon_list.*

class BeaconListFragment : Fragment(), ItemClickListener<Device> {

    private lateinit var beaconAdapter: BeaconAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_beacon_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        beaconAdapter = BeaconAdapter(mutableListOf(), this)
        beacon_list.adapter = beaconAdapter
    }

    override fun onItemClick(item: Device) {
        //TODO: Open main activity with scanning filter by device
    }

    companion object {

        fun newInstance(): BeaconListFragment {
            return BeaconListFragment()
        }
    }
}