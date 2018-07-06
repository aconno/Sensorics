package com.aconno.sensorics.ui.actions

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CheckedTextView
import android.widget.TextView
import android.widget.Toast
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.R
import com.aconno.sensorics.dagger.actionlist.ActionListModule
import com.aconno.sensorics.dagger.actionlist.DaggerActionListComponent
import com.aconno.sensorics.dagger.addaction.AddActionComponent
import com.aconno.sensorics.dagger.addaction.AddActionModule
import com.aconno.sensorics.dagger.addaction.DaggerAddActionComponent
import com.aconno.sensorics.domain.ifttt.Condition
import com.aconno.sensorics.domain.ifttt.LimitCondition
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.ui.dialogs.SavedDevicesDialog
import com.aconno.sensorics.ui.dialogs.SavedDevicesDialogListener
import com.aconno.sensorics.viewmodel.NewActionViewModel
import kotlinx.android.synthetic.main.activity_action_add.*
import kotlinx.android.synthetic.main.content_action_detail.*
import javax.inject.Inject


class AddActionActivity : AppCompatActivity(), ConditionDialogListener, SavedDevicesDialogListener {

    @Inject
    lateinit var newActionViewModel: NewActionViewModel

    private val addActionComponent: AddActionComponent by lazy {
        val sensoricsApplication: SensoricsApplication? = application as? SensoricsApplication

        val actionListComponent =
            DaggerActionListComponent.builder().appComponent(sensoricsApplication?.appComponent)
                .actionListModule(ActionListModule())
                .build()

        DaggerAddActionComponent.builder().actionListComponent(actionListComponent)
            .addActionModule(AddActionModule(this)).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action_add)
        addActionComponent.inject(this)

        setConditionChipOnClickListeners()
        initConditionViews()

        add_action_button.setOnClickListener { this.addAction() }

        setOutcomeChipOnClickListeners()

        button_device.setOnClickListener {
            SavedDevicesDialog().show(supportFragmentManager, "saved_devices_dialog")
        }
    }

    private fun setOutcomeChipOnClickListeners() {
        outcome_notification.setOnClickListener {
            outcome_notification.isChecked = true
            outcome_sms.isChecked = false
            outcome_vibration.isChecked = false
            outcome_text_to_speech.isChecked = false
            showOutcomeOptions()
        }
        outcome_sms.setOnClickListener {
            outcome_notification.isChecked = false
            outcome_sms.isChecked = true
            outcome_vibration.isChecked = false
            outcome_text_to_speech.isChecked = false
            showOutcomeOptions()
        }
        outcome_vibration.setOnClickListener {
            outcome_notification.isChecked = false
            outcome_sms.isChecked = false
            outcome_vibration.isChecked = true
            outcome_text_to_speech.isChecked = false
            showOutcomeOptions()
        }
        outcome_text_to_speech.setOnClickListener {
            outcome_notification.isChecked = false
            outcome_sms.isChecked = false
            outcome_vibration.isChecked = false
            outcome_text_to_speech.isChecked = true
            showOutcomeOptions()
        }
    }

    private fun setConditionChipOnClickListeners() {
        temperature.setOnClickListener {
            openConditionDialog("Temperature")
        }
        light.setOnClickListener {
            openConditionDialog("Light")
        }
        humidity.setOnClickListener {
            openConditionDialog("Humidity")
        }
        pressure.setOnClickListener {
            openConditionDialog("Pressure")
        }
        magnetometer_x.setOnClickListener {
            openConditionDialog("Magnetometer X")
        }
        magnetometer_y.setOnClickListener {
            openConditionDialog("Magnetometer Y")
        }
        magnetometer_z.setOnClickListener {
            openConditionDialog("Magnetometer Z")
        }
        accelerometer_x.setOnClickListener {
            openConditionDialog("Accelerometer X")
        }
        accelerometer_y.setOnClickListener {
            openConditionDialog("Accelerometer Y")
        }
        accelerometer_z.setOnClickListener {
            openConditionDialog("Accelerometer Z")
        }
        gyroscope_x.setOnClickListener {
            openConditionDialog("Gyroscope X")
        }
        gyroscope_y.setOnClickListener {
            openConditionDialog("Gyroscope Y")
        }
        gyroscope_z.setOnClickListener {
            openConditionDialog("Gyroscope Z")
        }
        battery_level.setOnClickListener {
            openConditionDialog("Battery Level")
        }
    }

    override fun onSavedDevicesDialogItemClick(item: Device) {
        text_mac_address.text = item.macAddress
    }

    private fun openConditionDialog(readingType: String) {
        val dialog = ConditionDialog.newInstance(readingType)
        dialog.show(supportFragmentManager, "condition_dialog_fragment")
    }

    override fun onResume() {
        super.onResume()
        newActionViewModel.addActionResults.observe(this, Observer { onAddActionResult(it) })
    }

    private fun addAction() {
        val name = action_name.text.toString()
        val deviceMacAddress = text_mac_address.text.toString()
        val outcome = getOutcome()
        val smsDestination = phone_number.text.toString()
        val content = message.text.toString()

        newActionViewModel.addAction(
            name,
            deviceMacAddress,
            outcome,
            smsDestination,
            content
        )
    }

    private fun getOutcome(): String {
        if (outcome_notification.isChecked) {
            return "Phone Notification"
        }
        if (outcome_sms.isChecked) {
            return "SMS Message"
        }
        if (outcome_vibration.isChecked) {
            return "Vibration"
        }
        if (outcome_text_to_speech.isChecked) {
            return "Text to Speech"
        }
        return ""
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
        val selected = getOutcome()
        when (selected) {
            "Phone Notification" -> {
                message.visibility = View.VISIBLE
                phone_number.visibility = View.GONE
            }
            "SMS Message" -> {
                message.visibility = View.VISIBLE
                phone_number.visibility = View.VISIBLE
            }
            "Vibration" -> {
                message.visibility = View.GONE
                phone_number.visibility = View.GONE
            }
            "Text to Speech" -> {
                message.visibility = View.VISIBLE
                phone_number.visibility = View.GONE
            }
        }
    }

    override fun onSetClicked(readingType: String, constraint: String, value: String) {
        initConditionViews()
        //TODO: Prevent passing an empty string value
        newActionViewModel.setCondition(readingType, constraint, value)
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
        temperature.text = "Temperature"
        light.text = "Light"
        humidity.text = "Humidity"
        pressure.text = "Pressure"
        magnetometer_x.text = "Magnetometer X"
        magnetometer_y.text = "Magnetometer Y"
        magnetometer_z.text = "Magnetometer Z"
        accelerometer_x.text = "Accelerometer X"
        accelerometer_y.text = "Accelerometer Y"
        accelerometer_z.text = "Accelerometer Z"
        gyroscope_x.text = "Gyroscope X"
        gyroscope_y.text = "Gyroscope Y"
        gyroscope_z.text = "Gyroscope Z"
        battery_level.text = "Battery Level"
    }

    private fun initConditionStates() {
        temperature.isChecked = false
        light.isChecked = false
        humidity.isChecked = false
        pressure.isChecked = false
        magnetometer_x.isChecked = false
        magnetometer_y.isChecked = false
        magnetometer_z.isChecked = false
        accelerometer_x.isChecked = false
        accelerometer_y.isChecked = false
        accelerometer_z.isChecked = false
        gyroscope_x.isChecked = false
        gyroscope_y.isChecked = false
        gyroscope_z.isChecked = false
        battery_level.isChecked = false
    }

    private fun setSelectedCondition(condition: Condition) {
        val conditionView = getConditionView(condition.readingType)
        conditionView.isChecked = true
        appendConditionString(conditionView, condition)
    }

    private fun appendConditionString(textView: TextView, condition: Condition) {
        val constraint = when (condition.type) {
            LimitCondition.LESS_THAN -> "<"
            LimitCondition.MORE_THAN -> ">"
            else -> throw IllegalArgumentException("Invalid constraint identifier ${condition.type}")
        }
        textView.text = "${textView.text} $constraint ${condition.limit}"
    }

    private fun getConditionView(readingType: String): CheckedTextView {
        return when (readingType) {
            "Temperature" -> temperature
            "Light" -> light
            "Humidity" -> humidity
            "Pressure" -> pressure
            "Magnetometer X" -> magnetometer_x
            "Magnetometer Y" -> magnetometer_y
            "Magnetometer Z" -> magnetometer_z
            "Accelerometer X" -> accelerometer_x
            "Accelerometer Y" -> accelerometer_y
            "Accelerometer Z" -> accelerometer_z
            "Gyroscope X" -> gyroscope_x
            "Gyroscope Y" -> gyroscope_y
            "Gyroscope Z" -> gyroscope_z
            "Battery Level" -> battery_level
            else -> temperature //TODO: Fix
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AddActionActivity::class.java)
            context.startActivity(intent)
        }
    }
}