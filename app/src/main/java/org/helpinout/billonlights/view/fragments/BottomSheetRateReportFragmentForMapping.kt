package org.helpinout.billonlights.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_rate_report.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.MappingDetail
import org.helpinout.billonlights.utils.HELP_TYPE_OFFER
import org.helpinout.billonlights.utils.HELP_TYPE_REQUEST
import org.helpinout.billonlights.utils.hide


class BottomSheetRateReportFragmentForMapping(val item: MappingDetail, private val onSubmitClick: (MappingDetail, String, Int, String) -> Unit) : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, if (item.activity_type == HELP_TYPE_REQUEST) R.style.BottomSheetThemeAskForHelp else R.style.BottomSheetThemeOfferHelp)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_rate_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (item.activity_type == HELP_TYPE_OFFER) {
            tv_message.text = getString(R.string.should_othe_help_them)
            tv_comment.hide()
            edt_comment.hide()
        }

        iv_expend_collapse.setOnClickListener {
            dismiss()
        }
        button_submit.setOnClickListener {
            onSubmitClick(item, rating_bar.rating.toString(), if (rb_yes.isChecked) 1 else 0, edt_comment.text.toString())
            dismiss()
        }
        tv_name.text = item.profile_name
    }

}