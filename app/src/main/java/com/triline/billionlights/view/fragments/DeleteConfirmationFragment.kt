package com.triline.billionlights.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.triline.billionlights.R
import com.triline.billionlights.model.database.entity.Offer
import com.triline.billionlights.model.database.entity.OfferReceived
import com.triline.billionlights.model.database.entity.RequestSent
import kotlinx.android.synthetic.main.layout_delete_confirmation.*

class DeleteConfirmationFragment : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_delete_confirmation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iv_expend_collapse.setOnClickListener {
            dismiss()
        }
        arguments?.let {
            val data = it.getString("RequestSent")!!
            val item = Gson().fromJson(data, Offer::class.java)
            if (item is RequestSent) {

            }
            if (item is OfferReceived) {

            }
            button_yes.setOnClickListener {

            }
            button_no.setOnClickListener {

            }
        }
    }

    companion object {
        fun newInstance(item: Offer): DeleteConfirmationFragment {
            val delete = DeleteConfirmationFragment()
            val args = Bundle()
            val data = Gson().toJson(item)
            args.putString("RequestSent", data)
            delete.arguments = args
            return delete
        }
    }
}