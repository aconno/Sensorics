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
import com.aconno.sensorics.domain.interactor.ifttt.action.DeleteActionUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.GetAllActionsUseCase
import com.aconno.sensorics.ui.actions.ActionDetailsActivity
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_action_list.*
import javax.inject.Inject

/**
 * @author aconno
 */
class ActionListFragment : DaggerFragment(), ItemClickListener<Action> {

    private lateinit var actionAdapter: ActionAdapter
    private var snackbar: Snackbar? = null

    @Inject
    lateinit var getAllActionsUseCase: GetAllActionsUseCase

    @Inject
    lateinit var deleteActionUseCase: DeleteActionUseCase

    private val disposables = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_action_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionAdapter = ActionAdapter(mutableListOf(), this)
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
                            if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT
                                || event == Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE
                                || event == Snackbar.Callback.DISMISS_EVENT_SWIPE
                                || event == Snackbar.Callback.DISMISS_EVENT_MANUAL
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

    private fun startAddActionActivity() {
        context?.let { ActionDetailsActivity.start(it) }
    }

    override fun onResume() {
        super.onResume()

        disposables.add(
            getAllActionsUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { actions -> initActionList(actions) }
        )

    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
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
