package com.aconno.sensorics.ui.readings

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.aconno.sensorics.BuildConfig
import com.aconno.sensorics.R
import com.aconno.sensorics.databinding.FragmentGenericReadingListBinding
import com.aconno.sensorics.databinding.ItemReadingBinding
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.ui.ActionListActivity
import com.aconno.sensorics.ui.MainActivity
import com.aconno.sensorics.ui.livegraph.LiveGraphOpener
import dagger.android.support.DaggerFragment
//import kotlinx.android.synthetic.main.fragment_generic_reading_list.*
//import kotlinx.android.synthetic.main.item_reading.view.*
import javax.inject.Inject

class GenericReadingListFragment : DaggerFragment() {

    private var binding: FragmentGenericReadingListBinding? = null
    private var itemReadingBinding: ItemReadingBinding? = null

    @Inject
    lateinit var readingListViewModel: ReadingListViewModel

    private var macAddress = ""

    private val views = hashMapOf<String, View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainActivity = activity as MainActivity

        setHasOptionsMenu(true)

        macAddress = getMacAddress(this)
        readingListViewModel.init(macAddress)
        mainActivity.supportActionBar?.title = getDeviceAlias()
        mainActivity.supportActionBar?.subtitle = macAddress
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        activity?.menuInflater?.inflate(R.menu.menu_readings, menu)
        menu.findItem(R.id.action_start_usecases_activity)?.isVisible = BuildConfig.DEBUG
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
        binding = FragmentGenericReadingListBinding.inflate(inflater, container, false)
        itemReadingBinding = ItemReadingBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        readingListViewModel.getReadingsLiveData()
            .observe(this, Observer {
                if (it != null) {
                    addReadings(it)
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        itemReadingBinding = null
    }

    private fun addReadings(readings: List<Reading>) {
        readings.sortedBy { it.name }.forEach { reading ->
            val view = views[reading.name]
            if (view == null) {
//                val newView =
//                    LayoutInflater.from(context)
//                        .inflate(R.layout.item_reading, binding?.listReadings, false)

                val newBinding =
                    ItemReadingBinding.inflate(layoutInflater, binding?.listReadings, false)
                val newView = newBinding.root

                newView.setOnClickListener {
                    if (activity is LiveGraphOpener) {
                        (activity as LiveGraphOpener).openLiveGraph(macAddress, reading.name)
                    }
                }

                newBinding.textReadingName.text = reading.name
                newBinding.textReadingValue.text = String.format("%.2f", reading.value.toFloat())
                binding?.listReadings?.addView(newView)
                views[reading.name] = newView
            } else {
                val newBinding =
                    ItemReadingBinding.inflate(layoutInflater, binding?.listReadings, false)
                newBinding.textReadingValue.text = String.format("%.2f", reading.value.toFloat())
            }
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

        fun newInstance(macAddress: String, deviceAlias: String, deviceName: String): Fragment {
            val bundle = Bundle()
            bundle.putString(MAC_ADDRESS_EXTRA, macAddress)
            bundle.putString(DEVICE_ALIAS_EXTRA, deviceAlias)
            bundle.putString(DEVICE_NAME_EXTRA, deviceName)
            val fragment = GenericReadingListFragment()
            fragment.arguments = bundle
            return fragment
        }

        private fun getMacAddress(fragment: GenericReadingListFragment): String {
            fragment.arguments?.let {
                return it.getString(MAC_ADDRESS_EXTRA) ?: ""
            }
            return ""
        }
    }
}