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
import com.aconno.sensorics.adapter.SelectableRecyclerViewAdapter
import com.aconno.sensorics.domain.ifttt.outcome.PublishType
import com.aconno.sensorics.model.*
import kotlinx.android.synthetic.main.item_publish.view.*

/**
 * [RecyclerView.Adapter] that can display a [BasePublishModel] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class PublishRecyclerViewAdapter(
    values: MutableList<BasePublishModel>,
    clickListener: ItemClickListener<BasePublishModel>?,
    longClickListener: ItemLongClickListener<BasePublishModel>?,
    itemSelectedListener: ItemSelectedListener<BasePublishModel>? = null
) : SelectableRecyclerViewAdapter<BasePublishModel,PublishRecyclerViewAdapter.PublisherKey,PublishRecyclerViewAdapter.ViewHolder>(values.toMutableList(),itemSelectedListener,clickListener,longClickListener) {
    private var mCheckedChangeListener: OnCheckedChangeListener? = null

    fun setOnCheckedChangeListener(checkedChangeListener: OnCheckedChangeListener?) {
        this.mCheckedChangeListener = checkedChangeListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_publish, parent, false)
        return ViewHolder(view)
    }


    inner class ViewHolder(private val mView: View) : SelectableRecyclerViewAdapter<BasePublishModel, PublisherKey, ViewHolder>.ViewHolder(mView) {
        private val mNameView: TextView = mView.publish_name
        private val mEnableView: Switch = mView.publish_switch
        private val mImageView: ImageView = mView.publish_image
        private val selectionButton: CheckBox = mView.cb_item_selected

        override fun toString(): String {
            return super.toString() + " '" + mEnableView.text + "'"
        }

        override fun bind(item: BasePublishModel) {
            mNameView.text = item.name
            mEnableView.isChecked = item.enabled

            with(selectionButton) {
                visibility = if (isItemSelectionEnabled) {
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }

                isChecked = isItemSelected(item)
            }

            when (item) {
                is GooglePublishModel -> mImageView.setImageResource(R.drawable.google_logo)
                is RestPublishModel -> mImageView.setImageResource(R.drawable.upload_cloud)
                is MqttPublishModel -> mImageView.setImageResource(R.drawable.mqtt_logo)
                is AzureMqttPublishModel -> mImageView.setImageResource(R.drawable.azure_logo)
            }

            with(mView) {
                tag = item
                setOnLongClickListener {
                    onItemLongClick(item)
                    true
                }
            }

            mEnableView.setOnCheckedChangeListener { _, isChecked ->
                mCheckedChangeListener?.onCheckedChange(isChecked, adapterPosition)
            }

            mView.setOnClickListener {
                if (isItemSelectionEnabled) {
                    selectionButton.isChecked = !selectionButton.isChecked
                    setSelected(item,selectionButton.isChecked)
                } else {
                    onItemClick(item)
                }
            }

            selectionButton.setOnClickListener {
                setSelected(item,selectionButton.isChecked)
            }
        }
    }

    interface OnCheckedChangeListener {
        fun onCheckedChange(checked: Boolean, position: Int)
    }

    override fun getKeyForItem(item: BasePublishModel): PublisherKey {
        return PublisherKey(item.id,item.type)
    }

    data class PublisherKey(
        val id : Long,
        val type : PublishType
    )

}
