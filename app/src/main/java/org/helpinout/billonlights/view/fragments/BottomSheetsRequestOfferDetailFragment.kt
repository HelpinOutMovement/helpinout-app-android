package org.helpinout.billonlights.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_request_offer_detail.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.utils.HELP_TYPE_REQUEST
import org.helpinout.billonlights.utils.displayTime
import org.helpinout.billonlights.utils.fromHtml
import org.helpinout.billonlights.utils.show

class BottomSheetsRequestOfferDetailFragment(private val offerType: Int, private val itemName: String, private val description: String, private val pay: Int, private val time: String, private val uuid:String,private val onCancelRequestClick: (String,Int) -> Unit) : BottomSheetDialogFragment() {
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
            tv_time.show()
            free_or_paid.text = getString(if (pay == 1) R.string.can_pay else R.string.can_not_pay)

        } else {
            free_or_paid.text = getString(if (pay == 1) R.string.not_free else R.string.free)
            btn_cancel_this_request.text = getString(R.string.cancel_this_offer)
        }
        tv_time.text = time.displayTime()
        iv_cancel.setOnClickListener {
            dismiss()
        }
        btn_cancel_this_request.setOnClickListener {
            dismiss()
            onCancelRequestClick(uuid,offerType)
        }
        item_name.text = itemName

        tv_detail.text = description.fromHtml()

    }
}