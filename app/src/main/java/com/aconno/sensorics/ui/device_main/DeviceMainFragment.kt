package com.aconno.sensorics.ui.device_main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.model.Reading
import dagger.android.support.DaggerFragment
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_device_main.*
import timber.log.Timber
import javax.inject.Inject

@SuppressLint("SetJavaScriptEnabled")
class DeviceMainFragment : DaggerFragment() {

    @Inject
    lateinit var sensorReadingFlow: Flowable<List<Reading>>
    private var sensorReadingFlowDisposable: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_device_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        web_view.webChromeClient = WebChromeClient()
        web_view.webViewClient = WebViewClient()
        web_view.settings.javaScriptEnabled = true
        web_view.loadUrl("file:///android_asset/device_screens/acnsensa/acnsensa.html")

        sensorReadingFlowDisposable = sensorReadingFlow
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { readings ->
                readings.forEach {
                    Timber.d("Reading, mac: ${it.device.macAddress}, name: ${it.name}, value: ${it.value}")
                    web_view.loadUrl("javascript:onSensorReading('${it.name}', '${it.value}')")
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sensorReadingFlowDisposable?.dispose()
    }

    companion object {

        private const val DEVICE_NAME_EXTRA = "device_name"

        private const val MAC_ADDRESS_EXTRA = "mac_address"

        fun newInstance(deviceName: String, macAddress: String): DeviceMainFragment {
            val deviceMainFragment = DeviceMainFragment()
            deviceMainFragment.arguments = Bundle().apply {
                putString(DEVICE_NAME_EXTRA, deviceName)
                putString(MAC_ADDRESS_EXTRA, macAddress)
            }
            return deviceMainFragment
        }
    }
}