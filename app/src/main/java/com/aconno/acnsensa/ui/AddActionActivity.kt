package com.aconno.acnsensa.ui

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.R
import com.aconno.acnsensa.dagger.addaction.AddActionComponent
import com.aconno.acnsensa.dagger.addaction.AddActionModule
import com.aconno.acnsensa.dagger.addaction.DaggerAddActionComponent
import com.aconno.acnsensa.viewmodel.ActionViewModel
import kotlinx.android.synthetic.main.activity_add_action.*
import javax.inject.Inject


class AddActionActivity : AppCompatActivity() {

    @Inject
    lateinit var actionViewModel: ActionViewModel

    private val addActionComponent: AddActionComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? = application as? AcnSensaApplication
        DaggerAddActionComponent.builder()
            .appComponent(acnSensaApplication?.appComponent)
            .addActionModule(AddActionModule(this)).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_action)
        addActionComponent.inject(this)

        initSpinner(sensor_spinner, actionViewModel.getSensorTypes())
        initSpinner(condition_type_spinner, actionViewModel.getConditionTypes())

        add_action_button.setOnClickListener { this.addAction() }
    }

    override fun onResume() {
        super.onResume()
        actionViewModel.addActionResults.observe(this, Observer { onAddActionResult(it) })
    }

    private fun initSpinner(spinner: Spinner, contents: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, contents)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun addAction() {
        val name = action_name.text.toString()
        val sensorType = sensor_spinner.selectedItemPosition
        val conditionType = condition_type_spinner.selectedItem.toString()
        val value = condition_value.text.toString()
        val outcome = outcome_notification_text.text.toString()

        actionViewModel.addAction(name, sensorType, conditionType, value, outcome)
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

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AddActionActivity::class.java)
            context.startActivity(intent)
        }
    }
}