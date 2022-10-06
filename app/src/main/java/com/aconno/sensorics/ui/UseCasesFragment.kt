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
import androidx.appcompat.app.AppCompatActivity
import com.aconno.sensorics.LiveDataObserver
import com.aconno.sensorics.databinding.FragmentUseCasesBinding
import com.aconno.sensorics.viewmodel.UseCasesViewModel
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject


class UseCasesFragment : DaggerFragment() {

    private var binding: FragmentUseCasesBinding? = null

    @Inject
    lateinit var mViewModel: UseCasesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUseCasesBinding.inflate(inflater, container, false)

        return binding?.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.e("onViewCreated")

        val macAddress = arguments?.getString(USECASES_MAC_ADDRESS_EXTRA)
        val name = arguments?.getString(USECASES_NAME_EXTRA)

        if (macAddress != null && name != null) {

            binding?.activityUsecasesWebview?.apply {
                webChromeClient = WebChromeClient()
                webViewClient = MyWebViewClient()
                settings.javaScriptEnabled = true
                settings.loadWithOverviewMode = true
                settings.builtInZoomControls = true
                settings.displayZoomControls = false
            }

            mViewModel.url.observe(viewLifecycleOwner, LiveDataObserver { loadUrl(it) })
            mViewModel.urlError.observe(viewLifecycleOwner, LiveDataObserver { showError() })

            mViewModel.mutableShowProgress.observe(
                viewLifecycleOwner,
                LiveDataObserver { showProgressBar() })
            mViewModel.mutableHideProgress.observe(
                viewLifecycleOwner,
                LiveDataObserver { hideProgressBar() })

            mViewModel.initViewModel(macAddress, name)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun showError() {
        context?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("There are no UseCase files defined.")
                .setNeutralButton("Ok") { _, _ ->
                    (context as AppCompatActivity).supportFragmentManager.popBackStack()
                }

            builder.create()
                .show()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun hideProgressBar() {
        binding?.progressbar?.visibility = View.GONE
        binding?.statusView?.visibility = View.GONE
        binding?.activityUsecasesWebview?.visibility = View.VISIBLE
    }

    private fun showProgressBar() {
        binding?.progressbar?.visibility = View.VISIBLE
        binding?.statusView?.visibility = View.VISIBLE
        binding?.activityUsecasesWebview?.visibility = View.GONE
    }

    override fun onDetach() {
        mViewModel.unsubscribe()
        super.onDetach()
    }

    private fun loadUrl(url: String) {
        binding?.activityUsecasesWebview?.loadUrl(url)
    }

    inner class MyWebViewClient : WebViewClient() {

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            mViewModel.subscribe()
        }
    }

    companion object {
        private const val USECASES_MAC_ADDRESS_EXTRA = "mac_address"
        private const val USECASES_NAME_EXTRA = "name"

        fun newInstance(macAddress: String, name: String): UseCasesFragment {
            val useCasesFragment = UseCasesFragment()
            val bundle = Bundle()
            bundle.putString(USECASES_MAC_ADDRESS_EXTRA, macAddress)
            bundle.putString(USECASES_NAME_EXTRA, name)
            useCasesFragment.arguments = bundle
            return useCasesFragment
        }
    }
}
