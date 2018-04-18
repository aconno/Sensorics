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
import android.widget.Toast
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.R
import com.aconno.acnsensa.dagger.actionlist.ActionListModule
import com.aconno.acnsensa.dagger.actionlist.DaggerActionListComponent
import com.aconno.acnsensa.dagger.addaction.AddActionComponent
import com.aconno.acnsensa.dagger.addaction.AddActionModule
import com.aconno.acnsensa.dagger.addaction.DaggerAddActionComponent
import com.aconno.acnsensa.domain.model.SensorTypeSingle
import com.aconno.acnsensa.model.toStringResource
import com.aconno.acnsensa.ui.actions.ConditionDialog
import com.aconno.acnsensa.ui.actions.ConditionDialogListener
import com.aconno.acnsensa.viewmodel.ActionOptionsViewModel
import com.aconno.acnsensa.viewmodel.NewActionViewModel
import kotlinx.android.synthetic.main.activity_add_action.*
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
        setContentView(R.layout.activity_add_action)
        addActionComponent.inject(this)

        initConditionChips()
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

    private fun initConditionChips() {
        initConditionChipTitles()
        setConditionChipOnClickListeners()
    }

    private fun initConditionChipTitles() {
        temperature.text = SensorTypeSingle.TEMPERATURE.toStringResource(this)
        light.text = SensorTypeSingle.LIGHT.toStringResource(this)
        humidity.text = SensorTypeSingle.HUMIDITY.toStringResource(this)
        pressure.text = SensorTypeSingle.PRESSURE.toStringResource(this)
        magnetometer_x.text = SensorTypeSingle.MAGNETOMETER_X.toStringResource(this)
        magnetometer_y.text = SensorTypeSingle.MAGNETOMETER_Y.toStringResource(this)
        magnetometer_z.text = SensorTypeSingle.MAGNETOMETER_Z.toStringResource(this)
        accelerometer_x.text = SensorTypeSingle.ACCELEROMETER_X.toStringResource(this)
        accelerometer_y.text = SensorTypeSingle.ACCELEROMETER_Y.toStringResource(this)
        accelerometer_z.text = SensorTypeSingle.ACCELEROMETER_Z.toStringResource(this)
        gyroscope_x.text = SensorTypeSingle.GYROSCOPE_X.toStringResource(this)
        gyroscope_y.text = SensorTypeSingle.GYROSCOPE_Y.toStringResource(this)
        gyroscope_z.text = SensorTypeSingle.GYROSCOPE_Z.toStringResource(this)
        battery_level.text = SensorTypeSingle.BATTERY_LEVEL.toStringResource(this)
    }

    private fun setConditionChipOnClickListeners() {
        temperature.setOnClickListener { openConditionDialog(SensorTypeSingle.TEMPERATURE) }
        light.setOnClickListener { openConditionDialog(SensorTypeSingle.LIGHT) }
        humidity.setOnClickListener { openConditionDialog(SensorTypeSingle.HUMIDITY) }
        pressure.setOnClickListener { openConditionDialog(SensorTypeSingle.PRESSURE) }
        magnetometer_x.setOnClickListener { openConditionDialog(SensorTypeSingle.MAGNETOMETER_X) }
        magnetometer_y.setOnClickListener { openConditionDialog(SensorTypeSingle.MAGNETOMETER_Y) }
        magnetometer_z.setOnClickListener { openConditionDialog(SensorTypeSingle.MAGNETOMETER_Z) }
        accelerometer_x.setOnClickListener { openConditionDialog(SensorTypeSingle.ACCELEROMETER_X) }
        accelerometer_y.setOnClickListener { openConditionDialog(SensorTypeSingle.ACCELEROMETER_Y) }
        accelerometer_z.setOnClickListener { openConditionDialog(SensorTypeSingle.ACCELEROMETER_Z) }
        gyroscope_x.setOnClickListener { openConditionDialog(SensorTypeSingle.GYROSCOPE_X) }
        gyroscope_y.setOnClickListener { openConditionDialog(SensorTypeSingle.GYROSCOPE_Y) }
        gyroscope_z.setOnClickListener { openConditionDialog(SensorTypeSingle.GYROSCOPE_Z) }
        battery_level.setOnClickListener { openConditionDialog(SensorTypeSingle.BATTERY_LEVEL) }
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
//        val name = action_name.text.toString()
//        val sensorType = sensor_spinner.selectedItemPosition
//        val conditionType = condition_type_spinner.selectedItem.toString()
//        val value = condition_value.text.toString()
//        val outcome = outcome_type_spinner.selectedItem.toString()
//        val smsDestination = phone_number.text.toString()
//        val content = message.text.toString()
//
//        newActionViewModel.addAction(
//            name,
//            sensorType,
//            conditionType,
//            value,
//            outcome,
//            smsDestination,
//            content
//        )
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
        }
    }

    override fun onSetClicked(sensorType: SensorTypeSingle, condition: String, value: String) {
        //TODO("not implemented")
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AddActionActivity::class.java)
            context.startActivity(intent)
        }
    }
}