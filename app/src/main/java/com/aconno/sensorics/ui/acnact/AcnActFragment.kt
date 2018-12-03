package com.aconno.sensorics.ui.acnact

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.view.*
import com.aconno.sensorics.BuildConfig
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.ui.ActionListActivity
import com.aconno.sensorics.ui.MainActivity
import com.aconno.sensorics.ui.readings.ReadingListViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_acnact.*
import javax.inject.Inject

class AcnActFragment : DaggerFragment() {

    private var macAddress: String = ""

    @Inject
    lateinit var readingListViewModel: ReadingListViewModel

    private var buttonCounter = 0
    private var latestAdvId = -1234

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
        return inflater.inflate(R.layout.fragment_acnact, container, false)
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
        val readingState = readings.find { it.name == "ButtonState" }//This is from format json
        val readingCounter = readings.find { it.name == "ButtonCounter" }//This is from format json

        if (readingState != null && readingCounter != null) {

            if (latestAdvId != readingCounter.value.toInt()
                && readingState.value.toInt() == BUTTON_PRESSED
            ) {
                latestAdvId = readingCounter.value.toInt()

                img_button.isSelected = true
                text_counter.text = "${++buttonCounter}"
            } else if (latestAdvId == readingCounter.value.toInt()
                && readingState.value.toInt() == BUTTON_NOT_PRESSED
            ) {
                img_button.isSelected = false
            } else {
                //No-Op
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
        private const val BUTTON_PRESSED: Int = 1
        private const val BUTTON_NOT_PRESSED: Int = 0

        private const val MAC_ADDRESS_EXTRA = "mac_address"
        private const val DEVICE_ALIAS_EXTRA = "device_alias"
        private const val DEVICE_NAME_EXTRA = "device_name"

        fun newInstance(
            macAddress: String,
            deviceAlias: String,
            deviceName: String
        ): AcnActFragment {
            val bundle = Bundle()
            bundle.putString(MAC_ADDRESS_EXTRA, macAddress)
            bundle.putString(DEVICE_ALIAS_EXTRA, deviceAlias)
            bundle.putString(DEVICE_NAME_EXTRA, deviceName)
            val fragment = AcnActFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
