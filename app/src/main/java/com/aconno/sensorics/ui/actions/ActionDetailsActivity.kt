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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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

        setSaveButtonOnClickListener()
        setOutcomeButtonOnClickListeners()

        observeNameLiveData()
        observeDevicesLiveData()
        observeSelectedDeviceLiveData()
        observeReadingTypesLiveData()
        observeConditionLiveData()
        observeOutcomeLiveData()
        observeMessageLiveData()
        observePhoneNumberLiveData()

        if (intent.hasExtra(ACTION_ID_EXTRA)) {
            val actionId = intent.getLongExtra(ACTION_ID_EXTRA, 0L)
            actionDetailsViewModel.setActionId(actionId)
        }
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
                hideConditions()
                val device = deviceSpinnerAdapter.getDevice(position)
                actionDetailsViewModel.setSelectedDevice(device)
            }
        }
    }

    private fun setSaveButtonOnClickListener() {
        button_save.setOnClickListener {
            if (edittext_name.text.isBlank()) {
                showSnackbarMessage("Action name cannot be blank")
            } else {
                actionDetailsViewModel.saveAction(
                    edittext_name.text.toString(),
                    edittext_message.text.toString(),
                    edittext_phone_number.text.toString()
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        finish()
                    }, {
                        showSnackbarMessage(it.message ?: "Save not succeeded")
                    })
            }
        }
    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(container_activity, message, Snackbar.LENGTH_SHORT).show()
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

    private fun observeNameLiveData() {
        actionDetailsViewModel.getNameLiveData().observe(this, Observer { name ->
            if (name == null) {
                edittext_name.text.clear()
                Timber.d("Action name: $name")
            } else {
                edittext_name.setText(name)
            }
        })
    }

    private fun observeDevicesLiveData() {
        actionDetailsViewModel.getDevicesLiveData().observe(this, Observer { devices ->
            if (devices == null || devices.isEmpty()) {
                hideDevicesSpinner()
                showSnackbarMessage("There are no devices")
                Timber.e(IllegalArgumentException("There are no devices"))
            } else {
                deviceSpinnerAdapter.setDevices(devices)
                showDevicesSpinner()
            }
        })
    }

    private fun observeSelectedDeviceLiveData() {
        actionDetailsViewModel.getSelectedDeviceLiveData().observe(this, Observer { device ->
            device?.let {
                val position = deviceSpinnerAdapter.getDevicePosition(device)
                spinner_devices.setSelection(position)
            }
        })
    }

    private fun observeReadingTypesLiveData() {
        actionDetailsViewModel.getReadingTypesLiveData().observe(this, Observer { readingTypes ->
            if (readingTypes == null || readingTypes.isEmpty()) {
                hideConditions()
                showSnackbarMessage("There are no reading types")
                Timber.d("No reading types: $readingTypes")
            } else {
                initConditionViews(readingTypes)
                showConditions()
            }
        })
    }

    private fun observeConditionLiveData() {
        actionDetailsViewModel.getConditionLiveData().observe(this, Observer { condition ->
            Timber.d("Condition: $condition")
            condition?.let {
                val readingTypes = actionDetailsViewModel.getReadingTypesLiveData().value
                Timber.d("Reading types: $readingTypes")
                readingTypes?.let {
                    val position = readingTypes.indexOf(condition.readingType)
                    val view = container_conditions.getChildAt(position)
                    (view as CheckedTextView).isChecked = true
                    view.text_title.text = condition.toString()
                    showOutcomes()
                }
            }
        })
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

    private fun observeMessageLiveData() {
        actionDetailsViewModel.getMessageLiveData().observe(this, Observer {
            it?.let {
                edittext_message.setText(it)
            }
        })
    }

    private fun observePhoneNumberLiveData() {
        actionDetailsViewModel.getPhoneNumberLiveData().observe(this, Observer {
            it?.let {
                edittext_phone_number.setText(it)
            }
        })
    }

    override fun onSetClicked(readingType: String, constraint: String, value: String) {
        container_activity.requestFocus()
        val readingTypes = actionDetailsViewModel.getReadingTypesLiveData().value
        initConditionViews(readingTypes!!)
        actionDetailsViewModel.setCondition(readingType, constraint, value)
    }

    private fun initConditionViews(readingTypes: List<String>) {
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

    private fun hideDevicesSpinner() {
        label_device.visibility = View.GONE
        spinner_devices.visibility = View.GONE
        hideConditions()
    }

    private fun showDevicesSpinner() {
        label_device.visibility = View.VISIBLE
        spinner_devices.visibility = View.VISIBLE
    }

    private fun hideConditions() {
        label_condition.visibility = View.GONE
        container_conditions.visibility = View.GONE
        hideOutcomes()
    }

    private fun showConditions() {
        label_condition.visibility = View.VISIBLE
        container_conditions.visibility = View.VISIBLE
    }

    private fun hideOutcomes() {
        label_outcome.visibility = View.GONE
        container_outcomes.visibility = View.GONE
        button_outcome_notification.isChecked = false
        button_outcome_sms.isChecked = false
        button_outcome_vibration.isChecked = false
        button_outcome_text_to_speech.isChecked = false
        edittext_phone_number.visibility = View.GONE
        edittext_message.visibility = View.GONE
        button_save.visibility = View.GONE
    }

    private fun showOutcomes() {
        label_outcome.visibility = View.VISIBLE
        container_outcomes.visibility = View.VISIBLE
    }

    companion object {

        private const val ACTION_ID_EXTRA = "action_id"

        fun start(context: Context) {
            val intent = Intent(context, ActionDetailsActivity::class.java)
            context.startActivity(intent)
        }

        fun start(context: Context, actionId: Long) {
            val intent = Intent(context, ActionDetailsActivity::class.java)
            intent.putExtra(ACTION_ID_EXTRA, actionId)
            context.startActivity(intent)
        }
    }
}