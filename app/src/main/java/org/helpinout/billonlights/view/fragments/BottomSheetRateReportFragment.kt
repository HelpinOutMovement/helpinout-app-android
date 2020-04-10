package org.helpinout.billonlights.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_rate_report.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.AddItem

class BottomSheetRateReportFragment(val item: AddItem, val isRequestSend: Boolean) : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_rate_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_expend_collapse.setOnClickListener {
            dismiss()
        }
        tv_name.text = item.name

    }


}