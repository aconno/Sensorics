package com.aconno.sensorics.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.R
import com.aconno.sensorics.adapter.ActionAdapter
import com.aconno.sensorics.adapter.ItemClickListener
import com.aconno.sensorics.dagger.actionlist.ActionListComponent
import com.aconno.sensorics.dagger.actionlist.ActionListModule
import com.aconno.sensorics.dagger.actionlist.DaggerActionListComponent
import com.aconno.sensorics.domain.ifttt.Action
import com.aconno.sensorics.domain.interactor.ifttt.action.GetAllActionsUseCase
import com.aconno.sensorics.ui.actions.ActionDetailsActivity
import com.aconno.sensorics.ui.actions.AddActionActivity
import com.aconno.sensorics.ui.actions.EditActionActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_action_list.*
import javax.inject.Inject

/**
 * @author aconno
 */
class ActionListFragment : Fragment(), ItemClickListener<Action> {

    private lateinit var actionAdapter: ActionAdapter

    @Inject
    lateinit var getAllActionsUseCase: GetAllActionsUseCase

    private val actionListComponent: ActionListComponent by lazy {
        val sensoricsApplication: SensoricsApplication? =
            context?.applicationContext as? SensoricsApplication

        DaggerActionListComponent.builder()
            .appComponent(sensoricsApplication?.appComponent)
            .actionListModule(ActionListModule())
            .build()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_action_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionListComponent.inject(this)
        actionAdapter = ActionAdapter(mutableListOf(), this)
        action_list.adapter = actionAdapter
        add_action_button.setOnClickListener { startAddActionActivity() }
    }

    private fun startAddActionActivity() {
        context?.let { ActionDetailsActivity.start(it) }
    }

    override fun onResume() {
        super.onResume()
        getAllActionsUseCase.execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { actions -> initActionList(actions) }
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
        context?.let { EditActionActivity.start(it, item.id) }
    }

    companion object {

        fun newInstance(): ActionListFragment {
            return ActionListFragment()
        }
    }
}
