//package org.helpinout.billonlights.view.adapters
//
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import kotlinx.android.synthetic.main.item_offer_received.view.*
//import org.helpinout.billonlights.R
//import org.helpinout.billonlights.databinding.ItemOfferReceivedBinding
//import org.helpinout.billonlights.model.database.entity.AddCategoryDbItem
//import org.helpinout.billonlights.utils.callPhoneNumber
//import org.helpinout.billonlights.utils.displayTime
//import org.helpinout.billonlights.utils.hide
//import org.helpinout.billonlights.utils.inflate
//
//
//class OfferReceivedAdapter(private var helpType: Int, private var offerList: ArrayList<AddCategoryDbItem>, private val onRateReportClick: (AddCategoryDbItem) -> Unit, private val onDeleteClick: (AddCategoryDbItem) -> Unit) : RecyclerView.Adapter<OfferReceivedAdapter.OfferReceivedViewHolder>() {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferReceivedViewHolder {
//        val viewLayout: ItemOfferReceivedBinding = parent.inflate(R.layout.item_offer_received)
//        return OfferReceivedViewHolder(viewLayout)
//
//    }
//
//    override fun onBindViewHolder(holder: OfferReceivedViewHolder, position: Int) {
//        val item = offerList[position]
//        holder.item.item = item
//
//        if (!item.parent_uuid.isNullOrEmpty()) {
//            val text = holder.itemView.context.getString(R.string.mapping_template, item.date_time.displayTime())
//            holder.itemView.tv_detail.text = text
//        } else {
//            item.isMappingExist?.let {
//                if (it) holder.itemView.tv_rate_report.hide()
//            }
//            holder.itemView.tv_rate_report.setText(R.string.search_for_help_provider)
//            val text = holder.itemView.context.getString(R.string.request_template, item.detail, item.date_time.displayTime())
//            holder.itemView.tv_detail.text = text
//        }
//        holder.itemView.tv_rate_report.setOnClickListener {
//            onRateReportClick(item)
//        }
//        holder.itemView.tv_delete.setOnClickListener {
//            onDeleteClick(item)
//        }
//        holder.itemView.iv_call.setOnClickListener {
//            if (!item.mobile_no.isNullOrEmpty()) {
//                holder.itemView.context.callPhoneNumber(item.mobile_no!!)
//            }
//        }
//
//    }
//
//    override fun getItemCount(): Int {
//        return offerList.size
//    }
//
//
//    inner class OfferReceivedViewHolder internal constructor(val item: ItemOfferReceivedBinding) : RecyclerView.ViewHolder(item.root)
//
//}
