package org.helpinout.billonlights.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_delete_confirmation.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.utils.HELP_TYPE_REQUEST

class BottomSheetsDeleteConfirmationFragment(private val mapping_initiator: Int, private val activityType: Int, private val parent_uuid: String?, private val activity_uuid: String, private val onDeleteYesClick: (String?, String, Int) -> Unit) : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, if (activityType == HELP_TYPE_REQUEST) R.style.BottomSheetThemeAskForHelp else R.style.BottomSheetThemeOfferHelp)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_delete_confirmation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iv_expend_collapse.setOnClickListener {
            dismiss()
        }

        button_yes.setOnClickListener {
            dismiss()
            onDeleteYesClick(parent_uuid, activity_uuid, mapping_initiator)
        }
        button_no.setOnClickListener {
            dismiss()
        }
    }
}