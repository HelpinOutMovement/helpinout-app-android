package org.helpinout.billonlights.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_rate_report.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.AddCategoryDbItem

class BottomSheetRateReportFragment(val item: AddCategoryDbItem, private val onSubmitClick: (AddCategoryDbItem, String, Int, String) -> Unit) : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_rate_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        if (helpType == HELP_TYPE_OFFER) {
//            tv_title.setText(R.string.offer_confirmation)
//            button_yes.setBackgroundResource(R.drawable.accent_revert_border_background)
//            button_no.setBackgroundResource(R.drawable.accent_border_background)
//            button_no.setTextColor(resources.getColor(R.color.colorAccent))
//        }
        iv_expend_collapse.setOnClickListener {
            dismiss()
        }
        button_submit.setOnClickListener {
            onSubmitClick(item, rating_bar.rating.toString(), if (rb_yes.isChecked) 1 else 0, edt_comment.text.toString())
            dismiss()
        }
        tv_name.text = item.name

    }


}