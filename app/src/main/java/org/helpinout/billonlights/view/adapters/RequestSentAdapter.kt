package org.helpinout.billonlights.view.adapters

import android.os.Build
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_request_sent.view.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.databinding.ItemRequestSentBinding
import org.helpinout.billonlights.model.database.entity.AddCategoryDbItem
import org.helpinout.billonlights.utils.*


class RequestSentAdapter(private var offerType: Int, private var initiator: Int, private var helpType: Int, private var requestSentList: ArrayList<AddCategoryDbItem>,
    private val onSearchForHelpProviderClick: (AddCategoryDbItem) -> Unit,
    private val onViewDetailClick: (Int, AddCategoryDbItem) -> Unit, private val onNewMatchesClick: (Int, Int, Int, AddCategoryDbItem) -> Unit, private val onRequestSentClick: (Int, Int,Int, String) -> Unit) : RecyclerView.Adapter<RequestSentAdapter.RequestSentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestSentViewHolder {
        val viewLayout: ItemRequestSentBinding = parent.inflate(R.layout.item_request_sent)
        return RequestSentViewHolder(viewLayout, offerType)
    }

    override fun onBindViewHolder(holder: RequestSentViewHolder, position: Int) {
        val item = requestSentList[position]
        holder.item.item = item
        holder.itemView.tv_time.text = item.date_time.displayTime()

        holder.itemView.tv_offer_received_count.text = item.offersReceived?.toString() ?: "0"
        holder.itemView.tv_request_send_count.text = item.requestSent?.toString() ?: "0"

        holder.itemView.tv_new_matches.visibleIf(item.newMatchesCount != null && item.newMatchesCount!! > 0)
        val ctx = holder.itemView.context

        if (item.show_notification == 1 && holder.itemView.tv_offer_received_count.text.toString() != "0") {
            holder.itemView.tv_offer_received_count.setTextColor(ctx.getColor(R.color.colorPrimary))
        } else {
            holder.itemView.tv_offer_received_count.setTextColor(ctx.getColor(R.color.colorAccent))
        }


        if (item.activity_type == HELP_TYPE_REQUEST) {
            holder.itemView.tv_offer_received.text = ctx.getString(R.string.offers_received)

            holder.itemView.tv_request_sent.text = ctx.getString(R.string.requests_sent)
            holder.itemView.tv_search_for_help_providers.text= ctx.getString(R.string.search_for_help_givers)
        } else {

            holder.itemView.tv_offer_received.text = ctx.getString(R.string.requests_received)
            holder.itemView.tv_request_sent.text = ctx.getString(R.string.offer_sent)
            holder.itemView.tv_search_for_help_providers.text= ctx.getString(R.string.search_for_help_seeker)
        }

        holder.itemView.tv_offer_received.setOnClickListener {
            val initiat = if (initiator == 1) 2 else 1
            onRequestSentClick(offerType,initiat,helpType,item.activity_uuid)
        }
        holder.itemView.tv_request_sent.setOnClickListener {
            onRequestSentClick(offerType,initiator,helpType,item.activity_uuid)
        }
        holder.itemView.tv_new_matches.setOnClickListener {
            onNewMatchesClick(offerType, initiator, helpType, item)
        }

        holder.itemView.tv_search_for_help_providers.setOnClickListener {
            onSearchForHelpProviderClick(item)
        }

        holder.itemView.view_detail.setOnClickListener {
            onViewDetailClick(offerType, item)
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
