package com.triline.billionlights.view.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.triline.billionlights.R
import com.triline.billionlights.databinding.ItemOfferHelpBinding
import com.triline.billionlights.model.database.entity.OfferHelpItem
import com.triline.billionlights.utils.inflate


class OfferHelpAdapter(
    var homeItemList: ArrayList<OfferHelpItem>,
    private val onItemClick: (OfferHelpItem) -> Unit
) : RecyclerView.Adapter<OfferHelpAdapter.OfferHelpViewHolder>() {
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


    inner class OfferHelpViewHolder internal constructor(val item: ItemOfferHelpBinding) :
        RecyclerView.ViewHolder(item.root)

}
