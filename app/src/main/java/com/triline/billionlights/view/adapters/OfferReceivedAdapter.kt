package com.triline.billionlights.view.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.triline.billionlights.R
import com.triline.billionlights.databinding.ItemOfferReceivedBinding
import com.triline.billionlights.model.database.entity.OfferReceived
import com.triline.billionlights.utils.callPhoneNumber
import com.triline.billionlights.utils.inflate
import kotlinx.android.synthetic.main.item_offer_received.view.*


class OfferReceivedAdapter(private var offerList: ArrayList<OfferReceived>, private val onRateReportClick: (OfferReceived) -> Unit, private val onDeleteClick: (OfferReceived) -> Unit) : RecyclerView.Adapter<OfferReceivedAdapter.OfferReceivedViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferReceivedViewHolder {
        val viewLayout: ItemOfferReceivedBinding = parent.inflate(R.layout.item_offer_received)
        return OfferReceivedViewHolder(viewLayout)

    }

    override fun onBindViewHolder(holder: OfferReceivedViewHolder, position: Int) {
        val item = offerList[position]
        holder.item.item = item

        holder.itemView.tv_rate_report.setOnClickListener {
            onRateReportClick(item)
        }
        holder.itemView.tv_delete.setOnClickListener {
            onDeleteClick(item)
        }
        holder.itemView.iv_call.setOnClickListener {
            if (item.mobile.isNotEmpty()) {
                holder.itemView.context.callPhoneNumber(item.mobile)
            }
        }

    }

    override fun getItemCount(): Int {
        return offerList.size
    }


    inner class OfferReceivedViewHolder internal constructor(val item: ItemOfferReceivedBinding) : RecyclerView.ViewHolder(item.root)

}
