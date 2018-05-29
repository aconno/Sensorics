package com.aconno.acnsensa.ui.settings

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import com.aconno.acnsensa.R
import com.aconno.acnsensa.domain.ifttt.BasePublish
import com.aconno.acnsensa.domain.ifttt.GooglePublish
import com.aconno.acnsensa.domain.ifttt.RESTPublish


import com.aconno.acnsensa.ui.settings.PublishFragment.OnListFragmentInteractionListener

import kotlinx.android.synthetic.main.item_publish.view.*

/**
 * [RecyclerView.Adapter] that can display a [GooglePublish] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class PublishRecyclerViewAdapter(
    private val mValues: List<BasePublish>,
    private val mListener: OnListFragmentInteractionListener?,
    private val mCheckedChangeListener: OnCheckedChangeListener?
) : RecyclerView.Adapter<PublishRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as BasePublish
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_publish, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mNameView.text = item.name
        holder.mEnableView.isChecked = item.enabled

        if (item is GooglePublish) {
            holder.mImageView.setImageResource(R.drawable.google_logo)
        } else if (item is RESTPublish) {
            holder.mImageView.setImageResource(R.drawable.uplaod_cloud)
        }

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }

        holder.mEnableView.setOnCheckedChangeListener({ _, isChecked ->
            mCheckedChangeListener?.onCheckedChange(isChecked, position)
        })
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mNameView: TextView = mView.publish_name
        val mEnableView: Switch = mView.publish_switch
        val mImageView: ImageView = mView.publish_image

        override fun toString(): String {
            return super.toString() + " '" + mEnableView.text + "'"
        }
    }

    interface OnCheckedChangeListener {
        fun onCheckedChange(checked: Boolean, position: Int)
    }

}
