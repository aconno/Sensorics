package com.aconno.sensorics.ui.readings

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.aconno.sensorics.BuildConfig
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.ui.ActionListActivity
import com.aconno.sensorics.ui.MainActivity
import com.aconno.sensorics.ui.livegraph.LiveGraphOpener
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_generic_reading_list.*
import kotlinx.android.synthetic.main.item_reading.view.*
import javax.inject.Inject

class GenericReadingListFragment : DaggerFragment() {
    @Inject
    lateinit var readingListViewModel: ReadingListViewModel

    private lateinit var macAddress: String
    private lateinit var deviceAlias: String
    private lateinit var deviceName: String

    private val views: MutableMap<String, View> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        macAddress = arguments?.getString(MAC_ADDRESS_EXTRA)
            ?: throw IllegalStateException("$this instantiated without MAC_ADDRESS_EXTRA")
        deviceAlias = arguments?.getString(DEVICE_ALIAS_EXTRA)
            ?: throw IllegalStateException("$this instantiated without DEVICE_ALIAS_EXTRA")
        deviceName = arguments?.getString(DEVICE_NAME_EXTRA)
            ?: throw IllegalStateException("$this instantiated without DEVICE_NAME_EXTRA")


        readingListViewModel.init(macAddress)

        (activity as? MainActivity)?.supportActionBar?.apply {
            title = deviceAlias
            subtitle = macAddress
        }
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        activity?.menuInflater?.inflate(R.menu.menu_readings, menu)
        menu.findItem(R.id.action_start_usecases_activity)?.isVisible = BuildConfig.FLAVOR == "dev"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        context?.let { context ->
            when (item.itemId) {
                R.id.action_start_actions_activity -> {
                    ActionListActivity.start(context)
                    return true
                }
                R.id.action_start_usecases_activity -> {
                    (activity as? MainActivity)?.onUseCaseClicked(macAddress, deviceName)
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
        return inflater.inflate(R.layout.fragment_generic_reading_list, container, false)
    }

    override fun onResume() {
        super.onResume()
        readingListViewModel.getReadingsLiveData()
            .observe(this, Observer {
                it?.let { addReadings(it) }
            })
        // TODO: Why is onPause not overridden
    }

    private fun addReadings(readings: List<Reading>) {
        readings.sortedBy { it.name }.forEach { reading ->
            views.getOrPut(reading.name, {
                LayoutInflater.from(context).inflate(
                    R.layout.item_reading, list_readings, false
                ).apply {
                    setOnClickListener {
                        (activity as? LiveGraphOpener)?.openLiveGraph(macAddress, reading.name)
                    }
                    text_reading_name.text = reading.name
                }
            }).let {
                it.text_reading_value.text = String.format("%.2f", reading.value.toFloat())
            }
        }
    }

    companion object {
        private const val MAC_ADDRESS_EXTRA = "mac_address"
        private const val DEVICE_ALIAS_EXTRA = "device_alias"
        private const val DEVICE_NAME_EXTRA = "device_name"

        fun newInstance(macAddress: String, deviceAlias: String, deviceName: String): Fragment {
            return GenericReadingListFragment().apply {
                arguments = Bundle().apply {
                    putString(MAC_ADDRESS_EXTRA, macAddress)
                    putString(DEVICE_ALIAS_EXTRA, deviceAlias)
                    putString(DEVICE_NAME_EXTRA, deviceName)
                }
            }
        }
    }
}