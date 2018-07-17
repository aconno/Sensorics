package com.aconno.sensorics.ui.actions

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.CheckedTextView
import com.aconno.sensorics.R
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.adapter.DeviceSpinnerAdapter
import com.aconno.sensorics.dagger.action_details.ActionDetailsComponent
import com.aconno.sensorics.dagger.action_details.ActionDetailsModule
import com.aconno.sensorics.dagger.action_details.DaggerActionDetailsComponent
import kotlinx.android.synthetic.main.activity_action_details.*
import kotlinx.android.synthetic.main.item_chip.view.*
import timber.log.Timber
import javax.inject.Inject

class ActionDetailsActivity : AppCompatActivity(), ConditionDialogListener {

    @Inject
    lateinit var actionDetailsViewModel: ActionDetailsViewModel

    private val deviceSpinnerAdapter = DeviceSpinnerAdapter()

    private val actionDetailsComponent: ActionDetailsComponent by lazy {
        val sensoricsApplication = application as SensoricsApplication
        DaggerActionDetailsComponent.builder()
            .appComponent(sensoricsApplication.appComponent)
            .actionDetailsModule(ActionDetailsModule(this))
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action_details)
        actionDetailsComponent.inject(this)

        initDevicesSpinner()

        actionDetailsViewModel.getDevicesLiveData().observe(this, Observer {
            it?.let { deviceSpinnerAdapter.setDevices(it) }
        })

        actionDetailsViewModel.getReadingTypesLiveData().observe(this, Observer {
            initConditionViews(it)
        })

        actionDetailsViewModel.getConditionLiveData().observe(this, Observer { condition ->
            condition?.let {
                val readingTypes = actionDetailsViewModel.getReadingTypesLiveData().value
                readingTypes?.let {
                    val position = readingTypes.indexOf(condition.readingType)
                    val view = container_conditions.getChildAt(position)
                    (view as CheckedTextView).isChecked = true
                    view.text_title.text = condition.toString()
                    initOutcomeViews()
                }
            }
        })

        setSaveButtonOnClickListener()
        setOutcomeButtonOnClickListeners()
        observeOutcomeLiveData()
    }

    private fun initDevicesSpinner() {
        spinner_devices.adapter = deviceSpinnerAdapter
        spinner_devices.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Timber.d("Nothing selected")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val device = deviceSpinnerAdapter.getDevice(position)
                actionDetailsViewModel.setSelectedDevice(device)
            }
        }
    }

    override fun onSetClicked(readingType: String, constraint: String, value: String) {
        val readingTypes = actionDetailsViewModel.getReadingTypesLiveData().value
        initConditionViews(readingTypes)
        actionDetailsViewModel.setCondition(readingType, constraint, value)
    }

    private fun initConditionViews(readingTypes: List<String>?) {
        if (readingTypes == null) {
            label_condition.visibility = View.GONE
            container_conditions.visibility = View.GONE
        } else {
            label_condition.visibility = View.VISIBLE
            container_conditions.visibility = View.VISIBLE
            readingTypes.forEachIndexed { index, readingType ->
                val view = container_conditions.getChildAt(index)
                if (view == null) {
                    val newView = LayoutInflater.from(this)
                        .inflate(R.layout.item_chip, container_conditions, false)
                    newView.text_title.text = readingType
                    newView.setOnClickListener {
                        val dialog = ConditionDialog.newInstance(readingType)
                        dialog.show(supportFragmentManager, "condition_dialog_fragment")
                    }
                    container_conditions.addView(newView)
                } else {
                    view.text_title.text = readingType
                    (view as CheckedTextView).isChecked = false
                    view.visibility = View.VISIBLE
                    view.setOnClickListener {
                        val dialog = ConditionDialog.newInstance(readingType)
                        dialog.show(supportFragmentManager, "condition_dialog_fragment")
                    }
                }
            }
            if (container_conditions.childCount > readingTypes.size) {
                for (index in readingTypes.size until container_conditions.childCount) {
                    container_conditions.getChildAt(index).visibility = View.GONE
                }
            }
        }
        label_outcome.visibility = View.GONE
        container_outcomes.visibility = View.GONE
        edittext_message.visibility = View.GONE
        edittext_phone_number.visibility = View.GONE
        button_save.visibility = View.GONE
    }

    private fun initOutcomeViews() {
        label_outcome.visibility = View.VISIBLE
        container_outcomes.visibility = View.VISIBLE
        button_outcome_notification.isChecked = false
        button_outcome_sms.isChecked = false
        button_outcome_text_to_speech.isChecked = false
        button_outcome_vibration.isChecked = false
        edittext_message.visibility = View.GONE
        edittext_phone_number.visibility = View.GONE
        button_save.visibility = View.GONE
    }

    private fun setSaveButtonOnClickListener() {
        button_save.setOnClickListener {
            if (edittext_name.text.isBlank()) {
                Snackbar.make(
                    container_activity,
                    "Action name cannot be blank",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                //TODO: Make a call to view model to save the action with edittext parameters
            }
        }
    }

    private fun setOutcomeButtonOnClickListeners() {
        button_outcome_notification.setOnClickListener {
            actionDetailsViewModel.setOutcome("Notification")
        }
        button_outcome_sms.setOnClickListener {
            actionDetailsViewModel.setOutcome("SMS")
        }
        button_outcome_text_to_speech.setOnClickListener {
            actionDetailsViewModel.setOutcome("Text to speech")
        }
        button_outcome_vibration.setOnClickListener {
            actionDetailsViewModel.setOutcome("Vibration")
        }
    }

    private fun observeOutcomeLiveData() {
        actionDetailsViewModel.getOutcomeLiveData().observe(this, Observer { outcome ->
            when (outcome) {
                "Notification" -> {
                    button_outcome_notification.isChecked = true
                    button_outcome_sms.isChecked = false
                    button_outcome_text_to_speech.isChecked = false
                    button_outcome_vibration.isChecked = false
                    edittext_phone_number.visibility = View.GONE
                    edittext_message.visibility = View.VISIBLE
                }
                "SMS" -> {
                    button_outcome_notification.isChecked = false
                    button_outcome_sms.isChecked = true
                    button_outcome_text_to_speech.isChecked = false
                    button_outcome_vibration.isChecked = false
                    edittext_phone_number.visibility = View.VISIBLE
                    edittext_message.visibility = View.VISIBLE
                }
                "Text to speech" -> {
                    button_outcome_notification.isChecked = false
                    button_outcome_sms.isChecked = false
                    button_outcome_text_to_speech.isChecked = true
                    button_outcome_vibration.isChecked = false
                    edittext_phone_number.visibility = View.GONE
                    edittext_message.visibility = View.VISIBLE
                }
                "Vibration" -> {
                    button_outcome_notification.isChecked = false
                    button_outcome_sms.isChecked = false
                    button_outcome_text_to_speech.isChecked = false
                    button_outcome_vibration.isChecked = true
                    edittext_phone_number.visibility = View.GONE
                    edittext_message.visibility = View.GONE
                }
                else -> throw IllegalArgumentException("Invalid outcome value: $outcome")
            }
            button_save.visibility = View.VISIBLE
        })
    }

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, ActionDetailsActivity::class.java)
            context.startActivity(intent)
        }
    }
}