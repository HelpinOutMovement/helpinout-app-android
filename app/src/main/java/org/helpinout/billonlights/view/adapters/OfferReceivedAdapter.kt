package org.helpinout.billonlights.view.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_offer_received.view.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.databinding.ItemOfferReceivedBinding
import org.helpinout.billonlights.model.database.entity.OfferReceived
import org.helpinout.billonlights.utils.callPhoneNumber
import org.helpinout.billonlights.utils.inflate


class OfferReceivedAdapter(private var helpType: Int, private var offerList: ArrayList<OfferReceived>, private val onRateReportClick: (OfferReceived) -> Unit, private val onDeleteClick: (OfferReceived) -> Unit) : RecyclerView.Adapter<OfferReceivedAdapter.OfferReceivedViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferReceivedViewHolder {
        val viewLayout: ItemOfferReceivedBinding = parent.inflate(R.layout.item_offer_received)
        return OfferReceivedViewHolder(viewLayout)

    }

    override fun onBindViewHolder(holder: OfferReceivedViewHolder, position: Int) {
        val item = offerList[position]
        holder.item.item = item

//        if (helpType == HELP_TYPE_REQUEST) {
//            holder.itemView.tv_rate_report.hide()
//            //val text = holder.itemView.context.getString(R.string.food_template, item.quantity.toString(), item.quantity.toString(), item.date_time.displayTime())
//            holder.itemView.tv_detail.text = text
//        }else{
        holder.itemView.tv_detail.text = item.detail
//        }

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
