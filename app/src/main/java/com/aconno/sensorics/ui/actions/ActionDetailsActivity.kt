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
import com.aconno.sensorics.databinding.ActivityActionDetailsBinding
import com.aconno.sensorics.databinding.ItemChipBinding
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
import timber.log.Timber
import javax.inject.Inject

class ActionDetailsActivity : DaggerAppCompatActivity(), LimitConditionDialogListener {

    private lateinit var binding: ActivityActionDetailsBinding

    @Inject
    lateinit var actionDetailsViewModel: ActionDetailsViewModel

    @Inject
    lateinit var settings: Settings

    private val deviceSpinnerAdapter = DeviceSpinnerAdapter()

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityActionDetailsBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

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
        binding.containerActivity.requestFocus()
    }

    private fun setActiveSwitchListener() {
        binding.switchActive.setOnCheckedChangeListener { _, checked ->
            val name = binding.editTextName.text.toString()
            actionDetailsViewModel.setActive(name, checked)
        }
    }

    private fun setDevicesSpinnerAdapter() {
        binding.spinnerDevices.adapter = deviceSpinnerAdapter
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
                            binding.spinnerDevices.setSelection(defaultPosition)
                        }
                    },
                    { throwable ->
                        Timber.d(throwable)
                    }
                )
        )
    }

    private fun setDevicesSelectListener() {
        binding.spinnerDevices.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

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
                    val name = binding.editTextName.text.toString()
                    val message1 = binding.editTextMessage1.text.toString()
                    val message2 = binding.editTextMessage2.text.toString()

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
            val view = binding.containerConditions.getChildAt(index)
            if (view == null) {
//                val newView = LayoutInflater.from(this)
//                    .inflate(R.layout.item_chip, binding.containerConditions, false)

                val newBinding =
                    ItemChipBinding.inflate(layoutInflater, binding.containerConditions, false)

                newBinding.textTitle.text = readingType
                newBinding.root.setOnClickListener {
                    LimitConditionDialog(this, readingType, this).show()
                }
                binding.containerConditions.addView(newBinding.root)
            } else {
                val newBinding =
                    ItemChipBinding.inflate(layoutInflater, binding.containerConditions, false)
                newBinding.textTitle.text = readingType
                (view as CheckedTextView).isChecked = false
                view.visibility = View.VISIBLE
                view.setOnClickListener {
                    LimitConditionDialog(this, readingType, this).show()
                }
            }
        }
        if (binding.containerConditions.childCount > readingTypes.size) {
            for (index in readingTypes.size until binding.containerConditions.childCount) {
                binding.containerConditions.getChildAt(index).visibility = View.GONE
            }
        }
        condition?.let {
            val position = readingTypes.indexOf(condition.readingType)
            val view = binding.containerConditions.getChildAt(position)
            if (view is CheckedTextView) {
                view.text = condition.toString()
                view.isChecked = true
            } else {
                val name = binding.editTextName.text.toString()
                val message1 = binding.editTextMessage1.text.toString()
                val message2 = binding.editTextMessage2.text.toString()
                actionDetailsViewModel.clearCondition(name, message1, message2)
            }
        }
    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(binding.containerActivity, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun applyLimitCondition(limitCondition: LimitCondition) {
        val name = binding.editTextName.text.toString()
        val message1 = binding.editTextMessage1.text.toString()
        val message2 = binding.editTextMessage2.text.toString()
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
        binding.editTextName.setText(name)
    }

    private fun setActive(active: Boolean) {
        binding.switchActive.isChecked = active
    }

    private fun setDevice(device: Device?) {
        device?.let {
            val position = deviceSpinnerAdapter.getDevicePosition(device)
            Timber.i("Position-------- $position")
            if (binding.spinnerDevices.selectedItemPosition != position) {
                binding.spinnerDevices.setSelection(position)
            } else {
                invalidateConditions(device)
            }
        }
    }

    private fun setTimeChangeListeners() {
        binding.timePickerTimeFrom.setIs24HourView(true)
        binding.timePickerTimeFrom.setOnTimeChangedListener { _, hour, minute ->
            val name = binding.editTextName.text.toString()
            actionDetailsViewModel.setTimeFrom(name, hour * 3600 + minute * 60)
        }

        binding.timePickerTimeTo.apply {
            setIs24HourView(true)
            setOnTimeChangedListener { _, hour, minute ->
                val name = binding.editTextName.text.toString()
                actionDetailsViewModel.setTimeTo(name, hour * 3600 + minute * 60)
            }
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
            binding.timePickerTimeFrom.apply {
                hour = hours
                minute = minutes
            }

        } else {
            binding.timePickerTimeFrom.apply {
                currentHour = hours
                currentMinute = minutes
            }
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
            binding.timePickerTimeTo.apply {
                hour = hours
                minute = minutes
            }

        } else {
            binding.timePickerTimeTo.apply {
                currentHour = hours
                currentMinute = minutes
            }
        }
    }

    private fun setOutcomeSelectListeners() {
        binding.buttonOutcomeNotification.setOnClickListener {
            setOutcomeData(Outcome.OUTCOME_TYPE_NOTIFICATION)
        }

        binding.buttonOutcomeTextToSpeech.setOnClickListener {
            setOutcomeData(Outcome.OUTCOME_TYPE_TEXT_TO_SPEECH)
        }

        binding.buttonOutcomeVibration.setOnClickListener {
            setOutcomeData(Outcome.OUTCOME_TYPE_VIBRATION)
        }

        binding.buttonOutcomeAlarm.setOnClickListener {
            setOutcomeData(Outcome.OUTCOME_TYPE_ALARM)
        }

        binding.buttonOutcomeSms.setOnClickListener {
            setOutcomeData(Outcome.OUTCOME_TYPE_SMS)
        }
    }

    private fun setOutcomeData(type: Int) {
        val message1 = binding.editTextMessage1.text.toString()
        val message2 = binding.editTextMessage2.text.toString()
        val name = binding.editTextName.text.toString()
        actionDetailsViewModel.setOutcome(type, message1, message2, name)
    }

    private fun setOutcome(outcome: Outcome?) {
        binding.buttonOutcomeNotification.isChecked = false
        binding.buttonOutcomeTextToSpeech.isChecked = false
        binding.buttonOutcomeVibration.isChecked = false
        binding.buttonOutcomeAlarm.isChecked = false
        binding.buttonOutcomeSms.isChecked = false
        binding.editTextMessage1.visibility = View.GONE
        binding.editTextMessage2.visibility = View.GONE

        outcome?.let {
            when (outcome.type) {
                Outcome.OUTCOME_TYPE_NOTIFICATION -> {
                    binding.buttonOutcomeNotification.isChecked = true
                    binding.editTextMessage1.visibility = View.VISIBLE
                    binding.editTextMessage1.setText(outcome.parameters[Outcome.TEXT_MESSAGE] ?: "")
                }

                Outcome.OUTCOME_TYPE_TEXT_TO_SPEECH -> {
                    binding.buttonOutcomeTextToSpeech.isChecked = true
                    binding.editTextMessage1.visibility = View.VISIBLE
                    binding.editTextMessage1.setText(outcome.parameters[Outcome.TEXT_MESSAGE] ?: "")
                }
                Outcome.OUTCOME_TYPE_VIBRATION -> binding.buttonOutcomeVibration.isChecked = true
                Outcome.OUTCOME_TYPE_ALARM -> binding.buttonOutcomeAlarm.isChecked = true
                Outcome.OUTCOME_TYPE_SMS -> {
                    binding.buttonOutcomeSms.isChecked = true
                    binding.editTextMessage1.visibility = View.VISIBLE
                    binding.editTextMessage2.visibility = View.VISIBLE
                    binding.editTextMessage1.setText(outcome.parameters[Outcome.TEXT_MESSAGE] ?: "")
                    binding.editTextMessage2.setText(outcome.parameters[Outcome.PHONE_NUMBER] ?: "")
                }
            }
        }
    }

    private fun setSaveButtonListener() {
        binding.buttonSave.setOnClickListener {
            val message1 = binding.editTextMessage1.text.toString()
            val message2 = binding.editTextMessage2.text.toString()
            val name = binding.editTextName.text.toString()
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