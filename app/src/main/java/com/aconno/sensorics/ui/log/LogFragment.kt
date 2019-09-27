package com.aconno.sensorics.ui.log

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.aconno.sensorics.R
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_log.*
import timber.log.Timber

@SuppressLint("SetJavaScriptEnabled")
class LogFragment : Fragment() {

    private lateinit var logsFlow: Flowable<LogActivity.LogLine>

    private var logsFlowDisposable: Disposable? = null

    private var webViewBundle: Bundle? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_log, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupWebView()
    }

    private fun setupWebView() {
        web_view_log.webChromeClient = WebChromeClient()
        web_view_log.webViewClient = MyWebViewClient()
        web_view_log.settings.javaScriptEnabled = true

        if (webViewBundle != null) {
            web_view_log.restoreState(webViewBundle)
        } else {
            web_view_log.loadUrl("file:///android_asset/resources/logs/logs.html")
        }
    }

    private fun subscribeLogs() {
        logsFlowDisposable = logsFlow
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { logLine ->
                web_view_log.evaluateJavascript(
                    "addLogLine('${logLine.toJson()}')"
                ) { }
            }
    }

    override fun onDestroyView() {
        webViewBundle = Bundle()
        web_view_log.saveState(webViewBundle)
        super.onDestroyView()

        logsFlowDisposable?.dispose()
    }

    fun importFromWebView(){
        web_view_log.evaluateJavascript(
            "exportDisplayed()"
        ) { logs -> Timber.d(logs)}
    }

    fun setMinLogLevel(logLevel: Char) {
        web_view_log.evaluateJavascript(
            "setMinLogLevel('$logLevel')"
        ) {}
    }

    fun toggleAutoScroll() {
        web_view_log.evaluateJavascript(
            "toggleAutoScroll()"
        ) {}
    }

    fun setBufferSize(newBufferSize: Int) {
        web_view_log.evaluateJavascript(
            "setBufferSize($newBufferSize)"
        ) {}
    }

    fun searchFor(columnName: String, keyword: String) {
        web_view_log.evaluateJavascript(
            "searchFor('$columnName', '$keyword')"
        ) {}
    }

    inner class MyWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            subscribeLogs()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(pSubject: PublishSubject<LogActivity.LogLine>): LogFragment {
            val logFragment = LogFragment()
            // TODO: Inject using Dagger?
            logFragment.logsFlow = pSubject.toFlowable(BackpressureStrategy.BUFFER)
            return logFragment
        }
    }


}
