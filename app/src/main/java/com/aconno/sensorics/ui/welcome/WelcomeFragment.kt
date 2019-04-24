package com.aconno.sensorics.ui.welcome


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.aconno.sensorics.BuildConfig
import com.aconno.sensorics.R
import com.aconno.sensorics.ui.dashboard.DashboardFragment
import kotlinx.android.synthetic.main.fragment_welcome.*

class WelcomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        activity?.menuInflater?.inflate(R.menu.fragment_welcome, menu)
        menu.findItem(R.id.action_start_dashboard).isVisible =
            BuildConfig.FLAVOR == DEV_BUILD_FLAVOR
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_close_dashboard?.setOnClickListener {
            val fragment = childFragmentManager.fragments[0]
            childFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.exit_to_right, R.anim.exit_to_right)
                .remove(fragment)
                .commit()

            ll_dashboard?.postDelayed({
                ll_dashboard?.visibility = View.GONE
            }, 700)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        context?.let {
            when (item.itemId) {
                R.id.action_start_dashboard -> {
                    if (ll_dashboard.visibility == View.GONE) {
                        ll_dashboard.visibility = View.VISIBLE
                        childFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)
                            .replace(
                                R.id.fl_dashboard,
                                DashboardFragment.newInstance()
                            )
                            .commit()
                    }
                    return true
                }
                else -> {
                    //Do Nothing
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {

        const val DEV_BUILD_FLAVOR = "dev"

        @JvmStatic
        fun newInstance() =
            WelcomeFragment()
    }
}
