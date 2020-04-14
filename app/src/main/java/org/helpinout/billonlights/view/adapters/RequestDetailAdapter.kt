package org.helpinout.billonlights.view.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_request_detail.view.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.databinding.ItemRequestDetailBinding
import org.helpinout.billonlights.model.database.entity.MappingDetail
import org.helpinout.billonlights.utils.*


class RequestDetailAdapter(private var helpType: Int, private var offerList: ArrayList<MappingDetail>, private val onRateReportClick: (MappingDetail) -> Unit, private val onDeleteClick: (MappingDetail) -> Unit, private val onDetailClick: (String, String) -> Unit) : RecyclerView.Adapter<RequestDetailAdapter.OfferReceivedViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferReceivedViewHolder {
        val viewLayout: ItemRequestDetailBinding = parent.inflate(R.layout.item_request_detail)
        return OfferReceivedViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: OfferReceivedViewHolder, position: Int) {
        val item = offerList[position]
        holder.item.item = item

        holder.itemView.tv_time.text = item.date_time?.displayTime() ?: ""
        holder.itemView.tv_name.text = item.first_name + " " + item.last_name
        holder.itemView.rating_bar.goneIf(item.rating_count == 0)
        holder.itemView.rating_bar.rating = item.rating_avg ?: 0.0F


        if (item.activity_type == HELP_TYPE_REQUEST) {
            if (item.activity_type == item.request_mapping_initiator) {//Requests have been sent to
                val text = holder.itemView.context.getString(R.string.request_help_text)
                holder.itemView.tv_detail.text = text
            } else {//Help offers received from
                val text = holder.itemView.context.getString(R.string.request_offer_receive_text)
                holder.itemView.tv_detail.text = text
                holder.itemView.tv_detail.setTextColor(holder.itemView.tv_detail.context.getColor(R.color.colorPrimary))
                holder.itemView.iv_call.show()
                holder.itemView.tv_cal_them.show()
                holder.itemView.tv_detail.setOnClickListener {
                    onDetailClick(item.first_name + " " + item.last_name, item.offer_condition ?: "")
                }
            }

        } else {
            if (item.activity_type == item.request_mapping_initiator) {//Offers have been sent to
                val text = holder.itemView.context.getString(R.string.offer_help_text)
                holder.itemView.tv_detail.text = text
            } else {//Help requests received from
                val text = holder.itemView.context.getString(R.string.offer_help_text)
                holder.itemView.tv_detail.text = text
                holder.itemView.tv_detail.setTextColor(holder.itemView.tv_detail.context.getColor(R.color.colorAccent))
                holder.itemView.iv_call.show()
                holder.itemView.tv_cal_them.show()
            }
        }

        holder.itemView.tv_rate_report.setOnClickListener {
            onRateReportClick(item)
        }
        holder.itemView.tv_delete.setOnClickListener {
            onDeleteClick(item)
        }
        holder.itemView.iv_call.setOnClickListener {
            if (item.mobile_no.isNotEmpty()) {
                holder.itemView.context.callPhoneNumber(item.mobile_no)
            }
        }
    }

    override fun getItemCount(): Int {
        return offerList.size
    }


    inner class OfferReceivedViewHolder internal constructor(val item: ItemRequestDetailBinding) : RecyclerView.ViewHolder(item.root)

}
