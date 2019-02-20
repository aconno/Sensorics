package com.aconno.sensorics.ui.logs

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet

class LogTextView : AppCompatTextView {

    private var onSelectionChangedListener: OnSelectionChangedListener? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setOnSelectionChangedListener(onSelectionChangedListener: OnSelectionChangedListener) {
        this.onSelectionChangedListener = onSelectionChangedListener
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        if(selEnd != selStart) {
            onSelectionChangedListener?.onSelectionChanged()
        }
    }

    interface OnSelectionChangedListener {
        fun onSelectionChanged()
    }
}