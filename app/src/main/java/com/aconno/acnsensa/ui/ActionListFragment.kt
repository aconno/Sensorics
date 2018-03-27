package com.aconno.acnsensa.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView.VERTICAL
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.R
import com.aconno.acnsensa.adapter.ActionAdapter
import com.aconno.acnsensa.dagger.actionlist.ActionListComponent
import com.aconno.acnsensa.dagger.actionlist.ActionListModule
import com.aconno.acnsensa.dagger.actionlist.DaggerActionListComponent
import com.aconno.acnsensa.domain.ifttt.Action
import com.aconno.acnsensa.domain.ifttt.GetAllActionsUseCase
import kotlinx.android.synthetic.main.fragment_action_list.*
import javax.inject.Inject

/**
 * @author aconno
 */
class ActionListFragment : Fragment() {

    @Inject
    lateinit var getAllActionsUseCase: GetAllActionsUseCase

    private val actionListComponent: ActionListComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? =
            context?.applicationContext as? AcnSensaApplication

        DaggerActionListComponent.builder()
            .appComponent(acnSensaApplication?.appComponent)
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
        add_action_button.setOnClickListener { startAddActionActivity() }
    }

    override fun onResume() {
        super.onResume()
        getAllActionsUseCase.execute().subscribe { actions -> initializeActionList(actions) }
    }

    private fun initializeActionList(actions: List<Action>) {
        actions_list.layoutManager = LinearLayoutManager(activity)
        val decoration = DividerItemDecoration(activity?.applicationContext, VERTICAL)
        actions_list.addItemDecoration(decoration)
        actions_list.adapter = ActionAdapter(actions.toMutableList())
    }

    private fun startAddActionActivity() {
        context?.let { AddActionActivity.start(it) }
    }

    companion object {
        fun newInstance(): ActionListFragment {
            return ActionListFragment()
        }
    }
}
