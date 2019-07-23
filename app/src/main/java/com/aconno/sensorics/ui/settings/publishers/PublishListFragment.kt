package com.aconno.sensorics.ui.settings.publishers

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.aconno.sensorics.R
import com.aconno.sensorics.adapter.LongItemClickListener
import com.aconno.sensorics.model.BasePublishModel
import com.aconno.sensorics.model.GooglePublishModel
import com.aconno.sensorics.model.MqttPublishModel
import com.aconno.sensorics.model.RestPublishModel
import com.aconno.sensorics.ui.SwipeToDeleteCallback
import com.aconno.sensorics.ui.base.BaseFragment
import com.aconno.sensorics.ui.settings.publishers.selectpublish.SelectPublisherActivity
import com.aconno.sensorics.viewmodel.PublishListViewModel
import com.google.android.material.snackbar.Snackbar
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
    private var snackbar: Snackbar? = null

    @Inject
    lateinit var publishListViewModel: PublishListViewModel

    private lateinit var publishAdapter: PublishRecyclerViewAdapter

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

            addDisposable(publishListViewModel.update(item))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_publish_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        publishAdapter = PublishRecyclerViewAdapter(
            listBasePublish,
            listener,
            this
        )

        view_publish_list.layoutManager = LinearLayoutManager(context)
        view_publish_list.adapter = publishAdapter

        view_publish_list.itemAnimator = DefaultItemAnimator()
        view_publish_list.addItemDecoration(
            DividerItemDecoration(
                context,
                (view_publish_list.layoutManager as LinearLayoutManager).orientation
            )
        )

        context?.let { context ->
            ItemTouchHelper(object : SwipeToDeleteCallback(context) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val publishModel = publishAdapter.getPublishModel(position)
                    publishAdapter.removePublishModel(position)

                    snackbar = Snackbar.make(
                        container_fragment,
                        "${publishModel.name} removed!",
                        Snackbar.LENGTH_LONG
                    ).also {
                        it.setAction(getString(R.string.undo)) {
                            publishAdapter.addPublishModelAtPosition(publishModel, position)
                        }

                        it.addCallback(object: Snackbar.Callback() {
                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                if (event == DISMISS_EVENT_TIMEOUT
                                    || event == DISMISS_EVENT_CONSECUTIVE
                                    || event == DISMISS_EVENT_SWIPE
                                    || event == DISMISS_EVENT_MANUAL
                                ) {
                                    selectedItem = publishModel
                                    deleteSelectedItem()
                                }
                            }
                        })

                        it.setActionTextColor(Color.YELLOW)

                        it.show()
                    }
                }
            }).attachToRecyclerView(view_publish_list)
        }

        button_add_publisher.setOnClickListener {
            SelectPublisherActivity.start(context!!)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
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
        publishAdapter.setOnCheckedChangeListener(null)
        listBasePublish.clear()
        publishAdapter.notifyDataSetChanged()
        super.onPause()
    }

    private fun initPublishList(actions: List<BasePublishModel>) {
        empty_view.visibility = View.GONE
        listBasePublish.addAll(actions)
        publishAdapter.notifyDataSetChanged()
        publishAdapter.setOnCheckedChangeListener(checkedChangeListener)
    }

    private fun deleteSelectedItem() {
        selectedItem?.let {
            when (selectedItem) {
                is GooglePublishModel -> {
                    addDisposable(
                        publishListViewModel.delete(selectedItem as GooglePublishModel)
                    )
                }
                is RestPublishModel -> {
                    addDisposable(
                        publishListViewModel.delete(selectedItem as RestPublishModel)
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
            publishAdapter.notifyItemRemoved(index)

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
