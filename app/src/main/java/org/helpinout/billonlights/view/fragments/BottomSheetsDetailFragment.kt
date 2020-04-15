package org.helpinout.billonlights.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_delete_confirmation.iv_expend_collapse
import kotlinx.android.synthetic.main.bottom_sheet_detail.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.utils.HELP_TYPE_REQUEST

class BottomSheetsDetailFragment(private val offerType: Int, private val name: String, private val detail: String) : BottomSheetDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, if (offerType == HELP_TYPE_REQUEST) R.style.BottomSheetThemeAskForHelp else R.style.BottomSheetThemeOfferHelp)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_name.text = name
        tv_condition.text = detail

        iv_expend_collapse.setOnClickListener {
            dismiss()
        }
    }
}