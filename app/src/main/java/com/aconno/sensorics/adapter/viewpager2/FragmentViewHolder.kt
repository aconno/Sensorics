package com.aconno.sensorics.adapter.viewpager2

import android.view.ViewGroup
import android.widget.FrameLayout

import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * [ViewHolder] implementation for handling [Fragment]s. Used in
 * [FragmentStateAdapter].
 */
class FragmentViewHolder private constructor(container: FrameLayout) :
    RecyclerView.ViewHolder(container) {

    internal val container: FrameLayout
        get() = itemView as FrameLayout

    companion object {

        internal fun create(parent: ViewGroup): FragmentViewHolder {
            val container = FrameLayout(parent.context)
            container.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            container.id = ViewCompat.generateViewId()
            container.isSaveEnabled = false
            return FragmentViewHolder(container)
        }
    }
}
