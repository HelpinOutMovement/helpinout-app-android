package org.helpinout.billonlights.view.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.bottom_sheet_item.view.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.databinding.BottomSheetItemBinding
import org.helpinout.billonlights.model.database.entity.ActivityAddDetail
import org.helpinout.billonlights.utils.inflate


class BottomSheetHelpAdapter(var homeItemList: ArrayList<ActivityAddDetail>, private val onItemClick: (ActivityAddDetail) -> Unit) : RecyclerView.Adapter<BottomSheetHelpAdapter.OfferHelpViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferHelpViewHolder {
        val viewLayout: BottomSheetItemBinding = parent.inflate(R.layout.bottom_sheet_item)
        return OfferHelpViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: OfferHelpViewHolder, position: Int) {
        val homeItem = homeItemList[position]
        holder.item.item = homeItem.app_user_detail
        holder.itemView.tv_message.text = homeItem.offer_condition
        holder.itemView.tv_name.setOnClickListener {
            homeItem.isSelected = !homeItem.isSelected
        }
    }

    override fun getItemCount(): Int {
        return homeItemList.size
    }

    fun getCheckedItemsList(): List<ActivityAddDetail> {
        return homeItemList.filter { it.isSelected }
    }


    inner class OfferHelpViewHolder internal constructor(val item: BottomSheetItemBinding) : RecyclerView.ViewHolder(item.root)

}
