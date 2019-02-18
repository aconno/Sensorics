package com.aconno.sensorics.ui.configure

import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.sensorics.R
import kotlinx.android.synthetic.main.dialog_dual_generic_input.view.*
import kotlinx.android.synthetic.main.dialog_generic_input.view.*
import kotlinx.android.synthetic.main.fragment_arbitrary_data_configuration.view.*


class BeaconArbitraryDataFragment : Fragment() {

    private val beaconViewModel: BeaconViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(BeaconViewModel::class.java)
    }

    private lateinit var arbitraryDataAdapter: ArbitraryDataAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.fragment_arbitrary_data_configuration, container, false)
        @Suppress("UNCHECKED_CAST")
        beaconViewModel.beacon.value?.let { beacon ->
            view.rv_json_list.layoutManager = LinearLayoutManager(activity)
            arbitraryDataAdapter = ArbitraryDataAdapter(beacon)
            view.rv_json_list.adapter = arbitraryDataAdapter
        }
        view.fab.setOnClickListener {
            createInputDialog().show()
        }
        return view
    }

    fun createInputDialog(): AlertDialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val view: View = layoutInflater.inflate(R.layout.dialog_dual_generic_input, null)
        val input1: TextInputEditText = view.et_input_1.apply {
            hint = "Data Key"
        }
        val input2: TextInputEditText = view.et_input_2.apply {
            hint = "Value"
        }
        builder.setTitle("Data Input")
        builder.setView(view)
        builder.setCancelable(true)
        builder.setPositiveButton("Update") { dialog, _ ->
            beaconViewModel.beacon.value?.let {
                it.abstractDataMapped[input1.text.toString()] = input2.text.toString()
            }
            arbitraryDataAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        return builder.create()
    }

    fun createEditDialog(key: String): AlertDialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val view: View = layoutInflater.inflate(R.layout.dialog_generic_input, null)
        val input: TextInputEditText = view.et_input
        builder.setTitle("Data Edit")
        builder.setView(view)
        builder.setCancelable(true)
        builder.setPositiveButton("Update") { dialog, _ ->
            beaconViewModel.beacon.value?.let {
                it.abstractDataMapped[key] = input.text.toString()
            }
            arbitraryDataAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }
        builder.setNeutralButton("Delete") { dialog, _ ->
            beaconViewModel.beacon.value?.abstractDataMapped?.remove(key)
            arbitraryDataAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        return builder.create()
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            BeaconArbitraryDataFragment()
    }
}
