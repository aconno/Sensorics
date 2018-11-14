package com.aconno.sensorics.ui.acnrange

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.view.*
import com.aconno.sensorics.BuildConfig
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.ui.ActionListActivity
import com.aconno.sensorics.ui.MainActivity
import com.aconno.sensorics.ui.livegraph.LiveGraphOpener
import com.aconno.sensorics.ui.readings.ReadingListViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_acnrange.*
import javax.inject.Inject

class AcnRangeFragment : DaggerFragment() {

    private var macAddress: String = ""

    @Inject
    lateinit var readingListViewModel: ReadingListViewModel

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val mainActivity = activity as MainActivity
        mainActivity.supportActionBar?.title = getDeviceAlias()

        setHasOptionsMenu(true)

        arguments?.let {
            macAddress = it.getString(MAC_ADDRESS_EXTRA, "")
            readingListViewModel.init(macAddress)
            mainActivity.supportActionBar?.subtitle = macAddress
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        activity?.menuInflater?.inflate(R.menu.menu_readings, menu)
        menu?.findItem(R.id.action_start_usecases_activity)?.isVisible = BuildConfig.FLAVOR == "dev"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        context?.let { context ->
            when (item.itemId) {
                R.id.action_start_actions_activity -> {
                    ActionListActivity.start(context)
                    return true
                }
                R.id.action_start_usecases_activity -> {
                    (activity as MainActivity).onUseCaseClicked(macAddress, getDeviceName())
                    return true
                }
                else -> {
                    //Do nothing
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_acnrange, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        frame_fragment.setOnClickListener {
            if (activity is LiveGraphOpener) {
                (activity as LiveGraphOpener).openLiveGraph(macAddress, "Range")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        readingListViewModel.getReadingsLiveData()
            .observe(this, Observer {
                if (it != null) {
                    processReadings(it)
                }
            })
    }

    private fun processReadings(readings: List<Reading>) {
        val reading = readings.find { it.name == "Range" }
        if (reading != null) {
            text_range.text = getString(R.string.reading_value_acnrange, reading.value.toInt())
        }
    }

    private fun getDeviceAlias(): String {
        return arguments?.getString(DEVICE_ALIAS_EXTRA) ?: ""
    }

    private fun getDeviceName(): String {
        return arguments?.getString(DEVICE_NAME_EXTRA) ?: ""
    }

    companion object {

        private const val MAC_ADDRESS_EXTRA = "mac_address"
        private const val DEVICE_ALIAS_EXTRA = "device_alias"
        private const val DEVICE_NAME_EXTRA = "device_name"

        fun newInstance(
            macAddress: String,
            deviceAlias: String,
            deviceName: String
        ): AcnRangeFragment {
            val bundle = Bundle()
            bundle.putString(MAC_ADDRESS_EXTRA, macAddress)
            bundle.putString(DEVICE_ALIAS_EXTRA, deviceAlias)
            bundle.putString(DEVICE_NAME_EXTRA, deviceName)
            val fragment = AcnRangeFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}