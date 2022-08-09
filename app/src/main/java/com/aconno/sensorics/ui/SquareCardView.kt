package com.aconno.sensorics.ui

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView

/**
 * @author aconno
 */
open class SquareCardView(context: Context, attrs: AttributeSet?) : CardView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, ignoredHeightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}