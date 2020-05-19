package org.helpinout.billonlights.view.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.helpinout.billonlights.R
import org.helpinout.billonlights.databinding.ItemAskForHelpBinding
import org.helpinout.billonlights.model.database.entity.OfferHelpItem
import org.helpinout.billonlights.utils.inflate


class AskForHelpAdapter(var selfElse: Int, var itemList: ArrayList<OfferHelpItem>, private val onItemClick: (OfferHelpItem, Int) -> Unit) : RecyclerView.Adapter<AskForHelpAdapter.AskForHelpViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AskForHelpViewHolder {
        val viewLayout: ItemAskForHelpBinding = parent.inflate(R.layout.item_ask_for_help)
        return AskForHelpViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: AskForHelpViewHolder, position: Int) {
        val item = itemList[position]
        holder.item.item = item
        holder.item.mainItem.setOnClickListener {
            onItemClick(item, selfElse)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class AskForHelpViewHolder internal constructor(val item: ItemAskForHelpBinding) : RecyclerView.ViewHolder(item.root)
}
