package com.aconno.sensorics.ui.readings

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.ui.ActionListActivity
import com.aconno.sensorics.ui.LiveGraphActivity
import com.aconno.sensorics.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_generic_reading_list.*
import kotlinx.android.synthetic.main.item_reading.view.*
import javax.inject.Inject

class GenericReadingListFragment : Fragment() {

    @Inject
    lateinit var readingListViewModel: ReadingListViewModel

    private var macAddress = ""

    private val views = hashMapOf<String, View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainActivity = activity as MainActivity
        mainActivity.mainActivityComponent.inject(this)

        setHasOptionsMenu(true)

        macAddress = getMacAddress(this)
        readingListViewModel.init(macAddress)
        mainActivity.supportActionBar?.title = getDeviceAlias()
        mainActivity.supportActionBar?.subtitle = macAddress
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        activity?.menuInflater?.inflate(R.menu.menu_readings, menu)
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
        return inflater.inflate(R.layout.fragment_generic_reading_list, container, false)
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

    private fun addReadings(readings: List<Reading>) {
        readings.sortedBy { it.name }.forEach { reading ->
            val view = views[reading.name]
            if (view == null) {
                val newView =
                    LayoutInflater.from(context)
                        .inflate(R.layout.item_reading, list_readings, false)
                context?.let { context ->
                    newView.setOnClickListener {
                        LiveGraphActivity.start(context, macAddress, reading.name)
                    }
                }
                newView.text_reading_name.text = reading.name
                newView.text_reading_value.text = String.format("%.2f", reading.value.toFloat())
                list_readings.addView(newView)
                views[reading.name] = newView
            } else {
                view.text_reading_value.text = String.format("%.2f", reading.value.toFloat())
            }
        }
    }

    private fun getDeviceAlias(): String {
        arguments?.let {
            return it.getString(DEVICE_ALIAS_EXTRA) ?: ""
        }
        return ""
    }

    private fun getDeviceName(): String {
        arguments?.let {
            return it.getString(DEVICE_NAME_EXTRA) ?: ""
        }
        return ""
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