package org.helpinout.billonlights.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_request_offer_detail.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.AddCategoryDbItem
import org.helpinout.billonlights.utils.*

class BottomSheetsRequestOfferDetailFragment(private val offerType: Int, private val item: AddCategoryDbItem, private val onCancelRequestClick: (String, Int) -> Unit) : BottomSheetDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, if (offerType == HELP_TYPE_REQUEST) R.style.BottomSheetThemeAskForHelp else R.style.BottomSheetThemeOfferHelp)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_request_offer_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (offerType == HELP_TYPE_REQUEST) {
            if (item.activity_category == CATEGORY_AMBULANCE) {
                tv_notes.show()
                tv_notes.text = getString(R.string.note, " " + item.conditions)
            } else tv_notes.hide()

            tv_time.show()
            free_or_paid.text = getString(if (item.pay == 1) R.string.can_pay else R.string.can_not_pay)
        } else {
            tv_notes.text = getString(R.string.note)+ " "+ item.conditions
            tv_notes.show()
            free_or_paid.text = getString(if (item.pay == 1) R.string.not_free else R.string.free)
            btn_cancel_this_request.text = getString(R.string.cancel_this_offer)
        }
        tv_time.text = item.date_time.displayTime()
        iv_cancel.setOnClickListener {
            dismiss()
        }
        btn_cancel_this_request.setOnClickListener {
            dismiss()
            onCancelRequestClick(item.activity_uuid, offerType)
        }
        item_name.text = item.name

        tv_detail.text = item.detail?.fromHtml()

    }
}