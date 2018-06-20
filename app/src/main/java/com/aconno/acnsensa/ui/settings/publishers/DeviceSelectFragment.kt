package com.aconno.acnsensa.ui.settings.publishers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.acnsensa.model.BasePublishModel
import com.aconno.acnsensa.ui.base.BaseFragment

class DeviceSelectFragment : BaseFragment() {
    private lateinit var basePublishModel: BasePublishModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null && arguments!!.containsKey(DEVICE_SELECT_FRAGMENT_KEY)) {
            basePublishModel = arguments!!.getParcelable(DEVICE_SELECT_FRAGMENT_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    companion object {
        private const val DEVICE_SELECT_FRAGMENT_KEY = "DEVICE_SELECT_FRAGMENT_KEY"

        @JvmStatic
        fun newInstance(basePublishModel: BasePublishModel): DeviceSelectFragment {
            val fragment = DeviceSelectFragment()

            val bundle = Bundle()
            bundle.putParcelable(DEVICE_SELECT_FRAGMENT_KEY, basePublishModel)

            fragment.arguments = bundle
            return fragment
        }
    }
}