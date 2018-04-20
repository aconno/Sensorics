package com.aconno.acnsensa.ui

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.R
import com.aconno.acnsensa.dagger.actionlist.ActionListModule
import com.aconno.acnsensa.dagger.actionlist.DaggerActionListComponent
import com.aconno.acnsensa.dagger.addaction.AddActionComponent
import com.aconno.acnsensa.dagger.addaction.AddActionModule
import com.aconno.acnsensa.dagger.addaction.DaggerAddActionComponent
import com.aconno.acnsensa.domain.ifttt.Condition
import com.aconno.acnsensa.domain.ifttt.LimitCondition
import com.aconno.acnsensa.domain.model.SensorTypeSingle
import com.aconno.acnsensa.model.toSensorType
import com.aconno.acnsensa.model.toStringResource
import com.aconno.acnsensa.ui.actions.ConditionDialog
import com.aconno.acnsensa.ui.actions.ConditionDialogListener
import com.aconno.acnsensa.viewmodel.ActionOptionsViewModel
import com.aconno.acnsensa.viewmodel.NewActionViewModel
import kotlinx.android.synthetic.main.activity_action_add.*
import timber.log.Timber
import javax.inject.Inject


class AddActionActivity : AppCompatActivity(), ConditionDialogListener {

    @Inject
    lateinit var newActionViewModel: NewActionViewModel

    @Inject
    lateinit var actionOptionsViewModel: ActionOptionsViewModel

    private val addActionComponent: AddActionComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? = application as? AcnSensaApplication

        val actionListComponent =
            DaggerActionListComponent.builder().appComponent(acnSensaApplication?.appComponent)
                .actionListModule(
                    ActionListModule(this)
                ).build()

        DaggerAddActionComponent.builder().actionListComponent(actionListComponent)
            .addActionModule(AddActionModule(this)).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action_add)
        addActionComponent.inject(this)

        setConditionChipOnClickListeners()
        initConditionViews()

        initSpinner(outcome_type_spinner, actionOptionsViewModel.getOuputTypes())

        add_action_button.setOnClickListener { this.addAction() }

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
    }

    private fun setConditionChipOnClickListeners() {
        SensorTypeSingle.values().forEach { sensorType ->
            getConditionView(sensorType).setOnClickListener {
                openConditionDialog(sensorType)
            }
        }
    }

    private fun openConditionDialog(sensorType: SensorTypeSingle) {
        val dialog = ConditionDialog.newInstance(sensorType)
        dialog.show(supportFragmentManager, "condition_dialog_fragment")
    }

    override fun onResume() {
        super.onResume()
        newActionViewModel.addActionResults.observe(this, Observer { onAddActionResult(it) })
    }

    private fun initSpinner(spinner: Spinner, contents: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, contents)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun addAction() {
        val name = action_name.text.toString()
        val outcome = outcome_type_spinner.selectedItem.toString()
        val smsDestination = phone_number.text.toString()
        val content = message.text.toString()

        newActionViewModel.addAction(
            name,
            outcome,
            smsDestination,
            content
        )
    }

    private fun onAddActionResult(success: Boolean?) {
        when (success) {
            true -> finish()
            else -> Toast.makeText(
                this,
                "Failed to makeServiceNotificationChannel Action",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    //TODO: Duplicate code with UpdateActionActivity.
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

    override fun onSetClicked(sensorType: SensorTypeSingle, constraint: String, value: String) {
        initConditionViews()
        //TODO: Prevent passing an empty string value
        newActionViewModel.setCondition(sensorType, constraint, value)
        val condition = newActionViewModel.getCondition()
        condition?.let {
            setSelectedCondition(condition)
        }
    }


    private fun initConditionViews() {
        initConditionTexts()
        initConditionStates()
    }

    private fun initConditionTexts() {
        SensorTypeSingle.values().forEach {
            getConditionView(it).text = it.toStringResource(this)
        }
    }

    private fun initConditionStates() {
        SensorTypeSingle.values().forEach {
            getConditionView(it).isChecked = false
        }
    }

    private fun setSelectedCondition(condition: Condition) {
        val conditionView = getConditionView(condition.sensorType.toSensorType())
        conditionView.isChecked = true
        appendConditionString(conditionView, condition)
    }

    private fun appendConditionString(textView: TextView, condition: Condition) {
        val constraint = when (condition.type) {
            LimitCondition.LOWER_LIMIT -> "<"
            LimitCondition.UPPER_LIMIT -> ">"
            else -> throw IllegalArgumentException("Invalid constraint identifier ${condition.type}")
        }
        textView.text = "${textView.text} $constraint ${condition.limit}"
    }

    private fun getConditionView(sensorType: SensorTypeSingle): CheckedTextView {
        return when (sensorType) {
            SensorTypeSingle.TEMPERATURE -> temperature
            SensorTypeSingle.LIGHT -> light
            SensorTypeSingle.HUMIDITY -> humidity
            SensorTypeSingle.PRESSURE -> pressure
            SensorTypeSingle.MAGNETOMETER_X -> magnetometer_x
            SensorTypeSingle.MAGNETOMETER_Y -> magnetometer_y
            SensorTypeSingle.MAGNETOMETER_Z -> magnetometer_z
            SensorTypeSingle.ACCELEROMETER_X -> accelerometer_x
            SensorTypeSingle.ACCELEROMETER_Y -> accelerometer_y
            SensorTypeSingle.ACCELEROMETER_Z -> accelerometer_z
            SensorTypeSingle.GYROSCOPE_X -> gyroscope_x
            SensorTypeSingle.GYROSCOPE_Y -> gyroscope_y
            SensorTypeSingle.GYROSCOPE_Z -> gyroscope_z
            SensorTypeSingle.BATTERY_LEVEL -> battery_level
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AddActionActivity::class.java)
            context.startActivity(intent)
        }
    }
}