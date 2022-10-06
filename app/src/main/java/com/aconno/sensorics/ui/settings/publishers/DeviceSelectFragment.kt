package com.aconno.sensorics.ui.settings.publishers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aconno.sensorics.R
import com.aconno.sensorics.databinding.FragmentDevicesBinding
import com.aconno.sensorics.domain.ifttt.outcome.PublishType
import com.aconno.sensorics.model.BasePublishModel
import com.aconno.sensorics.model.DeviceRelationModel
import com.aconno.sensorics.ui.base.BaseFragment
import com.aconno.sensorics.viewmodel.DeviceSelectViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class DeviceSelectFragment : BaseFragment() {

    private var binding: FragmentDevicesBinding? = null

    @Inject
    lateinit var deviceSelectViewModel: DeviceSelectViewModel

    private var publisherId: Long? = null
    private var publisherType: PublishType? = null
    private var deviceList: MutableList<DeviceRelationModel> = mutableListOf()
    private lateinit var adapter: DeviceSelectAdapter

    private val itemCheckChangeListener = object : DeviceSelectAdapter.ItemCheckChangeListener {
        override fun onItemCheckedChange(position: Int, isChecked: Boolean) {
            deviceList[position].related = isChecked
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        publisherId = arguments?.getLong(DEVICE_SELECT_PUBLISHER_ID_KEY)
        publisherType = arguments?.getSerializable(DEVICE_SELECT_PUBLISHER_TYPE_KEY) as? PublishType
//        arguments?.let { args ->
//            args.getString(DEVICE_SELECT_PUBLISHER_TYPE_KEY)?.let { type ->
//                publisherId = args.getLong(DEVICE_SELECT_PUBLISHER_ID_KEY)
//                publisherType = args.getSerializable(DEVICE_SELECT_PUBLISHER_TYPE_KEY) as? PublishType
//            }
//        }
//         TODO: Find out why this doesn't work
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDevicesBinding.inflate(inflater, container, false)

        adapter = DeviceSelectAdapter(
            deviceList,
            itemCheckChangeListener
        )
        binding?.listDevices?.layoutManager = LinearLayoutManager(context)
        binding?.listDevices?.adapter = adapter
        binding?.listDevices?.isNestedScrollingEnabled = false

        queryDevices()

        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
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
                    binding?.emptyView?.visibility = View.GONE
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