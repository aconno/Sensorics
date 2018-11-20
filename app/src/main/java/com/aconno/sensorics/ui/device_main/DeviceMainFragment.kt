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
import com.aconno.sensorics.viewmodel.resources.MainResourceViewModel
import dagger.android.support.DaggerFragment
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_device_main.*
import javax.inject.Inject

@SuppressLint("SetJavaScriptEnabled")
class DeviceMainFragment : DaggerFragment() {

    @Inject
    lateinit var sensorReadingFlow: Flowable<List<Reading>> //TODO: Move this to the view model
    private var sensorReadingFlowDisposable: Disposable? = null

    @Inject
    lateinit var mainResourceViewModel: MainResourceViewModel
    private var getResourceDisposable: Disposable? = null

    private lateinit var deviceName: String
    private lateinit var macAddress: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loadArguments()
        return inflater.inflate(R.layout.fragment_device_main, container, false)
    }

    private fun loadArguments() {
        deviceName = arguments?.getString(DEVICE_NAME_EXTRA) ?:
                throw IllegalArgumentException("Device name is not defined")
        macAddress = arguments?.getString(MAC_ADDRESS_EXTRA) ?:
                throw IllegalArgumentException("Device mac address is not defined")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupWebView()
        subscribeOnSensorReadings()
    }

    private fun setupWebView() {
        web_view.webChromeClient = WebChromeClient()
        web_view.webViewClient = WebViewClient()
        web_view.settings.javaScriptEnabled = true

        getResourceDisposable = mainResourceViewModel.getResourcePath(deviceName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { resourcePath ->
                    text_error_message.visibility = View.INVISIBLE
                    web_view.loadUrl(resourcePath)
                },
                { throwable ->
                    text_error_message.visibility = View.VISIBLE
                    text_error_message.text = throwable.message
                })
    }

    private fun subscribeOnSensorReadings() {
        sensorReadingFlowDisposable = sensorReadingFlow
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { readings ->
                readings.forEach {
                    web_view.loadUrl("javascript:onSensorReading('${it.name}', '${it.value}')")
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        getResourceDisposable?.dispose()
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