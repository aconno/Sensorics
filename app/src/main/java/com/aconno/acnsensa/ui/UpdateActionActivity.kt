package com.aconno.acnsensa.ui

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.R
import com.aconno.acnsensa.dagger.actionlist.ActionListModule
import com.aconno.acnsensa.dagger.actionlist.DaggerActionListComponent
import com.aconno.acnsensa.dagger.updateaction.DaggerUpdateActionComponent
import com.aconno.acnsensa.dagger.updateaction.UpdateActionComponent
import com.aconno.acnsensa.dagger.updateaction.UpdateActionModule
import com.aconno.acnsensa.domain.ifttt.Action
import com.aconno.acnsensa.domain.ifttt.outcome.Outcome
import com.aconno.acnsensa.viewmodel.ActionOptionsViewModel
import com.aconno.acnsensa.viewmodel.ExistingActionViewModel
import kotlinx.android.synthetic.main.activity_update_action.*
import timber.log.Timber
import javax.inject.Inject

class UpdateActionActivity : AppCompatActivity() {

    @Inject
    lateinit var existingActionViewModel: ExistingActionViewModel

    @Inject
    lateinit var actionOptionsViewModel: ActionOptionsViewModel

    private val updateActionComponent: UpdateActionComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? = application as? AcnSensaApplication

        val actionListComponent =
            DaggerActionListComponent.builder().appComponent(acnSensaApplication?.appComponent)
                .actionListModule(
                    ActionListModule(this)
                ).build()

        DaggerUpdateActionComponent.builder()
            .actionListComponent(actionListComponent)
            .updateActionModule(UpdateActionModule(this)).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_action)
        updateActionComponent.inject(this)

        val actionId = intent.getLongExtra(EXTRA_ACTION_ID, -1)

        update_action_button.setOnClickListener { updateAction() }
        delete_action_button.setOnClickListener { deleteAction() }

        initSpinner(sensor_spinner, actionOptionsViewModel.getSensorTypes())
        initSpinner(condition_type_spinner, actionOptionsViewModel.getConditionTypes())
        initSpinner(outcome_type_spinner, actionOptionsViewModel.getOuputTypes())

        outcome_type_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Timber.e("Nothing selected.")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                showOutcomeOptions()
            }
        }

        existingActionViewModel.action.observe(this, Observer { updateFields(it) })
        existingActionViewModel.getActionById(actionId)
    }

    //TODO: InitSpinner is duplicate code from AddActionActivity
    private fun initSpinner(spinner: Spinner, contents: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, contents)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun updateAction() {
        val name = action_name.text.toString()
        val sensorType = sensor_spinner.selectedItemPosition
        val conditionType = condition_type_spinner.selectedItem.toString()
        val value = condition_value.text.toString()
        val outcome = outcome_type_spinner.selectedItem.toString()
        val smsDestination = phone_number.text.toString()
        val content = message.text.toString()

        existingActionViewModel.updateAction(
            name,
            sensorType,
            conditionType,
            value,
            outcome,
            smsDestination,
            content
        )
        finish()
    }

    private fun deleteAction() {
        existingActionViewModel.deleteAction()
        finish()
    }

    private fun updateFields(action: Action?) {
        action?.let {
            action_name.setText(action.name)
            sensor_spinner.setSelection(action.condition.sensorType)
            condition_type_spinner.setSelection(action.condition.type)
            condition_value.setText(action.condition.limit.toString())

            val outcome = action.outcome

            when (outcome.type) {
                Outcome.OUTCOME_TYPE_NOTIFICATION -> {
                    outcome_type_spinner.setSelection(0)
                    message.visibility = View.VISIBLE
                    message.setText(outcome.parameters[Outcome.TEXT_MESSAGE])
                }
                Outcome.OUTCOME_TYPE_SMS -> {
                    outcome_type_spinner.setSelection(1)
                    message.visibility = View.VISIBLE
                    message.setText(outcome.parameters[Outcome.TEXT_MESSAGE])
                    phone_number.visibility = View.VISIBLE
                    phone_number.setText(outcome.parameters[Outcome.PHONE_NUMBER])
                }
                Outcome.OUTCOME_TYPE_VIBRATION -> {
                    outcome_type_spinner.setSelection(2)
                }
                Outcome.OUTCOME_TYPE_TEXT_TO_SPEECH -> {
                    outcome_type_spinner.setSelection(3)
                    message.visibility = View.VISIBLE
                    message.setText(outcome.parameters[Outcome.TEXT_MESSAGE])
                }
            }
        }
    }

    private fun showOutcomeOptions() {
        val selected = outcome_type_spinner.selectedItemId
        when (selected) {
            0L -> {
                message.visibility = View.VISIBLE
                phone_number.visibility = View.GONE
            }
            1L -> {
                message.visibility = View.VISIBLE
                phone_number.visibility = View.VISIBLE
            }
            2L -> {
                message.visibility = View.GONE
                phone_number.visibility = View.GONE
            }
            3L -> {
                message.visibility = View.VISIBLE
                phone_number.visibility = View.GONE
            }
        }
    }

    companion object {
        private const val EXTRA_ACTION_ID = "com.aconno.acnsensa.EXTRA_ACTION_ID"

        fun start(context: Context, actionId: Long) {
            val intent = Intent(context, UpdateActionActivity::class.java)
            intent.putExtra(EXTRA_ACTION_ID, actionId)
            context.startActivity(intent)
        }
    }
}
