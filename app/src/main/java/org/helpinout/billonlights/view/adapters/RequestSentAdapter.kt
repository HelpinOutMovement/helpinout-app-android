package org.helpinout.billonlights.view.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_offer_received.view.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.databinding.ItemRequestSentBinding
import org.helpinout.billonlights.model.database.entity.AddCategoryDbItem
import org.helpinout.billonlights.utils.*


class RequestSentAdapter(private var requestSentList: ArrayList<AddCategoryDbItem>, private val onRateReportClick: (AddCategoryDbItem) -> Unit, private val onDeleteClick: (AddCategoryDbItem) -> Unit) : RecyclerView.Adapter<RequestSentAdapter.RequestSentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestSentViewHolder {
        val viewLayout: ItemRequestSentBinding = parent.inflate(R.layout.item_request_sent)
        return RequestSentViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: RequestSentViewHolder, position: Int) {
        val item = requestSentList[position]
        holder.item.item = item

        when (item.activity_category) {

            CATEGORY_FOOD -> {
                if (item.activity_type == HELP_TYPE_REQUEST) {
                    if (!item.parent_uuid.isNullOrEmpty()) {
                        val text = holder.itemView.context.getString(R.string.mapping_template, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    } else {
                        item.isMappingExist?.let {
                            if (it) holder.itemView.tv_rate_report.hide()
                        }
                        holder.itemView.tv_rate_report.setText(R.string.search_for_help_provider)
                        val text = holder.itemView.context.getString(R.string.food_template_request, item.detail, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    }
                } else {
                    if (!item.parent_uuid.isNullOrEmpty()) {
                        val text = holder.itemView.context.getString(R.string.mapping_template, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    } else {
                        holder.itemView.tv_rate_report.setText(R.string.search_for_help_requester)
                        val text = holder.itemView.context.getString(R.string.food_template_offer, item.detail, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    }
                }
            }
            CATEGORY_PEOPLE -> {
                if (item.activity_type == HELP_TYPE_REQUEST) {
                    if (!item.parent_uuid.isNullOrEmpty()) {
                        val text = holder.itemView.context.getString(R.string.mapping_template, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    } else {
                        item.isMappingExist?.let {
                            if (it) holder.itemView.tv_rate_report.hide()
                        }
                        holder.itemView.tv_rate_report.setText(R.string.search_for_help_provider)
                        val text = holder.itemView.context.getString(R.string.people_template_request, item.detail, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    }

                } else {
                    if (!item.parent_uuid.isNullOrEmpty()) {
                        val text = holder.itemView.context.getString(R.string.mapping_template, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    } else {
                        item.isMappingExist?.let {
                            if (it) holder.itemView.tv_rate_report.hide()
                        }
                        holder.itemView.tv_rate_report.setText(R.string.search_for_help_requester)
                        val text = holder.itemView.context.getString(R.string.people_template_offer, item.detail, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    }

                }
            }
            CATEGORY_SHELTER -> {
                if (item.activity_type == HELP_TYPE_REQUEST) {
                    if (!item.parent_uuid.isNullOrEmpty()) {
                        val text = holder.itemView.context.getString(R.string.mapping_template, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    } else {
                        item.isMappingExist?.let {
                            if (it) holder.itemView.tv_rate_report.hide()
                        }
                        holder.itemView.tv_rate_report.setText(R.string.search_for_help_provider)
                        val text = holder.itemView.context.getString(R.string.shelter_template_request, item.detail, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    }

                } else {
                    if (!item.parent_uuid.isNullOrEmpty()) {
                        val text = holder.itemView.context.getString(R.string.mapping_template, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    } else {
                        item.isMappingExist?.let {
                            if (it) holder.itemView.tv_rate_report.hide()
                        }
                        holder.itemView.tv_rate_report.setText(R.string.search_for_help_requester)
                        val text = holder.itemView.context.getString(R.string.shelter_template_offer, item.detail, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    }
                }
            }
            CATEGORY_MED_PPE -> {
                if (item.activity_type == HELP_TYPE_REQUEST) {
                    if (!item.parent_uuid.isNullOrEmpty()) {
                        val text = holder.itemView.context.getString(R.string.mapping_template, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    } else {
                        item.isMappingExist?.let {
                            if (it) holder.itemView.tv_rate_report.hide()
                        }
                        holder.itemView.tv_rate_report.setText(R.string.search_for_help_provider)
                        val text = holder.itemView.context.getString(R.string.med_ppe_template_request, item.detail, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    }

                } else {
                    if (!item.parent_uuid.isNullOrEmpty()) {
                        val text = holder.itemView.context.getString(R.string.mapping_template, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    } else {
                        item.isMappingExist?.let {
                            if (it) holder.itemView.tv_rate_report.hide()
                        }
                        holder.itemView.tv_rate_report.setText(R.string.search_for_help_requester)
                        val text = holder.itemView.context.getString(R.string.med_ppe_template_offer, item.detail, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    }

                }
            }
            CATEGORY_TESTING -> {
                if (item.activity_type == HELP_TYPE_REQUEST) {
                    if (!item.parent_uuid.isNullOrEmpty()) {
                        val text = holder.itemView.context.getString(R.string.mapping_template, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    } else {
                        item.isMappingExist?.let {
                            if (it) holder.itemView.tv_rate_report.hide()
                        }
                        holder.itemView.tv_rate_report.setText(R.string.search_for_help_provider)
                        val text = holder.itemView.context.getString(R.string.testing_template_request, item.detail, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    }

                } else {
                    if (!item.parent_uuid.isNullOrEmpty()) {
                        val text = holder.itemView.context.getString(R.string.mapping_template, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    } else {
                        item.isMappingExist?.let {
                            if (it) holder.itemView.tv_rate_report.hide()
                        }
                        holder.itemView.tv_rate_report.setText(R.string.search_for_help_requester)
                        val text = holder.itemView.context.getString(R.string.testing_template_offer, item.detail, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    }

                }
            }
            CATEGORY_MEDICINES -> {
                if (item.activity_type == HELP_TYPE_REQUEST) {
                    if (!item.parent_uuid.isNullOrEmpty()) {
                        val text = holder.itemView.context.getString(R.string.mapping_template, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    } else {
                        item.isMappingExist?.let {
                            if (it) holder.itemView.tv_rate_report.hide()
                        }
                        holder.itemView.tv_rate_report.setText(R.string.search_for_help_provider)
                        val text = holder.itemView.context.getString(R.string.medicines_template_request, item.detail, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    }
                } else {
                    if (!item.parent_uuid.isNullOrEmpty()) {
                        val text = holder.itemView.context.getString(R.string.mapping_template, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    } else {
                        item.isMappingExist?.let {
                            if (it) holder.itemView.tv_rate_report.hide()
                        }
                        holder.itemView.tv_rate_report.setText(R.string.search_for_help_requester)
                        val text = holder.itemView.context.getString(R.string.medicines_template_offer, item.detail, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    }
                }
            }
            CATEGORY_AMBULANCE -> {
                if (item.activity_type == HELP_TYPE_REQUEST) {
                    if (!item.parent_uuid.isNullOrEmpty()) {
                        val text = holder.itemView.context.getString(R.string.mapping_template, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    } else {
                        item.isMappingExist?.let {
                            if (it) holder.itemView.tv_rate_report.hide()
                        }
                        holder.itemView.tv_rate_report.setText(R.string.search_for_help_provider)
                        val text = holder.itemView.context.getString(R.string.ambulance_template_request, item.qty.toString(), item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    }

                } else {
                    if (!item.parent_uuid.isNullOrEmpty()) {
                        val text = holder.itemView.context.getString(R.string.mapping_template, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    } else {
                        item.isMappingExist?.let {
                            if (it) holder.itemView.tv_rate_report.hide()
                        }
                        holder.itemView.tv_rate_report.setText(R.string.search_for_help_requester)
                        val text = holder.itemView.context.getString(R.string.ambulance_template_offer, item.qty.toString(), item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    }
                }
            }
            CATEGORY_MEDICAL_EQUIPMENT -> {
                if (item.activity_type == HELP_TYPE_REQUEST) {
                    if (!item.parent_uuid.isNullOrEmpty()) {
                        val text = holder.itemView.context.getString(R.string.mapping_template, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    } else {
                        item.isMappingExist?.let {
                            if (it) holder.itemView.tv_rate_report.hide()
                        }
                        holder.itemView.tv_rate_report.setText(R.string.search_for_help_provider)
                        val text = holder.itemView.context.getString(R.string.medical_equipment_template_request, item.detail, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    }
                } else {
                    if (!item.parent_uuid.isNullOrEmpty()) {
                        val text = holder.itemView.context.getString(R.string.mapping_template, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    } else {
                        item.isMappingExist?.let {
                            if (it) holder.itemView.tv_rate_report.hide()
                        }
                        holder.itemView.tv_rate_report.setText(R.string.search_for_help_requester)
                        val text = holder.itemView.context.getString(R.string.medical_equipment_template_offer, item.detail, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    }
                }
            }
            CATEGORY_OTHERS -> {
                if (item.activity_type == HELP_TYPE_REQUEST) {
                    if (!item.parent_uuid.isNullOrEmpty()) {
                        val text = holder.itemView.context.getString(R.string.mapping_template, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    } else {
                        item.isMappingExist?.let {
                            if (it) holder.itemView.tv_rate_report.hide()
                        }
                        holder.itemView.tv_rate_report.setText(R.string.search_for_help_provider)
                        val text = holder.itemView.context.getString(R.string.other_thing_template_request, item.detail, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    }

                } else {
                    if (!item.parent_uuid.isNullOrEmpty()) {
                        val text = holder.itemView.context.getString(R.string.mapping_template, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    } else {
                        item.isMappingExist?.let {
                            if (it) holder.itemView.tv_rate_report.hide()
                        }
                        holder.itemView.tv_rate_report.setText(R.string.search_for_help_requester)
                        val text = holder.itemView.context.getString(R.string.other_thing_template_offer, item.detail, item.date_time.displayTime())
                        holder.itemView.tv_detail.text = text
                    }
                }
            }
            else -> {
                holder.itemView.tv_detail.text = item.detail
            }
        }

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
