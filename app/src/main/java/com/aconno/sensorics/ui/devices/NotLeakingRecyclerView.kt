package com.aconno.sensorics.ui.devices

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet

class NotLeakingRecyclerView(context: Context, attrs: AttributeSet?, defStyle: Int) :
    RecyclerView(context, attrs) {

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (adapter != null) {
            adapter = null
        }
    }
}