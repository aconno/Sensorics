package com.aconno.acnsensa.ui.views

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.widget.TextView
import com.aconno.acnsensa.R

class ChipView : TextView {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    init {
        setShape()
        setColors()
    }

    private fun setShape() {
        background = ContextCompat.getDrawable(context, R.drawable.chip_shape)
    }

    private fun setColors() {
        backgroundTintList = ContextCompat.getColorStateList(context, R.color.chip_background)
        setTextColor(ContextCompat.getColorStateList(context, R.color.chip_text))
    }

}