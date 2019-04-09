package com.aconno.sensorics.ui.configure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.aconno.sensorics.R
import kotlinx.android.synthetic.main.fragment_configuration_parameters.view.*


class BeaconParameterFragment : Fragment() {

    private val beaconViewModel: BeaconViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(BeaconViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_configuration_parameters, container, false)
        @Suppress("UNCHECKED_CAST")
        beaconViewModel.beacon.value?.let { beacon ->
            view.rv_parameters.layoutManager = LinearLayoutManager(activity)
            view.rv_parameters.adapter = ParameterAdapter(beacon)
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BeaconParameterFragment()
    }
}
