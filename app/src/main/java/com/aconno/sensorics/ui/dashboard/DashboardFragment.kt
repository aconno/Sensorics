package com.aconno.sensorics.ui.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.aconno.sensorics.LiveDataObserver
import com.aconno.sensorics.R
import com.aconno.sensorics.viewmodel.DashboardViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_dashboard.*
import javax.inject.Inject

class DashboardFragment : DaggerFragment() {

    @Inject
    lateinit var mViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_dashboard, container, false)

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity_dashboard_webview.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
        }

        mViewModel.url.observe(this, LiveDataObserver { loadUrl(it) })

        mViewModel.initViewModel()
    }

    private fun loadUrl(url: String) {
        activity_dashboard_webview.loadUrl(url)
    }

    override fun onResume() {
        super.onResume()
        mViewModel.subscribe()
    }

    override fun onDetach() {
        mViewModel.unsubscribe()
        super.onDetach()
    }

    companion object {

        fun newInstance(): DashboardFragment {
            return DashboardFragment()
        }
    }
}