package com.aconno.sensorics.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.aconno.sensorics.R
import com.aconno.sensorics.adapter.ActionAdapter
import com.aconno.sensorics.adapter.ItemClickListener
import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.domain.interactor.ifttt.action.AddActionUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.DeleteActionUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.GetAllActionsUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertJsonToActionsUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertJsonToObjectsUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertObjectsToJsonUseCase
import com.aconno.sensorics.ui.actions.ActionDetailsActivity
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_action_list.*
import kotlinx.android.synthetic.main.fragment_action_list.container_fragment
import timber.log.Timber
import javax.inject.Inject

/**
 * @author aconno
 */


class ActionListFragment : ShareableItemsListFragment<Action>(), ItemClickListener<Action>, ActionAdapter.OnListItemLongClickListener {

    private lateinit var actionAdapter: ActionAdapter
    private var snackbar: Snackbar? = null
    override val sharedFileNamePrefix = "actions"

    @Inject
    lateinit var getAllActionsUseCase: GetAllActionsUseCase

    @Inject
    lateinit var deleteActionUseCase: DeleteActionUseCase

    @Inject
    lateinit var addActionUseCase: AddActionUseCase

    @Inject
    lateinit var convertActionsToJsonUseCase: ConvertObjectsToJsonUseCase<Action>

    @Inject
    lateinit var convertJsonToActionsUseCase: ConvertJsonToActionsUseCase


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_action_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionAdapter = ActionAdapter(mutableListOf(), this,this)
        action_list.adapter = actionAdapter

        action_list.itemAnimator = DefaultItemAnimator()
        action_list.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        context?.let { context ->
            val swipeToDeleteCallback = object : SwipeToDeleteCallback(context) {

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val action = actionAdapter.getAction(position)
                    actionAdapter.removeAction(position)

                    snackbar = Snackbar
                        .make(container_fragment, "${action.name} removed!", Snackbar.LENGTH_LONG)
                    snackbar?.setAction("UNDO") {
                        actionAdapter.addActionAtPosition(action, position)
                    }

                    snackbar?.addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if (event == DISMISS_EVENT_TIMEOUT
                                || event == DISMISS_EVENT_CONSECUTIVE
                                || event == DISMISS_EVENT_SWIPE
                                || event == DISMISS_EVENT_MANUAL
                            ) {
                                deleteActionUseCase.execute(action)
                                    .subscribeOn(Schedulers.io())
                                    .subscribe()
                            }
                        }
                    })
                    snackbar?.setActionTextColor(Color.YELLOW)
                    snackbar?.show()
                }
            }
            ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(action_list)
        }

        add_action_button.setOnClickListener {
            snackbar?.dismiss()
            startAddActionActivity()
        }
    }

    override fun onListItemLongClick(item: Action) {
        showExportOptionsDialog(item)
    }

    override fun getFileShareSubject(): String {
        return getString(R.string.actions_file_share_subject)
    }

    override fun getConvertFromJsonUseCase(): ConvertJsonToObjectsUseCase<Action> {
        return convertJsonToActionsUseCase
    }

    override fun getConvertToJsonUseCase(): ConvertObjectsToJsonUseCase<Action> {
        return convertActionsToJsonUseCase
    }

    override fun getItems(): List<Action> {
        return actionAdapter.getActions()
    }

    override fun onItemsImportedFromFile(items: List<Action>) {
        addActions(items)
    }

    private fun addActions(actions: List<Action>) {
        Flowable.fromIterable(actions)
                .observeOn(Schedulers.io())
                .flatMapSingle {
                    action ->
                        addActionUseCase.execute(action)
                                .map {
                                    action.id = it
                                    action
                                }
                                .observeOn(Schedulers.io())
                    }
                .observeOn(AndroidSchedulers.mainThread())
                .toList()
                .subscribe ({
                        addActionsToActionAdapter(it)
                }, {
                    Timber.d(it)
                    Snackbar.make(container_fragment,
                            getString(R.string.import_error),
                            Snackbar.LENGTH_SHORT).show()
                })
                .also { addDisposable(it) }
    }

    private fun addActionsToActionAdapter(actions: List<Action>) {
        if(actionAdapter.getActions().isEmpty()) {
            initActionList(actions)
        } else {
            actionAdapter.appendActions(actions)
        }
    }

    private fun startAddActionActivity() {
        context?.let { ActionDetailsActivity.start(it) }
    }

    override fun onResume() {
        super.onResume()

        addDisposable(
            getAllActionsUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { actions -> initActionList(actions) }
        )

    }


    private fun initActionList(actions: List<Action>) {
        actionAdapter.setActions(actions)

        if (actions.isEmpty()) {
            action_list_empty_view.visibility = View.VISIBLE
        } else {
            action_list_empty_view.visibility = View.INVISIBLE
        }
    }

    override fun onItemClick(item: Action) {
        snackbar?.dismiss()
        context?.let { ActionDetailsActivity.start(it, item.id) }
    }



    companion object {

        fun newInstance(): ActionListFragment {
            return ActionListFragment()
        }
    }
}
