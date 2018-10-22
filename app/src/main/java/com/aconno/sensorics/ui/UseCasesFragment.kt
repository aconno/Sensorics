package com.aconno.sensorics.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.aconno.sensorics.LiveDataObserver
import com.aconno.sensorics.R
import com.aconno.sensorics.viewmodel.UseCasesViewModel
import kotlinx.android.synthetic.main.fragment_use_cases.*
import javax.inject.Inject


class UseCasesFragment : Fragment() {

    @Inject
    lateinit var mViewModel: UseCasesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainActivity: MainActivity? = activity as? MainActivity
        mainActivity?.mainActivityComponent?.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_use_cases, container, false)

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val macAddress = arguments?.getString(UseCasesFragment.USECASES_MAC_ADDRESS_EXTRA)
        val name = arguments?.getString(UseCasesFragment.USECASES_NAME_EXTRA)

        if (macAddress != null && name != null) {

            activity_usecases_webview.apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
            }

            mViewModel.url.observe(this, LiveDataObserver { loadUrl(it) })

            mViewModel.mutableProgress.observe(this, LiveDataObserver { progressVisibility(it) })

            mViewModel.initViewModel(macAddress, name)
        }
    }

    private fun progressVisibility(it: Boolean) {
        progressbar.visibility = if (it) View.VISIBLE else View.GONE
        status_view.visibility = if (it) View.VISIBLE else View.GONE
        activity_usecases_webview.visibility = if (it) View.GONE else View.VISIBLE
    }

    override fun onDetach() {
        mViewModel.unsubscribe()
        super.onDetach()
    }

    private fun loadUrl(url: String) {
        if (url == "Error") {
            context?.let {
                val builder = AlertDialog.Builder(it)
                builder.setMessage("There are no UseCase files defined.")
                    .setNeutralButton("Ok") { _, _ ->
                        (context as AppCompatActivity).supportFragmentManager.popBackStack()
                    }

                builder.create()
                    .show()
            } ?: throw IllegalStateException("Activity cannot be null")

        } else {
            activity_usecases_webview.loadUrl(url)
        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel.subscribe()
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
