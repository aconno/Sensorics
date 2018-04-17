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
import com.aconno.acnsensa.viewmodel.ActionOptionsViewModel
import com.aconno.acnsensa.viewmodel.NewActionViewModel
import kotlinx.android.synthetic.main.activity_add_action.*
import timber.log.Timber
import javax.inject.Inject


class AddActionActivity : AppCompatActivity() {

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
    }

    private fun initConditionChipTitles() {
        temperature.text = getString(R.string.temperature)
        light.text = getString(R.string.light)
        humidity.text = getString(R.string.humidity)
        pressure.text = getString(R.string.pressure)
        magnetometer_x.text = getString(R.string.magnetometer_x)
        magnetometer_y.text = getString(R.string.magnetometer_y)
        magnetometer_z.text = getString(R.string.magnetometer_z)
        accelerometer_x.text = getString(R.string.accelerometer_x)
        accelerometer_y.text = getString(R.string.accelerometer_y)
        accelerometer_z.text = getString(R.string.accelerometer_z)
        gyroscope_x.text = getString(R.string.gyro_x)
        gyroscope_y.text = getString(R.string.gyro_y)
        gyroscope_z.text = getString(R.string.gyro_z)
        battery_level.text = getString(R.string.battery_level)
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

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AddActionActivity::class.java)
            context.startActivity(intent)
        }
    }
}