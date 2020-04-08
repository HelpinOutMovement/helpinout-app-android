package com.triline.billionlights.view.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.triline.billionlights.R
import com.triline.billionlights.databinding.ItemLanguageBinding
import com.triline.billionlights.model.database.entity.LanguageItem
import com.triline.billionlights.utils.inflate


class LanguageChooserAdapter(
    var languageList: ArrayList<LanguageItem>,
    private val onItemClick: (LanguageItem) -> Unit
) : RecyclerView.Adapter<LanguageChooserAdapter.LanguageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val viewLayout: ItemLanguageBinding = parent.inflate(R.layout.item_language)
        return LanguageViewHolder(viewLayout)

    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val homeItem = languageList[position]
        holder.item.item = homeItem
        holder.itemView.setOnClickListener {
            onItemClick(homeItem)
        }

    }

    override fun getItemCount(): Int {
        return languageList.size
    }


    inner class LanguageViewHolder internal constructor(val item: ItemLanguageBinding) :
        RecyclerView.ViewHolder(item.root)

}
