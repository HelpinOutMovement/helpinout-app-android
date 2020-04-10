package org.helpinout.billonlights.view.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.helpinout.billonlights.R
import org.helpinout.billonlights.databinding.BottomSheetItemBinding
import org.helpinout.billonlights.model.database.entity.BottomHelp
import org.helpinout.billonlights.utils.inflate


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
