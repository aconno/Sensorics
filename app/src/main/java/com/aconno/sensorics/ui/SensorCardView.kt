package com.aconno.sensorics.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.aconno.sensorics.R
import com.aconno.sensorics.databinding.ViewSensorCardBinding

/**
 * @author aconno
 */
class SensorCardView(context: Context, attrs: AttributeSet?, defStyle: Int) :
    SquareCardView(context, attrs) {

    //TODO: check if the binding is correct
    private var binding: ViewSensorCardBinding =
        ViewSensorCardBinding.inflate(LayoutInflater.from(context))

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        addView(
            inflate(context, R.layout.view_sensor_card, null)
        )
    }

    fun update(newName: String, newValue: String) {
        binding.name.text = newName
        binding.value.text = newValue
    }
}