package org.helpinout.billonlights.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_rate_report.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.AddCategoryDbItem
import org.helpinout.billonlights.utils.HELP_TYPE_REQUEST

class BottomSheetRateReportFragment(val item: AddCategoryDbItem, private val onSubmitClick: (AddCategoryDbItem, String, Int, String) -> Unit) : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, if (item.activity_type == HELP_TYPE_REQUEST) R.style.BottomSheetThemeAskForHelp else R.style.BottomSheetThemeOfferHelp)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_rate_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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