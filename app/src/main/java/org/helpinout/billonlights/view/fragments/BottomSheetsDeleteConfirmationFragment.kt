package org.helpinout.billonlights.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_delete_confirmation.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.AddCategoryDbItem

class BottomSheetsDeleteConfirmationFragment(private val item: AddCategoryDbItem, private val onDeleteYesClick: (AddCategoryDbItem) -> Unit) : BottomSheetDialogFragment() {
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
            onDeleteYesClick(item)
        }
        button_no.setOnClickListener {
            dismiss()
        }

    }
}