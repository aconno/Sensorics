package com.aconno.acnsensa.ui.settings.publishers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.R
import com.aconno.acnsensa.dagger.deviceselect.DaggerDeviceSelectComponent
import com.aconno.acnsensa.dagger.deviceselect.DeviceSelectComponent
import com.aconno.acnsensa.dagger.deviceselect.DeviceSelectModule
import com.aconno.acnsensa.model.*
import com.aconno.acnsensa.ui.base.BaseFragment
import com.aconno.acnsensa.viewmodel.DeviceSelectViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_devices.*
import timber.log.Timber
import javax.inject.Inject

class DeviceSelectFragment : BaseFragment() {

    @Inject
    lateinit var deviceSelectViewModel: DeviceSelectViewModel

    private var basePublishModel: BasePublishModel? = null
    private var deviceList: MutableList<DeviceRelationModel> = mutableListOf()
    private lateinit var adapter: DeviceSelectAdapter

    private val itemCheckChangeListener = object : DeviceSelectAdapter.ItemCheckChangeListener {
        override fun onItemCheckedChange(position: Int, isChecked: Boolean) {
            deviceList[position]
                .related = isChecked
        }
    }

    private val actionListComponent: DeviceSelectComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? =
            context?.applicationContext as? AcnSensaApplication

        DaggerDeviceSelectComponent.builder()
            .appComponent(acnSensaApplication?.appComponent)
            .deviceSelectModule(DeviceSelectModule(this))
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionListComponent.inject(this)
        if (arguments != null && arguments!!.containsKey(DEVICE_SELECT_FRAGMENT_KEY)) {
            basePublishModel = arguments!!.getParcelable(DEVICE_SELECT_FRAGMENT_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_devices, container, false)
        val listView = view.findViewById<ListView>(R.id.list_devices)

        adapter = DeviceSelectAdapter(
            context!!,
            deviceList,
            itemCheckChangeListener
        )
        listView.adapter = adapter
        listView.isNestedScrollingEnabled = true

        queryDevices()

        return view
    }

    private fun queryDevices() {
        val single: Single<List<DeviceRelationModel>>

        if (basePublishModel == null) {
            single = deviceSelectViewModel.getAllDevices()
        } else {
            single = when (basePublishModel) {
                is GooglePublishModel -> deviceSelectViewModel.getAllDevicesWithGoogleRelation(
                    basePublishModel!!.id
                )
                is RESTPublishModel -> deviceSelectViewModel.getAllDevicesWithRESTRelation(
                    basePublishModel!!.id
                )
                is MqttPublishModel -> deviceSelectViewModel.getAllDevicesWithMqttRelation(
                    basePublishModel!!.id
                )
                else -> throw IllegalArgumentException()
            }.firstOrError()
        }

        val subscribe = single.filter { !it.isEmpty() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                empty_view.visibility = View.GONE
                deviceList.clear()
                deviceList.addAll(it)
                adapter.notifyDataSetChanged()
                Timber.d("${it.size}")
            }

        addDisposable(subscribe)
    }

    fun getDevices(): MutableList<DeviceRelationModel> {
        return deviceList
    }

    companion object {
        private const val DEVICE_SELECT_FRAGMENT_KEY = "DEVICE_SELECT_FRAGMENT_KEY"

        @JvmStatic
        fun newInstance(basePublishModel: BasePublishModel? = null): DeviceSelectFragment {
            val fragment = DeviceSelectFragment()

            basePublishModel?.let {
                val bundle = Bundle()
                bundle.putParcelable(DEVICE_SELECT_FRAGMENT_KEY, basePublishModel)

                fragment.arguments = bundle
            }

            return fragment
        }
    }
}