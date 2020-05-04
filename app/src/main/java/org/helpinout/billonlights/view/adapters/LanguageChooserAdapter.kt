package org.helpinout.billonlights.view.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.helpinout.billonlights.R
import org.helpinout.billonlights.databinding.ItemLanguageBinding
import org.helpinout.billonlights.model.database.entity.LanguageItem
import org.helpinout.billonlights.utils.inflate


class LanguageChooserAdapter(var languageList: ArrayList<LanguageItem>, private val onItemClick: (LanguageItem) -> Unit) : RecyclerView.Adapter<LanguageChooserAdapter.LanguageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val viewLayout: ItemLanguageBinding = parent.inflate(R.layout.item_language)
        return LanguageViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val labguageItem = languageList[position]
        holder.item.item = labguageItem
        holder.itemView.setOnClickListener {
            onItemClick(labguageItem)
        }
    }
    
    override fun getItemCount(): Int {
        return languageList.size
    }

    inner class LanguageViewHolder internal constructor(val item: ItemLanguageBinding) : RecyclerView.ViewHolder(item.root)

}
