package com.aconno.sensorics.ui.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.aconno.sensorics.LiveDataObserver
import com.aconno.sensorics.databinding.FragmentDashboardBinding
import com.aconno.sensorics.viewmodel.DashboardViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class DashboardFragment : DaggerFragment() {

    private var binding: FragmentDashboardBinding? = null

    @Inject
    lateinit var mViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dashboardBinding = FragmentDashboardBinding.inflate(layoutInflater, null, false)

        dashboardBinding.activityDashboardWebview.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
        }

        mViewModel.url.observe(viewLifecycleOwner, LiveDataObserver { loadUrl(it) })

        mViewModel.initViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun loadUrl(url: String) {
        val dashboardBinding = FragmentDashboardBinding.inflate(layoutInflater, null, false)
        dashboardBinding.activityDashboardWebview.loadUrl(url)
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