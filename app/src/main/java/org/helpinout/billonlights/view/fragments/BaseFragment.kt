package org.helpinout.billonlights.view.fragments

import android.os.SystemClock
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.AddCategoryDbItem
import org.helpinout.billonlights.model.database.entity.SuggestionRequest
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.view.activity.HelpProviderRequestersActivity
import org.helpinout.billonlights.view.activity.RequestDetailActivity
import org.helpinout.billonlights.viewmodel.OfferViewModel
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

abstract class BaseFragment : Fragment() {
    var mLastClickTime: Long = 0
    val activityResult = 33
    fun onRateReportClick(item: AddCategoryDbItem) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_TIME) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()

        if (item.parent_uuid.isNullOrEmpty()) {//for  search for help providers or search for help requester
            val suggestionData = SuggestionRequest()
            suggestionData.activity_uuid = item.activity_uuid
            suggestionData.activity_category = item.activity_category
            suggestionData.activity_type = item.activity_type
            try {
                val latt = item.geo_location!!.split(",")
                suggestionData.latitude = latt[0].toDouble()
                suggestionData.longitude = latt[1].toDouble()
                suggestionData.accuracy = ""
            } catch (e: Exception) {
            }
            val suggestionDataAsString = Gson().toJson(suggestionData)
            activity!!.startActivity<HelpProviderRequestersActivity>(SUGGESTION_DATA to suggestionDataAsString, HELP_TYPE to item.activity_type)
        } else {
            val rateReport = BottomSheetRateReportFragment(item, onSubmitClick = { _, rating, recommendToOther, comments -> onSubmitClick(item, rating, recommendToOther, comments) })
            rateReport.show(childFragmentManager, null)
        }
    }

    private fun onSubmitClick(item: AddCategoryDbItem, rating: String, recommendToOther: Int, comments: String) {
        val dialog = activity?.indeterminateProgressDialog(R.string.alert_msg_please_wait)
        dialog?.show()
        val offerType = arguments?.getInt(OFFER_TYPE, 0) ?: 0
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.makeRating(item.parent_uuid, item.activity_uuid, offerType, rating, recommendToOther, comments).observe(this, Observer {
            dialog?.dismiss()
            if (it.first != null) {
                toastSuccess(R.string.rating_success)
            } else {
                toastError(it.second)
            }
        })
    }

    fun onSendRequestClick(offerType: Int, initiator: Int, helpType: Int, item: AddCategoryDbItem) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_TIME) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        activity?.startActivityForResult<RequestDetailActivity>(activityResult, OFFER_TYPE to offerType, INITIATOR to initiator, HELP_TYPE to helpType, ACTIVITY_UUID to (item.activity_uuid ?: ""))
        activity?.overridePendingTransition(R.anim.enter, R.anim.exit)
    }

    fun onOffersClick(offerType: Int, initiator: Int, helpType: Int, item: AddCategoryDbItem) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_TIME) {
            return
        }
        val initiat = if (initiator == 1) 2 else 1
        mLastClickTime = SystemClock.elapsedRealtime()
        activity?.startActivityForResult<RequestDetailActivity>(activityResult, OFFER_TYPE to offerType, INITIATOR to initiat, HELP_TYPE to helpType, ACTIVITY_UUID to (item.activity_uuid ?: ""))
        activity?.overridePendingTransition(R.anim.enter, R.anim.exit)
    }

    abstract fun loadRequestList()

}