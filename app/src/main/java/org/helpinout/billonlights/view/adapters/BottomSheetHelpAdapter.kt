package org.helpinout.billonlights.view.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.bottom_sheet_item.view.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.databinding.BottomSheetItemBinding
import org.helpinout.billonlights.model.database.entity.ActivityAddDetail
import org.helpinout.billonlights.utils.inflate


class BottomSheetHelpAdapter(private var homeItemList: ArrayList<ActivityAddDetail>) : RecyclerView.Adapter<BottomSheetHelpAdapter.BottomSheetViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetViewHolder {
        val viewLayout: BottomSheetItemBinding = parent.inflate(R.layout.bottom_sheet_item)
        return BottomSheetViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: BottomSheetViewHolder, position: Int) {
        val homeItem = homeItemList[position]
        holder.item.item = homeItem.user_detail
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


    inner class BottomSheetViewHolder internal constructor(val item: BottomSheetItemBinding) : RecyclerView.ViewHolder(item.root)

}
