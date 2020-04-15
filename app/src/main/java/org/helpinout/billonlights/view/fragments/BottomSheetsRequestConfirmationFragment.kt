package org.helpinout.billonlights.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_delete_confirmation.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.utils.HELP_TYPE_OFFER
import org.helpinout.billonlights.utils.HELP_TYPE_REQUEST

class BottomSheetsRequestConfirmationFragment(private val helpType: Int, private val onYesClick: () -> Unit, private val onNoClick: () -> Unit) : BottomSheetDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, if (helpType == HELP_TYPE_REQUEST) R.style.BottomSheetThemeAskForHelp else R.style.BottomSheetThemeOfferHelp)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_request_confirmation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (helpType == HELP_TYPE_OFFER) {
            tv_title.setText(R.string.offer_confirmation)
            button_yes.setBackgroundResource(R.drawable.accent_revert_border_background)
            button_no.setBackgroundResource(R.drawable.accent_border_background)
            button_no.setTextColor(resources.getColor(R.color.colorAccent))
        }

        button_yes.setOnClickListener {
            dismiss()
            onYesClick()
        }
        button_no.setOnClickListener {
            dismiss()
            onNoClick()
        }
    }
}