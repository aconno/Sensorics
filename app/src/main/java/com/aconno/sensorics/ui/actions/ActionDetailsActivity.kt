package com.aconno.sensorics.ui.actions

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.CheckedTextView
import androidx.lifecycle.Observer
import com.aconno.sensorics.R
import com.aconno.sensorics.adapter.DeviceSpinnerAdapter
import com.aconno.sensorics.domain.actions.outcomes.Outcome
import com.aconno.sensorics.domain.ifttt.Condition
import com.aconno.sensorics.domain.ifttt.LimitCondition
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.repository.Settings
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_action_details.*
import kotlinx.android.synthetic.main.item_chip.view.*
import timber.log.Timber
import javax.inject.Inject

class ActionDetailsActivity : DaggerAppCompatActivity(), LimitConditionDialogListener {
    @Inject
    lateinit var actionDetailsViewModel: ActionDetailsViewModel

    @Inject
    lateinit var settings: Settings

    private val deviceSpinnerAdapter = DeviceSpinnerAdapter()

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action_details)

        setDevicesSpinnerAdapter()
        setDevicesSelectListener()

        setActiveSwitchListener()

        setOutcomeSelectListeners()

        setTimeChangeListeners()

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

    private fun setActiveSwitchListener() {
        switch_active.setOnCheckedChangeListener { _, checked ->
            val name = edittext_name.text.toString()
            actionDetailsViewModel.setActive(name, checked)
        }
    }

    private fun setDevicesSpinnerAdapter() {
        spinner_devices.adapter = deviceSpinnerAdapter
        disposables.add(
            actionDetailsViewModel.getDevices()
                .subscribe({ devices ->
                    deviceSpinnerAdapter.setDevices(devices)
                    deviceSpinnerAdapter.setIcons(getIconInfoForDevices(devices))
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
                val message1 = edittext_message1.text.toString()
                val message2 = edittext_message2.text.toString()

                actionDetailsViewModel.setDevice(device, name, message1, message2)
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
                    LimitConditionDialog(this, readingType, this).show()
                }
                container_conditions.addView(newView)
            } else {
                view.text_title.text = readingType
                (view as CheckedTextView).isChecked = false
                view.visibility = View.VISIBLE
                view.setOnClickListener {
                    LimitConditionDialog(this, readingType, this).show()
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
                val message1 = edittext_message1.text.toString()
                val message2 = edittext_message2.text.toString()
                actionDetailsViewModel.clearCondition(name, message1, message2)
            }
        }
    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(container_activity, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun applyLimitCondition(limitCondition: LimitCondition) {
        val name = edittext_name.text.toString()
        val message1 = edittext_message1.text.toString()
        val message2 = edittext_message2.text.toString()
        actionDetailsViewModel.setLimitCondition(limitCondition, name, message1, message2)
    }

    private fun observeActionLiveData() {
        actionDetailsViewModel.getActionLiveData().observe(this, Observer { action ->
            action?.let {
                Timber.d("On action changed")
                setName(action.name)
                setActive(action.active)
                setDevice(action.device)
                setOutcome(action.outcome)
                setTimeFromData(action.timeFrom)
                setTimeToData(action.timeTo)
            }
        })
    }

    private fun setName(name: String) {
        edittext_name.setText(name)
    }

    private fun setActive(active: Boolean) {
        switch_active.isChecked = active
    }

    private fun setDevice(device: Device?) {
        device?.let {
            val position = deviceSpinnerAdapter.getDevicePosition(device)
            Timber.i("Position-------- $position")
            if (spinner_devices.selectedItemPosition != position) {
                spinner_devices.setSelection(position)
            } else {
                invalidateConditions(device)
            }
        }
    }

    private fun setTimeChangeListeners() {
        timepicker_time_from.setIs24HourView(true)
        timepicker_time_from.setOnTimeChangedListener { _, hour, minute ->
            val name = edittext_name.text.toString()
            actionDetailsViewModel.setTimeFrom(name, hour * 3600 + minute * 60)
        }

        timepicker_time_to.setIs24HourView(true)
        timepicker_time_to.setOnTimeChangedListener { _, hour, minute ->
            val name = edittext_name.text.toString()
            actionDetailsViewModel.setTimeTo(name, hour * 3600 + minute * 60)
        }
    }

    @Suppress("DEPRECATION")
    private fun setTimeFromData(timeOfDayInSeconds: Int) {
        var time = timeOfDayInSeconds
        val seconds = time % 60
        time -= seconds
        time /= 60
        val minutes = time % 60
        time -= minutes
        time /= 60
        val hours = time % 24

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timepicker_time_from.hour = hours
            timepicker_time_from.minute = minutes
        } else {
            timepicker_time_from.currentHour = hours
            timepicker_time_from.currentMinute = minutes
        }
    }

    @Suppress("DEPRECATION")
    private fun setTimeToData(timeOfDayInSeconds: Int) {
        var time = timeOfDayInSeconds
        val seconds = time % 60
        time -= seconds
        time /= 60
        val minutes = time % 60
        time -= minutes
        time /= 60
        val hours = time % 24

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timepicker_time_to.hour = hours
            timepicker_time_to.minute = minutes
        } else {
            timepicker_time_to.currentHour = hours
            timepicker_time_to.currentMinute = minutes
        }
    }

    private fun setOutcomeSelectListeners() {
        button_outcome_notification.setOnClickListener {
            setOutcomeData(Outcome.OUTCOME_TYPE_NOTIFICATION)
        }

        button_outcome_text_to_speech.setOnClickListener {
            setOutcomeData(Outcome.OUTCOME_TYPE_TEXT_TO_SPEECH)
        }

        button_outcome_vibration.setOnClickListener {
            setOutcomeData(Outcome.OUTCOME_TYPE_VIBRATION)
        }

        button_outcome_alarm.setOnClickListener {
            setOutcomeData(Outcome.OUTCOME_TYPE_ALARM)
        }

        button_outcome_sms.setOnClickListener {
            setOutcomeData(Outcome.OUTCOME_TYPE_SMS)
        }
    }

    private fun setOutcomeData(type: Int) {
        val message1 = edittext_message1.text.toString()
        val message2 = edittext_message2.text.toString()
        val name = edittext_name.text.toString()
        actionDetailsViewModel.setOutcome(type, message1, message2, name)
    }

    private fun setOutcome(outcome: Outcome?) {
        button_outcome_notification.isChecked = false
        button_outcome_text_to_speech.isChecked = false
        button_outcome_vibration.isChecked = false
        button_outcome_alarm.isChecked = false
        button_outcome_sms.isChecked = false
        edittext_message1.visibility = View.GONE
        edittext_message2.visibility = View.GONE

        outcome?.let {
            when (outcome.type) {
                Outcome.OUTCOME_TYPE_NOTIFICATION -> {
                    button_outcome_notification.isChecked = true
                    edittext_message1.visibility = View.VISIBLE
                    edittext_message1.setText(outcome.parameters[Outcome.TEXT_MESSAGE] ?: "")
                }

                Outcome.OUTCOME_TYPE_TEXT_TO_SPEECH -> {
                    button_outcome_text_to_speech.isChecked = true
                    edittext_message1.visibility = View.VISIBLE
                    edittext_message1.setText(outcome.parameters[Outcome.TEXT_MESSAGE] ?: "")
                }
                Outcome.OUTCOME_TYPE_VIBRATION -> button_outcome_vibration.isChecked = true
                Outcome.OUTCOME_TYPE_ALARM -> button_outcome_alarm.isChecked = true
                Outcome.OUTCOME_TYPE_SMS -> {
                    button_outcome_sms.isChecked = true
                    edittext_message1.visibility = View.VISIBLE
                    edittext_message2.visibility = View.VISIBLE
                    edittext_message1.setText(outcome.parameters[Outcome.TEXT_MESSAGE] ?: "")
                    edittext_message2.setText(outcome.parameters[Outcome.PHONE_NUMBER] ?: "")
                }
            }
        }
    }

    private fun setSaveButtonListener() {
        button_save.setOnClickListener {
            val message1 = edittext_message1.text.toString()
            val message2 = edittext_message2.text.toString()
            val name = edittext_name.text.toString()
            actionDetailsViewModel.saveAction(application, name, message1, message2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    finish()

                }, {
                    showSnackbarMessage(
                        it.message
                            ?: getString(R.string.message_save_unsuccessful)
                    )
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

    private fun getIconInfoForDevices(deviceNames: List<Device>): HashMap<String, String> {
        val hashMap: HashMap<String, String> = hashMapOf()

        deviceNames.forEach { device ->
            if (!hashMap.containsKey(device.name))
                actionDetailsViewModel.getIconPath(device.name)?.let {
                    hashMap[device.name] = it
                }
        }
        return hashMap
    }
}