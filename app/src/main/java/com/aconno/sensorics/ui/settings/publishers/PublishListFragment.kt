package com.aconno.sensorics.ui.settings.publishers

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.sensorics.R
import com.aconno.sensorics.adapter.LongItemClickListener
import com.aconno.sensorics.model.BasePublishModel
import com.aconno.sensorics.model.GooglePublishModel
import com.aconno.sensorics.model.MqttPublishModel
import com.aconno.sensorics.model.RESTPublishModel
import com.aconno.sensorics.ui.base.BaseFragment
import com.aconno.sensorics.ui.settings.publishers.selectpublish.SelectPublisherActivity
import com.aconno.sensorics.viewmodel.PublishListViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_publish_list.*
import javax.inject.Inject


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [PublishListFragment.OnListFragmentInteractionListener] interface.
 */
class PublishListFragment : BaseFragment(),
    LongItemClickListener<BasePublishModel> {

    @Inject
    lateinit var publishListViewModel: PublishListViewModel

    private lateinit var rvAdapter: PublishRecyclerViewAdapter

    private var listener: OnListFragmentInteractionListener? = null
    private var listBasePublish: MutableList<BasePublishModel> = mutableListOf()
    private var selectedItem: BasePublishModel? = null

    private var dialogClickListener: DialogInterface.OnClickListener =
        DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    deleteSelectedItem()
                    dialog.dismiss()
                }

                DialogInterface.BUTTON_NEGATIVE -> {
                    dialog.dismiss()
                }
            }
        }

    private val checkedChangeListener: PublishRecyclerViewAdapter.OnCheckedChangeListener = object :
        PublishRecyclerViewAdapter.OnCheckedChangeListener {
        override fun onCheckedChange(checked: Boolean, position: Int) {
            val item = listBasePublish[position]
            item.enabled = checked

            when (item) {
                is GooglePublishModel -> addDisposable(publishListViewModel.update(item))
                is RESTPublishModel -> addDisposable(publishListViewModel.update(item))
                is MqttPublishModel -> addDisposable(publishListViewModel.update(item))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_publish_list, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.view_publish_list)
        val fab = view.findViewById<FloatingActionButton>(R.id.button_add_publisher)
        rvAdapter = PublishRecyclerViewAdapter(
            listBasePublish,
            listener,
            this
        )
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter

            val dividerItemDecoration = DividerItemDecoration(
                recyclerView.context,
                (layoutManager as LinearLayoutManager).orientation
            )
            this.addItemDecoration(dividerItemDecoration)
        }

        fab.setOnClickListener {
            SelectPublisherActivity.start(context!!)
        }

        return view
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
        val subscribe = publishListViewModel.getAllPublish()
            .filter { !it.isEmpty() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { actions -> initPublishList(actions) }

        addDisposable(subscribe)
    }

    override fun onPause() {
        rvAdapter.setOnCheckedChangeListener(null)
        listBasePublish.clear()
        rvAdapter.notifyDataSetChanged()
        super.onPause()
    }

    private fun initPublishList(actions: List<BasePublishModel>) {
        empty_view.visibility = View.GONE
        listBasePublish.addAll(actions)
        rvAdapter.notifyDataSetChanged()
        rvAdapter.setOnCheckedChangeListener(checkedChangeListener)
    }

    private fun deleteSelectedItem() {
        selectedItem?.let {
            when (selectedItem) {
                is GooglePublishModel -> {
                    addDisposable(
                        publishListViewModel.delete(selectedItem as GooglePublishModel)
                    )
                }
                is RESTPublishModel -> {
                    addDisposable(
                        publishListViewModel.delete(selectedItem as RESTPublishModel)
                    )
                }
                is MqttPublishModel -> {
                    addDisposable(
                        publishListViewModel.delete(selectedItem as MqttPublishModel)
                    )
                }
                else -> throw IllegalArgumentException("Illegal argument provided.")
            }

            val index = listBasePublish.indexOf(selectedItem!!)
            listBasePublish.remove(selectedItem!!)
            rvAdapter.notifyItemRemoved(index)

            if (listBasePublish.isEmpty()) {
                empty_view.visibility = View.VISIBLE
            }
            //Let GC collect removed instance
            selectedItem = null
        }
    }

    override fun onLongClick(param: BasePublishModel) {
        selectedItem = param
        val builder = AlertDialog.Builder(context)

        builder.setMessage(getString(R.string.are_you_sure))
            .setPositiveButton(getString(R.string.yes), dialogClickListener)
            .setNegativeButton(getString(R.string.no), dialogClickListener)
            .show()
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
        fun onListFragmentInteraction(item: BasePublishModel?)
    }

    companion object {
        @JvmStatic
        fun newInstance() = PublishListFragment()
    }
}
