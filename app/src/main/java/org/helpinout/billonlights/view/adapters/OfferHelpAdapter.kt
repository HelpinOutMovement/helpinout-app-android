package org.helpinout.billonlights.view.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.helpinout.billonlights.R
import org.helpinout.billonlights.databinding.ItemOfferHelpBinding
import org.helpinout.billonlights.model.database.entity.OfferHelpItem
import org.helpinout.billonlights.utils.inflate


class OfferHelpAdapter(var homeItemList: ArrayList<OfferHelpItem>, private val onItemClick: (OfferHelpItem) -> Unit) : RecyclerView.Adapter<OfferHelpAdapter.OfferHelpViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferHelpViewHolder {
        val viewLayout: ItemOfferHelpBinding = parent.inflate(R.layout.item_offer_help)
        return OfferHelpViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: OfferHelpViewHolder, position: Int) {
        val homeItem = homeItemList[position]
        holder.item.item = homeItem
        holder.item.mainItem.setOnClickListener {
            onItemClick(homeItem)
        }
    }

    override fun getItemCount(): Int {
        return homeItemList.size
    }

    inner class OfferHelpViewHolder internal constructor(val item: ItemOfferHelpBinding) : RecyclerView.ViewHolder(item.root)
}
