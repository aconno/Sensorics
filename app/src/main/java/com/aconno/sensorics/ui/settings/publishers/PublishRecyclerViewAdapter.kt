package com.aconno.sensorics.ui.settings.publishers


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aconno.sensorics.R
import com.aconno.sensorics.model.*
import kotlinx.android.synthetic.main.item_publish.view.*

/**
 * [RecyclerView.Adapter] that can display a [BasePublishModel] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class PublishRecyclerViewAdapter(
    private val mValues: MutableList<BasePublishModel>,
    private val mListener: PublishListFragment.OnListFragmentClickListener?,
    private val mLongClickListener: OnListItemLongClickListener?,
    private val itemSelectedListener: OnListItemSelectedListener? = null
) : RecyclerView.Adapter<PublishRecyclerViewAdapter.ViewHolder>() {
    private var mCheckedChangeListener: OnCheckedChangeListener? = null

    var itemSelectionEnabled = false
        private set
    private var itemSelectedMap: MutableMap<Long, Boolean> =
        HashMap()//maps item id to current item selection state


    fun getAllPublishers(): List<BasePublishModel> {
        return mValues
    }


    fun enableItemSelection(initiallySelectedItem: BasePublishModel? = null) {
        itemSelectionEnabled = true
        itemSelectedMap.clear()
        initiallySelectedItem?.let {
            itemSelectedMap[it.id] = true
        }
        notifyDataSetChanged()
    }

    fun disableItemSelection() {
        itemSelectionEnabled = false
        itemSelectedMap.clear()
        notifyDataSetChanged()
    }

    fun getNumberOfSelectedItems(): Int = itemSelectedMap.count { entry -> entry.value }

    fun getSelectedItems(): List<BasePublishModel> =
        mValues.filter { itemSelectedMap[it.id] == true }

    fun setItemsAsSelected(items: List<BasePublishModel>) {
        for (item in items) {
            onItemSelectionStateChanged(true, item)
        }
        notifyDataSetChanged()
    }

    private fun onItemSelectionStateChanged(selected: Boolean, item: BasePublishModel) {
        itemSelectedMap[item.id] = selected
        itemSelectedListener?.apply {
            if (selected) onListItemSelected(item)
            else onListItemDeselected(item)
        }
    }


    fun setOnCheckedChangeListener(checkedChangeListener: OnCheckedChangeListener?) {
        this.mCheckedChangeListener = checkedChangeListener
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

        with(holder.selectionButton) {
            visibility = if (itemSelectionEnabled) {
                View.VISIBLE
            } else {
                View.GONE
            }

            isChecked = itemSelectedMap[item.id] ?: false
        }

        when (item) {
            is GooglePublishModel -> holder.mImageView.setImageResource(R.drawable.google_logo)
            is RestPublishModel -> holder.mImageView.setImageResource(R.drawable.upload_cloud)
            is MqttPublishModel -> holder.mImageView.setImageResource(R.drawable.mqtt_logo)
            is AzureMqttPublishModel -> holder.mImageView.setImageResource(R.drawable.azure_logo)
        }

        with(holder.mView) {
            tag = item
            setOnLongClickListener {
                mLongClickListener?.onListItemLongClick(item)
                true
            }
        }

        holder.mEnableView.setOnCheckedChangeListener { _, isChecked ->
            mCheckedChangeListener?.onCheckedChange(isChecked, holder.adapterPosition)
        }

        holder.mView.setOnClickListener {
            if (itemSelectionEnabled) {
                holder.selectionButton.isChecked = !holder.selectionButton.isChecked
                onItemSelectionStateChanged(holder.selectionButton.isChecked, item)
            } else {
                mListener?.onListFragmentClick(item)
            }
        }

        holder.selectionButton.setOnClickListener {
            onItemSelectionStateChanged(holder.selectionButton.isChecked, item)
        }
    }

    override fun getItemCount(): Int = mValues.size

    fun getPublishModel(position: Int) = mValues[position]

    fun removePublishModel(position: Int) {
        mValues.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addPublishModelAtPosition(basePublishModel: BasePublishModel, position: Int) {
        mValues.add(position, basePublishModel)
        notifyItemInserted(position)
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mNameView: TextView = mView.publish_name
        val mEnableView: Switch = mView.publish_switch
        val mImageView: ImageView = mView.publish_image
        val selectionButton: CheckBox = mView.item_selected

        override fun toString(): String {
            return super.toString() + " '" + mEnableView.text + "'"
        }
    }

    interface OnCheckedChangeListener {
        fun onCheckedChange(checked: Boolean, position: Int)
    }

    interface OnListItemLongClickListener {
        fun onListItemLongClick(item: BasePublishModel?)
    }

    interface OnListItemSelectedListener {
        fun onListItemSelected(item: BasePublishModel)
        fun onListItemDeselected(item: BasePublishModel)
    }

}
