package org.helpinout.billonlights.view.adapters

import android.os.Build
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_request_detail.view.tv_detail
import kotlinx.android.synthetic.main.item_request_detail.view.tv_rate_report
import kotlinx.android.synthetic.main.item_request_sent.view.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.databinding.ItemRequestSentBinding
import org.helpinout.billonlights.model.database.entity.AddCategoryDbItem
import org.helpinout.billonlights.utils.*


class RequestSentAdapter(private var offerType: Int, private var initiator: Int, private var helpType: Int, private var requestSentList: ArrayList<AddCategoryDbItem>, private val onRateReportClick: (AddCategoryDbItem) -> Unit, private val onSendRequestClick: (Int, Int, Int, AddCategoryDbItem) -> Unit, private val onOffersClick: (Int, Int, Int, AddCategoryDbItem) -> Unit) : RecyclerView.Adapter<RequestSentAdapter.RequestSentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestSentViewHolder {
        val viewLayout: ItemRequestSentBinding = parent.inflate(R.layout.item_request_sent)
        return RequestSentViewHolder(viewLayout, offerType)
    }

    override fun onBindViewHolder(holder: RequestSentViewHolder, position: Int) {
        val item = requestSentList[position]
        holder.item.item = item
        holder.itemView.count_text_view.goneIf(item.totalOffers == 0)
        holder.itemView.tv_time.text = item.date_time.displayTime()
        holder.itemView.tv_detail.text = item.detail?.fromHtml()

        if (item.activity_type == HELP_TYPE_REQUEST) {
            holder.itemView.tv_rate_report.setText(R.string.search_for_help_provider)
            holder.itemView.count_text_view.text = holder.itemView.context.getString(R.string.total_offers, item.totalOffers)
        } else {
            holder.itemView.count_text_view.setBackgroundResource(R.drawable.rounded_text_view_accent)
            holder.itemView.tv_rate_report.setText(R.string.search_for_help_requester)
            holder.itemView.count_text_view.text = holder.itemView.context.getString(R.string.total_requests, item.totalOffers)
            holder.itemView.tv_send_requests.text = holder.itemView.tv_send_requests.context.getString(R.string.sent_offers)
        }
        holder.itemView.count_text_view.setOnClickListener {
            onOffersClick(offerType, initiator, helpType, item)
        }

        holder.itemView.tv_rate_report.setOnClickListener {
            onRateReportClick(item)
        }

        holder.itemView.tv_send_requests.setOnClickListener {
            onSendRequestClick(offerType, initiator, helpType, item)
        }
    }

    override fun getItemCount(): Int {
        return requestSentList.size
    }

    inner class RequestSentViewHolder internal constructor(val item: ItemRequestSentBinding, val offerType: Int) : RecyclerView.ViewHolder(item.root) {
        init {
            if (offerType == HELP_TYPE_OFFER) {
                if (Build.VERSION.SDK_INT >= 28) {
                    itemView.main_card.outlineAmbientShadowColor = itemView.main_card.context.getColor(R.color.colorAccent)
                    itemView.main_card.outlineSpotShadowColor = itemView.main_card.context.getColor(R.color.colorAccent)
                }
            }
        }
    }
}
