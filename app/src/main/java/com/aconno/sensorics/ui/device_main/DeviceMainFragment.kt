package com.aconno.sensorics.ui.device_main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.aconno.sensorics.BuildConfig
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.interactor.filter.FilterByMacUseCase
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.ui.ActionListActivity
import com.aconno.sensorics.ui.MainActivity
import com.aconno.sensorics.ui.livegraph.LiveGraphOpener
import com.aconno.sensorics.viewmodel.resources.MainResourceViewModel
import dagger.android.support.DaggerFragment
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_device_main.*
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject


@SuppressLint("SetJavaScriptEnabled")
class DeviceMainFragment : DaggerFragment() {

    @Inject
    lateinit var sensorReadingFlow: Flowable<List<Reading>> //TODO: Move this to the view model

    @Inject
    lateinit var filterByMacUseCase: FilterByMacUseCase

    private var sensorReadingFlowDisposable: Disposable? = null

    @Inject
    lateinit var mainResourceViewModel: MainResourceViewModel
    private var getResourceDisposable: Disposable? = null

    private lateinit var deviceName: String
    private lateinit var macAddress: String
    private lateinit var deviceAlias: String

    private var webViewBundle: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
    }

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
        deviceAlias = arguments?.getString(DEVICE_ALIAS_EXTRA) ?:
                throw IllegalArgumentException("Device alias is not defined")
    }

    override fun onResume() {
        super.onResume()
        val mainActivity: MainActivity = context as MainActivity

        mainActivity?.supportActionBar?.title = deviceAlias
        mainActivity?.supportActionBar?.subtitle = macAddress
        if (!mainActivity.isScanning()) {
            showAlertDialog(mainActivity)

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
                    (activity as MainActivity).onUseCaseClicked(macAddress, deviceName)
                    return true
                }
                else -> {
                    //Do nothing
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupWebView()
    }

    private fun setupWebView() {
        web_view.webChromeClient = WebChromeClient()
        web_view.webViewClient = MyWebViewClient()
        web_view.addJavascriptInterface(WebViewJavaScriptInterface(), "app")
        web_view.settings.javaScriptEnabled = true

        if (webViewBundle != null) {
            web_view.restoreState(webViewBundle)
        } else {
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

    }

    private fun subscribeOnSensorReadings() {
        sensorReadingFlowDisposable = sensorReadingFlow
            .concatMap { filterByMacUseCase.execute(it, macAddress).toFlowable() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { readings ->
                var json_values = generateJsonArray(readings)
                //values = "acnsensa"
                Timber.i("Values in Json $json_values")

                web_view.loadUrl("javascript:onSensorReadings('$json_values')")
                /*readings.forEach {
                    web_view.loadUrl("javascript:onSensorReading('${it.name}', '${it.value}')")
                }*/
            }
    }

    private fun generateJsonArray(readings: List<Reading>?): String {

        val jsonObject = JSONObject()
        readings?.forEach {

            jsonObject.put(it.name, it.value)

        }


        return jsonObject.toString()
    }

    override fun onDestroyView() {
        webViewBundle = Bundle()
        web_view.saveState(webViewBundle)

        super.onDestroyView()
        getResourceDisposable?.dispose()
        sensorReadingFlowDisposable?.dispose()
    }

    inner class MyWebViewClient : WebViewClient() {

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            subscribeOnSensorReadings()
        }
    }

    inner class WebViewJavaScriptInterface {

        @JavascriptInterface
        fun openLiveGraph(sensorName: String) {
            activity?.let {
                if (it is LiveGraphOpener) {
                    (it as LiveGraphOpener).openLiveGraph(macAddress, sensorName)
                }
            }
        }
    }

    companion object {

        private const val DEVICE_NAME_EXTRA = "device_name"
        private const val MAC_ADDRESS_EXTRA = "mac_address"
        private const val DEVICE_ALIAS_EXTRA = "device_alias"

        fun newInstance(
            macAddress: String,
            deviceAlias: String,
            deviceName: String
        ): DeviceMainFragment {
            val deviceMainFragment = DeviceMainFragment()
            deviceMainFragment.arguments = Bundle().apply {
                putString(DEVICE_NAME_EXTRA, deviceName)
                putString(MAC_ADDRESS_EXTRA, macAddress)
                putString(DEVICE_ALIAS_EXTRA, deviceAlias)

            }
            return deviceMainFragment
        }
    }

    private fun showAlertDialog(mainActivity: MainActivity) {

        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(
            mainActivity
        )

        // set title
        alertDialogBuilder.setTitle(resources.getString(R.string.start_scan_popup))

        // set dialog message
        alertDialogBuilder
            .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->

                mainActivity.startScanOperation()
                dialog.cancel()

            }
            .setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->

                dialog.cancel()
            }


        // create alert dialog
        val alertDialog = alertDialogBuilder.create()

        // show it
        alertDialog.show()
    }

}