package com.aconno.sensorics.ui.acnrange

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.ui.MainActivity
import com.aconno.sensorics.ui.readings.ReadingListViewModel
import kotlinx.android.synthetic.main.fragment_acnrange.*
import javax.inject.Inject

class AcnRangeFragment : Fragment() {

    private var macAddress: String = ""

    @Inject
    lateinit var readingListViewModel: ReadingListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_acnrange, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        val mainActivity = activity as MainActivity
        mainActivity.mainActivityComponent.inject(this)
        mainActivity.supportActionBar?.title = "AcnRange"

        arguments?.let {
            macAddress = it.getString(MAC_ADDRESS_EXTRA)
            readingListViewModel.init(macAddress)
            mainActivity.supportActionBar?.subtitle = macAddress
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

    companion object {

        private const val MAC_ADDRESS_EXTRA = "mac_address"

        fun newInstance(macAddress: String): AcnRangeFragment {
            val bundle = Bundle()
            bundle.putString(MAC_ADDRESS_EXTRA, macAddress)
            val fragment = AcnRangeFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}