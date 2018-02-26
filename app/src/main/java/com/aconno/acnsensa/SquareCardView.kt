package com.aconno.acnsensa

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet

/**
 * @author aconno
 */
open class SquareCardView(context: Context, attrs: AttributeSet?) : CardView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, ignoredHeightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}