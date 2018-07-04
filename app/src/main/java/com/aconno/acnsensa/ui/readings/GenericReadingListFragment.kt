package com.aconno.acnsensa.ui.readings

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.acnsensa.R
import com.aconno.acnsensa.domain.model.Reading
import com.aconno.acnsensa.ui.LiveGraphActivity
import com.aconno.acnsensa.ui.MainActivity
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

        macAddress = getMacAddress(this)
        readingListViewModel.init(macAddress)
        mainActivity.supportActionBar?.title = macAddress
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
                    LayoutInflater.from(context).inflate(R.layout.item_reading, null)
                context?.let { context ->
                    // TODO: Implement icon configuration
                    val iconId =
                        context.resources.getIdentifier("it.icon", "drawable", context.packageName)
                    if (iconId != 0) {
                        newView.image_reading_icon.setImageResource(iconId)
                    }
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

    companion object {

        private const val MAC_ADDRESS_EXTRA = "mac_address"

        fun newInstance(macAddress: String): Fragment {
            val bundle = Bundle()
            bundle.putString(MAC_ADDRESS_EXTRA, macAddress)
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