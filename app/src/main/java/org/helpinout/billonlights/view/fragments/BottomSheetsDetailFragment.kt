package org.helpinout.billonlights.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_delete_confirmation.iv_expend_collapse
import kotlinx.android.synthetic.main.bottom_sheet_detail.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.utils.*

class BottomSheetsDetailFragment(private val type: Int, private val offerType: Int, private val name: String, private val detail: String, private val description: String, private val pay: Int, private val self_else: Int) : BottomSheetDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, if (offerType == HELP_TYPE_REQUEST) R.style.BottomSheetThemeAskForHelp else R.style.BottomSheetThemeOfferHelp)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (offerType == HELP_TYPE_REQUEST) {
            note.show()
            divider1.show()
            tv_items.text = (getString(R.string.can_help_with) + "<br/>" + description.replace("%1s", getString(R.string.volunteers)).replace("%2s", getString(R.string.technical_personnel))).fromHtml()
            free_or_paid.text = getString(if (pay == 1) R.string.not_free else R.string.free)
        } else {
            tv_items.text = (getString(R.string.need_help_with) + "<br/>" + description.replace("%1s", getString(R.string.volunteers)).replace("%2s", getString(R.string.technical_personnel))).fromHtml()
            free_or_paid.text = getString(if (pay == 1) R.string.can_pay else R.string.can_not_pay)
            tv_help_for.visibleIf(self_else == 2)
        }

        if (type == CATEGORY_PEOPLE) {
            note.hide()
            divider1.hide()
        }
        if (type == CATEGORY_AMBULANCE) {
            note.show()
            divider1.show()
        }
        tv_name.text = name

        tv_condition.text = detail

        iv_expend_collapse.setOnClickListener {
            dismiss()
        }
    }
}