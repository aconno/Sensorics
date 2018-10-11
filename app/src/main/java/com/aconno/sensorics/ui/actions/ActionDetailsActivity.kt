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
import com.aconno.sensorics.domain.actions.outcomes.Outcome
import com.aconno.sensorics.domain.ifttt.Condition
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.repository.Settings
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_action_details.*
import kotlinx.android.synthetic.main.item_chip.view.*
import timber.log.Timber
import javax.inject.Inject

class ActionDetailsActivity : AppCompatActivity(), ConditionDialogListener {

    @Inject
    lateinit var actionDetailsViewModel: ActionDetailsViewModel

    @Inject
    lateinit var settings: Settings

    private val deviceSpinnerAdapter = DeviceSpinnerAdapter()

    private val disposables = CompositeDisposable()

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

        setDevicesSpinnerAdapter()
        setDevicesSelectListener()

        setOutcomeSelectListeners()

        setSaveButtonListener()

        observeActionLiveData()

        if (actionExists()) {
            val actionId = intent.getLongExtra(ACTION_ID_EXTRA, 0L)
            actionDetailsViewModel.setActionId(actionId)
        }
    }

    override fun onResume() {
        super.onResume()
        container_activity.requestFocus()
    }

    private fun setDevicesSpinnerAdapter() {
        spinner_devices.adapter = deviceSpinnerAdapter
        disposables.add(
            actionDetailsViewModel.getDevices()
                .subscribe({ devices ->
                    deviceSpinnerAdapter.setDevices(devices)
                    if (!actionExists()) setDefaultDevice()
                }, {
                    showSnackbarMessage(getString(R.string.message_no_devices))
                })
        )
    }

    private fun setDefaultDevice() {
        disposables.add(
            settings.getLastClickedDeviceMac()
                .subscribe(
                    { defaultDeviceMac ->
                        val defaultPosition =
                            deviceSpinnerAdapter.getDevices()
                                .indexOfFirst { it.macAddress == defaultDeviceMac }
                        if (defaultPosition != -1) {
                            spinner_devices.setSelection(defaultPosition)
                        }
                    },
                    { throwable ->
                        Timber.d(throwable)
                    }
                )
        )
    }

    private fun setDevicesSelectListener() {
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
                Timber.d("Item selected, position: $position")
                val device = deviceSpinnerAdapter.getDevice(position)
                val name = edittext_name.text.toString()
                val message = edittext_message.text.toString()
                val phoneNumber = edittext_phone_number.text.toString()
                actionDetailsViewModel.setDevice(device, name, message, phoneNumber)
            }
        }
    }

    private fun invalidateConditions(device: Device) {
        val readingTypes = actionDetailsViewModel.getReadingTypes(device)
        val action = actionDetailsViewModel.getActionLiveData().value
        Timber.d("Invalidate conditions: ${action?.condition}")
        initConditionViews(readingTypes, action?.condition)
    }

    private fun initConditionViews(readingTypes: List<String>, condition: Condition?) {
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
        condition?.let {
            val position = readingTypes.indexOf(condition.readingType)
            val view = container_conditions.getChildAt(position)
            if (view is CheckedTextView) {
                view.text = condition.toString()
                view.isChecked = true
            } else {
                val name = edittext_name.text.toString()
                val message = edittext_message.text.toString()
                val phoneNumber = edittext_phone_number.text.toString()
                actionDetailsViewModel.clearCondition(name, message, phoneNumber)
            }
        }
    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(container_activity, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onSetClicked(readingType: String, constraint: String, value: String) {
        val name = edittext_name.text.toString()
        val message = edittext_message.text.toString()
        val phoneNumber = edittext_phone_number.text.toString()
        actionDetailsViewModel.setCondition(
            readingType,
            value,
            constraint,
            name,
            message,
            phoneNumber
        )
    }

    private fun observeActionLiveData() {
        actionDetailsViewModel.getActionLiveData().observe(this, Observer { action ->
            action?.let {
                Timber.d("On action changed")
                setName(action.name)
                setDevice(action.device)
                setOutcome(action.outcome)
            }
        })
    }

    private fun setName(name: String) {
        edittext_name.setText(name)
    }

    private fun setDevice(device: Device?) {
        device?.let {
            val position = deviceSpinnerAdapter.getDevicePosition(device)
            if (spinner_devices.selectedItemPosition != position) {
                spinner_devices.setSelection(position)
            } else {
                invalidateConditions(device)
            }
        }
    }

    private fun setOutcomeSelectListeners() {
        button_outcome_notification.setOnClickListener {
            setOutcomeData(Outcome.OUTCOME_TYPE_NOTIFICATION)
        }
        button_outcome_sms.setOnClickListener {
            setOutcomeData(Outcome.OUTCOME_TYPE_SMS)
        }
        button_outcome_text_to_speech.setOnClickListener {
            setOutcomeData(Outcome.OUTCOME_TYPE_TEXT_TO_SPEECH)
        }
        button_outcome_vibration.setOnClickListener {
            setOutcomeData(Outcome.OUTCOME_TYPE_VIBRATION)
        }
    }

    private fun setOutcomeData(type: Int) {
        val message = edittext_message.text.toString()
        val phoneNumber = edittext_phone_number.text.toString()
        val name = edittext_name.text.toString()
        actionDetailsViewModel.setOutcome(type, message, phoneNumber, name)
    }

    private fun setOutcome(outcome: Outcome?) {
        button_outcome_notification.isChecked = false
        button_outcome_sms.isChecked = false
        button_outcome_text_to_speech.isChecked = false
        button_outcome_vibration.isChecked = false
        edittext_message.visibility = View.GONE
        edittext_phone_number.visibility = View.GONE
        outcome?.let {
            when (outcome.type) {
                Outcome.OUTCOME_TYPE_NOTIFICATION -> {
                    button_outcome_notification.isChecked = true
                    edittext_message.visibility = View.VISIBLE
                }
                Outcome.OUTCOME_TYPE_SMS -> {
                    button_outcome_sms.isChecked = true
                    edittext_message.visibility = View.VISIBLE
                    edittext_phone_number.visibility = View.VISIBLE
                }
                Outcome.OUTCOME_TYPE_TEXT_TO_SPEECH -> {
                    button_outcome_text_to_speech.isChecked = true
                    edittext_message.visibility = View.VISIBLE
                }
                Outcome.OUTCOME_TYPE_VIBRATION -> button_outcome_vibration.isChecked = true
            }
            edittext_message.setText(outcome.parameters[Outcome.TEXT_MESSAGE] ?: "")
            edittext_phone_number.setText(outcome.parameters[Outcome.PHONE_NUMBER] ?: "")
        }
    }

    private fun setSaveButtonListener() {
        button_save.setOnClickListener { _ ->
            val message = edittext_message.text.toString()
            val phoneNumber = edittext_phone_number.text.toString()
            val name = edittext_name.text.toString()
            actionDetailsViewModel.saveAction(name, message, phoneNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    finish()
                }, {
                    showSnackbarMessage(it.message ?: getString(R.string.message_save_unsuccessful))
                })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private fun actionExists() = intent.hasExtra(ACTION_ID_EXTRA)

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