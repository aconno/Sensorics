package de.troido.acnsensa.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import de.troido.acnsensa.R
import de.troido.acnsensa.data.Sensor
import kotlinx.android.synthetic.main.view_sensor_card.view.*

@SuppressLint("InflateParams")
class SensorCardView(context: Context, attrs: AttributeSet?, defStyle: Int)
    : SquareCardView(context, attrs, defStyle) {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        addView(LayoutInflater.from(context)
                .inflate(R.layout.view_sensor_card, null, false))
        tv_sensor.typeface = Fonts.get(context, Font.PT_SANS_BOLD)
        tv_value.typeface = Fonts.get(context, Font.PT_SANS_REGULAR)
    }

    fun update(sensor: Sensor<*>) {
        iv_icon.setImageResource(uiImage(sensor))
        tv_sensor.text = uiTitle(sensor)
        tv_value.text = uiText(sensor)
    }
}
