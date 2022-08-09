package com.aconno.sensorics.ui.settings.virtualscanningsources

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aconno.sensorics.R
import com.aconno.sensorics.model.BaseVirtualScanningSourceModel
import com.aconno.sensorics.model.MqttVirtualScanningSourceModel
import kotlinx.android.synthetic.main.item_virtual_scanning_source.view.*

class VirtualScanningSourcesAdapter(
        private val sourceList: MutableList<BaseVirtualScanningSourceModel>,
        private val clickListener: OnListItemClickListener?
) : RecyclerView.Adapter<VirtualScanningSourcesAdapter.ViewHolder>() {
    private var checkedChangeListener: OnCheckedChangeListener? = null


    fun setOnCheckedChangeListener(checkedChangeListener: OnCheckedChangeListener?) {
        this.checkedChangeListener = checkedChangeListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_virtual_scanning_source, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = sourceList[position]
        holder.nameView.text = item.name
        holder.enableView.isChecked = item.enabled

        when (item) {
            is MqttVirtualScanningSourceModel -> holder.imageView.setImageResource(R.drawable.mqtt_logo)
        }

        with(holder.rootView) {
            tag = item
            setOnClickListener{
                clickListener?.onListItemClick(item)
            }
        }

        holder.enableView.setOnCheckedChangeListener { _, isChecked ->
            checkedChangeListener?.onCheckedChange(isChecked, holder.bindingAdapterPosition)
        }
    }

    override fun getItemCount(): Int = sourceList.size

    fun getSourceModel(position: Int) = sourceList[position]

    fun removeSourceModel(position: Int) {
        sourceList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addSourceModelAtPosition(sourceModel: BaseVirtualScanningSourceModel, position: Int) {
        sourceList.add(position, sourceModel)
        notifyItemInserted(position)
    }


    inner class ViewHolder(val rootView: View) : RecyclerView.ViewHolder(rootView) {
        val nameView: TextView = rootView.virtual_scanning_source_name
        val enableView: Switch = rootView.virtual_scanning_source_switch
        val imageView: ImageView = rootView.virtual_scanning_source_image

        override fun toString(): String {
            return super.toString() + " '" + enableView.text + "'"
        }
    }

    interface OnListItemClickListener {
        fun onListItemClick(item: BaseVirtualScanningSourceModel?)
    }

    interface OnCheckedChangeListener {
        fun onCheckedChange(checked: Boolean, position: Int)
    }
}