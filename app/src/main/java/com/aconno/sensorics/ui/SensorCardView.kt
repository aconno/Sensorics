package com.aconno.sensorics.ui

import android.content.Context
import android.util.AttributeSet
import com.aconno.sensorics.R
import kotlinx.android.synthetic.main.view_sensor_card.view.*

/**
 * @author aconno
 */
class SensorCardView(context: Context, attrs: AttributeSet?, defStyle: Int) :
    SquareCardView(context, attrs) {

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        addView(
            inflate(context, R.layout.view_sensor_card, null)
        )
    }

    fun update(newName: String, newValue: String) {
        name.text = newName
        value.text = newValue
    }
}