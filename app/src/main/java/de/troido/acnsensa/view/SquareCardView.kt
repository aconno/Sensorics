package de.troido.acnsensa.view

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet

open class SquareCardView(context: Context, attrs: AttributeSet?, defStyle: Int)
    : CardView(context, attrs) {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) =
            super.onMeasure(widthMeasureSpec, widthMeasureSpec)
}
