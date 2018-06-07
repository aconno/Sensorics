package com.aconno.acnsensa.ui.settings

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
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
import com.aconno.acnsensa.model.BasePublishModel
import com.aconno.acnsensa.model.GooglePublishModel
import com.aconno.acnsensa.model.RESTPublishModel
import com.aconno.acnsensa.ui.base.BaseFragment
import com.aconno.acnsensa.viewmodel.PublishListViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [PublishListFragment.OnListFragmentInteractionListener] interface.
 */
class PublishListFragment : BaseFragment(), PublishOnLongClickListener {

    @Inject
    lateinit var publishListViewModel: PublishListViewModel

    private lateinit var rvAdapter: PublishRecyclerViewAdapter

    private var listener: OnListFragmentInteractionListener? = null
    private var listBasePublish: MutableList<BasePublishModel> = mutableListOf()
    private var selectedItem: BasePublishModel? = null

    var dialogClickListener: DialogInterface.OnClickListener =
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

            if (item is GooglePublishModel) {
                addDisposable(publishListViewModel.update(item))
            } else if (item is RESTPublishModel) {
                addDisposable(publishListViewModel.update(item))
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
            checkedChangeListener,
            this
        )
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter

            val dividerItemDecoration = DividerItemDecoration(
                recyclerView.context,
                (layoutManager as LinearLayoutManager).getOrientation()
            )
            this.addItemDecoration(dividerItemDecoration)
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
        val subscribe = publishListViewModel.getAllPublish()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { actions -> initPublishList(actions) }

        addDisposable(subscribe)
    }


    override fun onPause() {
        listBasePublish.clear()
        rvAdapter.notifyDataSetChanged()
        super.onPause()
    }

    private fun initPublishList(actions: List<BasePublishModel>?) {
        if (actions != null) {
            listBasePublish.addAll(actions)
            rvAdapter.notifyDataSetChanged()
        }
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
                else -> throw IllegalArgumentException("Illegal argument provided.")
            }

            val index = listBasePublish.indexOf(selectedItem!!)
            listBasePublish.remove(selectedItem!!)
            rvAdapter.notifyItemRemoved(index)

            //Let GC collect removed instance
            selectedItem = null
        }
    }

    override fun onLongClick(basePublishModel: BasePublishModel) {
        selectedItem = basePublishModel
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
