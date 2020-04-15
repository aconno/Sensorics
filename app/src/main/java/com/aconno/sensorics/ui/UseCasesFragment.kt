package com.aconno.sensorics.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import com.aconno.sensorics.LiveDataObserver
import com.aconno.sensorics.R
import com.aconno.sensorics.viewmodel.UseCasesViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_use_cases.*
import javax.inject.Inject


class UseCasesFragment : DaggerFragment() {

    @Inject
    lateinit var useCasesViewModel: UseCasesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_use_cases, container, false)

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val macAddress = arguments?.getString(USECASES_MAC_ADDRESS_EXTRA)
        val name = arguments?.getString(USECASES_NAME_EXTRA)

        if (macAddress != null && name != null) {
            activity_usecases_webview.apply {
                webChromeClient = WebChromeClient()
                webViewClient = MyWebViewClient()
                settings.javaScriptEnabled = true
                settings.loadWithOverviewMode = true
                settings.builtInZoomControls = true
                settings.displayZoomControls = false
            }

            useCasesViewModel.url.observe(viewLifecycleOwner, LiveDataObserver {
                loadUrl(it)
            })
            useCasesViewModel.urlError.observe(viewLifecycleOwner, LiveDataObserver {
                showError()
            })

            useCasesViewModel.mutableShowProgress.observe(viewLifecycleOwner, LiveDataObserver {
                showProgressBar()
            })
            useCasesViewModel.mutableHideProgress.observe(viewLifecycleOwner, LiveDataObserver {
                hideProgressBar()
            })

            useCasesViewModel.initViewModel(macAddress, name)
        }
    }

    private fun showError() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.no_use_case_files_defined)
            .setNeutralButton(R.string.ok) { _, _ ->
                requireActivity().supportFragmentManager.popBackStack()
            }
            .create()
            .show()
    }

    private fun hideProgressBar() {
        progressbar.visibility = View.GONE
        status_view.visibility = View.GONE
        activity_usecases_webview.visibility = View.VISIBLE
    }

    private fun showProgressBar() {
        progressbar.visibility = View.VISIBLE
        status_view.visibility = View.VISIBLE
        activity_usecases_webview.visibility = View.GONE
    }

    override fun onDetach() {
        useCasesViewModel.unsubscribe()
        super.onDetach()
    }

    private fun loadUrl(url: String) {
        activity_usecases_webview.loadUrl(url)
    }

    inner class MyWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            useCasesViewModel.subscribe()
        }
    }

    companion object {
        private const val USECASES_MAC_ADDRESS_EXTRA = "mac_address"
        private const val USECASES_NAME_EXTRA = "name"

        fun newInstance(macAddress: String, name: String): UseCasesFragment {
            return UseCasesFragment().apply {
                arguments = Bundle().apply {
                    putString(USECASES_MAC_ADDRESS_EXTRA, macAddress)
                    putString(USECASES_NAME_EXTRA, name)
                }
            }
        }
    }
}
