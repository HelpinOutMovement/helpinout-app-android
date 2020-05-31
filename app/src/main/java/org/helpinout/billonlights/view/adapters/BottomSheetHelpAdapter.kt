package org.helpinout.billonlights.view.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.bottom_sheet_item.view.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.databinding.BottomSheetItemBinding
import org.helpinout.billonlights.model.database.entity.ActivityAddDetail
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.utils.Utils.Companion.timeAgo


class BottomSheetHelpAdapter(private var appDetailItems: ArrayList<ActivityAddDetail>, private val onViewDetailClick: (ActivityAddDetail) -> Unit) : RecyclerView.Adapter<BottomSheetHelpAdapter.BottomSheetViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetViewHolder {
        val viewLayout: BottomSheetItemBinding = parent.inflate(R.layout.bottom_sheet_item)
        return BottomSheetViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: BottomSheetViewHolder, position: Int) {
        val homeItem = appDetailItems[position]
        holder.item.item = homeItem.user_detail
        val context = holder.itemView.context
        holder.itemView.tv_name.text = homeItem.user_detail?.profile_name
        holder.itemView.distance.text = timeAgo(homeItem.date_time ?: "", holder.itemView.context) + "  |  " + context.getString(R.string.distance_km, homeItem.user_detail!!.distance)

        holder.itemView.tv_name.isChecked = homeItem.isSelected
        holder.itemView.tv_name.isEnabled = homeItem.isEnable
        if (homeItem.user_detail?.rating_count != 0) {
            holder.itemView.rating_bar.rating = homeItem.user_detail?.rating_avg ?: 0F
            holder.itemView.rating_bar.jumpDrawablesToCurrentState()
        }

        if (homeItem.activity_type == HELP_TYPE_REQUEST) {
            if (!homeItem.user_detail?.detail.isNullOrEmpty()) holder.itemView.tv_message.text = (context.getString(R.string.need_help_with) + "<br/>(" + context.getString(homeItem.activity_category.getName())+")" + "<br/>" + homeItem.user_detail?.detail).fromHtml()
            if (homeItem.activity_category == CATEGORY_MEDICAL_PAID_WORK) {
                holder.itemView.free_or_paid.text = context.getString(R.string.must_get_paid)
            } else holder.itemView.free_or_paid.text = context.getString(if (homeItem.pay == 1) R.string.can_pay else R.string.can_not_pay)
            holder.itemView.tv_self_help.visibleIf(homeItem.self_else == 2)
        } else {
            if (!homeItem.user_detail?.detail.isNullOrEmpty()) holder.itemView.tv_message.text = (context.getString(R.string.can_help_with) + "<br/>(" + context.getString(homeItem.activity_category.getName())+")" + "<br/>" + homeItem.user_detail?.detail).fromHtml()
            if (homeItem.activity_category == CATEGORY_MEDICAL_PAID_WORK) {
                holder.itemView.free_or_paid.text = context.getString(R.string.we_will_pay)
            } else
            holder.itemView.free_or_paid.text = context.getString(if (homeItem.pay == 1) R.string.not_free else R.string.free)
            holder.itemView.tv_notes.show()
            holder.itemView.tv_notes.text = ("<b>" + context.getString(R.string.note) + "</b> " + homeItem.offer_note).fromHtml()
        }
        holder.itemView.tv_name.setOnClickListener {
            homeItem.isSelected = !homeItem.isSelected
        }
        holder.itemView.tv_view_detail.setOnClickListener {
            onViewDetailClick(homeItem)
        }
    }

    override fun getItemCount(): Int {
        return appDetailItems.size
    }

    fun getCheckedItemsList(): List<ActivityAddDetail> {
        return appDetailItems.filter { it.isSelected }
    }

    fun isAllItemChecked(): Boolean {
        val items = appDetailItems.filter { !it.isSelected }
        return items.isEmpty()
    }

    fun toggleCheckBox(checked: Boolean) {
        appDetailItems.forEach {
            it.isSelected = checked
            it.isEnable = !checked
        }

        notifyDataSetChanged()
    }


    inner class BottomSheetViewHolder internal constructor(val item: BottomSheetItemBinding) : RecyclerView.ViewHolder(item.root)

}
