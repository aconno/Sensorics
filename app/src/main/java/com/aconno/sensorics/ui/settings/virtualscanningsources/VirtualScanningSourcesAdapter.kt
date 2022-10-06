package com.aconno.sensorics.ui.settings.virtualscanningsources

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aconno.sensorics.R
import com.aconno.sensorics.databinding.ItemVirtualScanningSourceBinding
import com.aconno.sensorics.model.BaseVirtualScanningSourceModel
import com.aconno.sensorics.model.MqttVirtualScanningSourceModel

class VirtualScanningSourcesAdapter(
    private val sourceList: MutableList<BaseVirtualScanningSourceModel>,
    private val clickListener: OnListItemClickListener?
) : RecyclerView.Adapter<VirtualScanningSourcesAdapter.ViewHolder>() {
    private var checkedChangeListener: OnCheckedChangeListener? = null


    fun setOnCheckedChangeListener(checkedChangeListener: OnCheckedChangeListener?) {
        this.checkedChangeListener = checkedChangeListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemVirtualScanningSourceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = sourceList[position]
        holder.nameView.text = item.name
        holder.enableView.isChecked = item.enabled

        when (item) {
            is MqttVirtualScanningSourceModel -> holder.imageView.setImageResource(R.drawable.mqtt_logo)
        }

        with(holder.binding.root) {
            tag = item
            setOnClickListener {
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


    inner class ViewHolder(val binding: ItemVirtualScanningSourceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val nameView: TextView = binding.virtualScanningSourceName
        val enableView: Switch = binding.virtualScanningSourceSwitch
        val imageView: ImageView = binding.virtualScanningSourceImage

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