package com.aconno.acnsensa.ui.settings

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.R
import com.aconno.acnsensa.dagger.publish.DaggerPublishListComponent
import com.aconno.acnsensa.dagger.publish.PublishListComponent
import com.aconno.acnsensa.dagger.publish.PublishListModule
import com.aconno.acnsensa.domain.ifttt.BasePublish
import com.aconno.acnsensa.domain.ifttt.GooglePublish
import com.aconno.acnsensa.domain.ifttt.RESTPublish
import com.aconno.acnsensa.viewmodel.PublishListViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [PublishFragment.OnListFragmentInteractionListener] interface.
 */
class PublishFragment : Fragment() {

    @Inject
    lateinit var publishListViewModel: PublishListViewModel

    //TODO Need to think about these listeners
    private var listener: OnListFragmentInteractionListener? = null
    private var listBasePublish: MutableList<BasePublish> = mutableListOf()
    private lateinit var rvAdapter: PublishRecyclerViewAdapter

    private val publishListComponent: PublishListComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? =
            context?.applicationContext as? AcnSensaApplication

        DaggerPublishListComponent.builder()
            .appComponent(acnSensaApplication?.appComponent)
            .publishListModule(PublishListModule(this))
            .build()
    }

    private val checkedChangeListener: PublishRecyclerViewAdapter.OnCheckedChangeListener = object :
        PublishRecyclerViewAdapter.OnCheckedChangeListener {
        override fun onCheckedChange(checked: Boolean, position: Int) {
            val item = listBasePublish[position]
            item.enabled = checked

            if (item is GooglePublish) {
                publishListViewModel.updateGoogle(
                    item.id,
                    item.name,
                    item.projectId,
                    item.region,
                    item.deviceRegistry,
                    item.device,
                    item.privateKey,
                    item.enabled,
                    item.timeType,
                    item.timeMillis,
                    item.lastTimeMillis
                )
            } else if (item is RESTPublish) {
                publishListViewModel.updateREST(
                    item.id,
                    item.name,
                    item.url,
                    item.method,
                    item.enabled,
                    item.timeType,
                    item.timeMillis,
                    item.lastTimeMillis
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_publish_list, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.view_publish_list)
        rvAdapter = PublishRecyclerViewAdapter(
            listBasePublish,
            listener,
            checkedChangeListener
        )
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        publishListComponent.inject(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onResume() {
        super.onResume()
        publishListViewModel.getAllPublish()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { actions -> initPublishList(actions) }
    }

    override fun onPause() {
        listBasePublish.clear()
        rvAdapter.notifyDataSetChanged()
        super.onPause()
    }

    private fun initPublishList(actions: List<BasePublish>?) {
        if (actions != null) {
            listBasePublish.addAll(actions)
            rvAdapter.notifyDataSetChanged()
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: BasePublish?)
    }

    companion object {
        @JvmStatic
        fun newInstance() = PublishFragment()
    }
}
