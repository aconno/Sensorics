package com.aconno.sensorics.ui.settings.publishers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.ifttt.outcome.PublishType
import com.aconno.sensorics.model.BasePublishModel
import com.aconno.sensorics.model.DeviceRelationModel
import com.aconno.sensorics.ui.base.BaseFragment
import com.aconno.sensorics.viewmodel.DeviceSelectViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_devices.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class DeviceSelectFragment : BaseFragment() {

    @Inject
    lateinit var deviceSelectViewModel: DeviceSelectViewModel

    private var publisherId: Long? = null
    private var publisherType: PublishType? = null
    private var deviceList: MutableList<DeviceRelationModel> = mutableListOf()
    private lateinit var adapter: DeviceSelectAdapter

    private val itemCheckChangeListener = object : DeviceSelectAdapter.ItemCheckChangeListener {
        override fun onItemCheckedChange(position: Int, isChecked: Boolean) {
            deviceList[position]
                .related = isChecked
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            args.getString(DEVICE_SELECT_PUBLISHER_TYPE_KEY)?.let { type ->
                publisherId = args.getLong(DEVICE_SELECT_PUBLISHER_ID_KEY)
                publisherType = args.getSerializable(DEVICE_SELECT_PUBLISHER_TYPE_KEY) as? PublishType
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_devices, container, false)
        val listView = view.findViewById<RecyclerView>(R.id.list_devices)

        adapter = DeviceSelectAdapter(
            deviceList,
            itemCheckChangeListener
        )
        listView.layoutManager = LinearLayoutManager(context)
        listView.adapter = adapter
        listView.isNestedScrollingEnabled = true

        queryDevices()

        return view
    }

    private fun queryDevices() {
        GlobalScope.launch(Dispatchers.Default) {

            val single: Single<List<DeviceRelationModel>> = (publisherType?.let { type ->
                publisherId?.let { id ->
                    deviceSelectViewModel.getAllDevicesWithRelation(id, type).firstOrError()
                }
            }) ?: deviceSelectViewModel.getAllDevices()

            val subscribe = single.filter { it.isNotEmpty() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    empty_view.visibility = View.GONE
                    deviceList.clear()
                    deviceList.addAll(it)
                    adapter.notifyDataSetChanged()
                    Timber.d("devices size is ${it.size}")
                }

            addDisposable(subscribe)
        }
    }

    fun getDevices(): MutableList<DeviceRelationModel> {
        return deviceList
    }

    companion object {
        private const val DEVICE_SELECT_PUBLISHER_ID_KEY = "DEVICE_SELECT_PUBLISHER_ID_KEY"
        private const val DEVICE_SELECT_PUBLISHER_TYPE_KEY = "DEVICE_SELECT_PUBLISHER_TYPE_KEY"

        @JvmStatic
        fun newInstance(model: BasePublishModel? = null): DeviceSelectFragment {
            val fragment = DeviceSelectFragment()

            model?.let {
                fragment.arguments = Bundle().apply {
                    putLong(DEVICE_SELECT_PUBLISHER_ID_KEY, it.id)
                    putSerializable(DEVICE_SELECT_PUBLISHER_TYPE_KEY, it.type)
                }
            }

            return fragment
        }
    }
}