package com.triline.billionlights.view.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.triline.billionlights.R
import com.triline.billionlights.databinding.BottomSheetItemBinding
import com.triline.billionlights.model.database.entity.BottomHelp
import com.triline.billionlights.utils.inflate


class BottomSheetHelpAdapter(var homeItemList: ArrayList<BottomHelp>, private val onItemClick: (BottomHelp) -> Unit) : RecyclerView.Adapter<BottomSheetHelpAdapter.OfferHelpViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferHelpViewHolder {
        val viewLayout: BottomSheetItemBinding = parent.inflate(R.layout.bottom_sheet_item)
        return OfferHelpViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: OfferHelpViewHolder, position: Int) {
        val homeItem = homeItemList[position]
        holder.item.item = homeItem
//        holder.item.mainItem.setOnClickListener {
//            onItemClick(homeItem)
//        }
    }

    override fun getItemCount(): Int {
        return homeItemList.size
    }


    inner class OfferHelpViewHolder internal constructor(val item: BottomSheetItemBinding) : RecyclerView.ViewHolder(item.root)

}
