package org.helpinout.billonlights.view.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.bottom_sheet_item.view.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.databinding.BottomSheetItemBinding
import org.helpinout.billonlights.model.database.entity.ActivityAddDetail
import org.helpinout.billonlights.utils.HELP_TYPE_REQUEST
import org.helpinout.billonlights.utils.Utils.Companion.timeAgo
import org.helpinout.billonlights.utils.fromHtml
import org.helpinout.billonlights.utils.inflate


class BottomSheetHelpAdapter(private var appDetailItems: ArrayList<ActivityAddDetail>) : RecyclerView.Adapter<BottomSheetHelpAdapter.BottomSheetViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetViewHolder {
        val viewLayout: BottomSheetItemBinding = parent.inflate(R.layout.bottom_sheet_item)
        return BottomSheetViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: BottomSheetViewHolder, position: Int) {
        val homeItem = appDetailItems[position]
        holder.item.item = homeItem.user_detail
        holder.itemView.tv_name.text = homeItem.user_detail?.first_name + " " + homeItem.user_detail?.last_name
        holder.itemView.distance.text = timeAgo(homeItem.date_time ?: "", holder.itemView.context) + "  |  " + holder.itemView.context.getString(R.string.distance_km, homeItem.user_detail!!.distance)

        holder.itemView.tv_name.isChecked = homeItem.isSelected
        if (homeItem.user_detail?.rating_count != 0) {
            holder.itemView.rating_bar.rating = homeItem.user_detail?.rating_avg ?: 0F
        }

        if (homeItem.activity_type == HELP_TYPE_REQUEST) {
            if (!homeItem.user_detail?.detail.isNullOrEmpty()) holder.itemView.tv_message.text = (holder.itemView.context.getString(R.string.need_help_with) + "<br/>" + homeItem.user_detail?.detail).fromHtml()
        } else {
            if (!homeItem.user_detail?.detail.isNullOrEmpty()) holder.itemView.tv_message.text = (holder.itemView.context.getString(R.string.can_help_with) + "<br/>" + homeItem.user_detail?.detail).fromHtml()
        }
        holder.itemView.tv_name.setOnClickListener {
            homeItem.isSelected = !homeItem.isSelected
        }
    }

    override fun getItemCount(): Int {
        return appDetailItems.size
    }

    fun getCheckedItemsList(): List<ActivityAddDetail> {
        return appDetailItems.filter { it.isSelected }
    }


    inner class BottomSheetViewHolder internal constructor(val item: BottomSheetItemBinding) : RecyclerView.ViewHolder(item.root)

}
