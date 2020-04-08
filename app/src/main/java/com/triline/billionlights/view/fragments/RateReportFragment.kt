package com.triline.billionlights.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.triline.billionlights.R
import com.triline.billionlights.model.database.entity.Offer
import com.triline.billionlights.model.database.entity.OfferReceived
import com.triline.billionlights.model.database.entity.RequestSent
import kotlinx.android.synthetic.main.layout_rate_report.*

class RateReportFragment : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_rate_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_expend_collapse.setOnClickListener {
            dismiss()
        }

        arguments?.let {
            val isRequestSend = it.getBoolean("isRequestSend")
            val data = it.getString("RequestSent")!!
            if (isRequestSend) {
                val item = Gson().fromJson(data, RequestSent::class.java)
                tv_name.text = item.name
            } else {
                val item = Gson().fromJson(data, OfferReceived::class.java)
                tv_name.text = item.name
            }
        }
    }

    companion object {
        fun newInstance(item: Offer, isRequestSend: Boolean): RateReportFragment {
            val report = RateReportFragment()
            val args = Bundle()
            val data = Gson().toJson(item)
            args.putString("RequestSent", data)
            args.putBoolean("isRequestSend", isRequestSend)
            report.arguments = args
            return report
        }
    }
}