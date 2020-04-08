package com.triline.billionlights.view.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.triline.billionlights.R
import com.triline.billionlights.databinding.ItemAskForHelpBinding
import com.triline.billionlights.model.database.entity.AskForHelpItem
import com.triline.billionlights.utils.inflate


class AskForHelpAdapter(
    var homeItemList: ArrayList<AskForHelpItem>,
    private val onItemClick: (AskForHelpItem) -> Unit
) : RecyclerView.Adapter<AskForHelpAdapter.AskForHelpViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AskForHelpViewHolder {
        val viewLayout: ItemAskForHelpBinding = parent.inflate(R.layout.item_ask_for_help)
        return AskForHelpViewHolder(viewLayout)

    }

    override fun onBindViewHolder(holder: AskForHelpViewHolder, position: Int) {
        val homeItem = homeItemList[position]
        holder.item.item = homeItem
        holder.item.mainItem.setOnClickListener {
            onItemClick(homeItem)
        }

    }

    override fun getItemCount(): Int {
        return homeItemList.size
    }


    inner class AskForHelpViewHolder internal constructor(val item: ItemAskForHelpBinding) :
        RecyclerView.ViewHolder(item.root)

}
