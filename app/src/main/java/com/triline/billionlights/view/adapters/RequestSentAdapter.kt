package com.triline.billionlights.view.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.triline.billionlights.R
import com.triline.billionlights.databinding.ItemRequestSentBinding
import com.triline.billionlights.model.database.entity.RequestSent
import com.triline.billionlights.utils.inflate
import kotlinx.android.synthetic.main.item_offer_received.view.*


class RequestSentAdapter(private var requestSentList: ArrayList<RequestSent>, private val onRateReportClick: (RequestSent) -> Unit, private val onDeleteClick: (RequestSent) -> Unit) : RecyclerView.Adapter<RequestSentAdapter.RequestSentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestSentViewHolder {
        val viewLayout: ItemRequestSentBinding = parent.inflate(R.layout.item_request_sent)
        return RequestSentViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: RequestSentViewHolder, position: Int) {
        val item = requestSentList[position]
        holder.item.item = item

        holder.itemView.tv_rate_report.setOnClickListener {
            onRateReportClick(item)
        }
        holder.itemView.tv_delete.setOnClickListener {
            onDeleteClick(item)
        }

    }

    override fun getItemCount(): Int {
        return requestSentList.size
    }

    inner class RequestSentViewHolder internal constructor(val item: ItemRequestSentBinding) : RecyclerView.ViewHolder(item.root)
}
