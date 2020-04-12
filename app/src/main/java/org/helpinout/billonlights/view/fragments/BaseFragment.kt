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
import org.helpinout.billonlights.viewmodel.OfferViewModel
import org.jetbrains.anko.startActivity

abstract class BaseFragment : Fragment() {
    var mLastClickTime: Long = 0

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
                suggestionData.latitude = latt[0]
                suggestionData.longitude = latt[1]
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

    fun onSubmitClick(item: AddCategoryDbItem, rating: String, recommendToOther: Int, comments: String) {
        val offerType = arguments?.getInt(OFFER_TYPE, 0) ?: 0
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.makeRating(item, offerType, rating, recommendToOther, comments).observe(this, Observer {
            if (it.first != null) {
                toastSuccess(R.string.rating_success)
            }
        })
    }

    fun onDeleteClick(item: AddCategoryDbItem) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_TIME) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()

        val deleteDialog = BottomSheetsDeleteConfirmationFragment(item, onDeleteYesClick = { uuid -> onDeleteYesClick(uuid) })
        deleteDialog.show(childFragmentManager, null)
    }

    fun onDeleteYesClick(item: AddCategoryDbItem) {
        val offerType = arguments?.getInt(OFFER_TYPE, 0) ?: 0
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)

        if (item.parent_uuid.isNullOrEmpty()) {
            viewModel.deleteActivity(item.activity_uuid, offerType).observe(this, Observer {
                it.first?.let { delete ->
                    deleteActivityFromDatabase(delete.data?.activity_uuid)
                } ?: kotlin.run {
                    if (!activity!!.isNetworkAvailable()) {
                        toastError(R.string.toast_error_internet_issue)
                    } else toastError(it.second)
                }

            })
        } else {
            viewModel.deleteMapping(item, offerType).observe(this, Observer {
                it.first?.let { delete ->
                    deleteMappingFromDatabase(item)
                } ?: kotlin.run {
                    if (!activity!!.isNetworkAvailable()) {
                        toastError(R.string.toast_error_internet_issue)
                    } else toastError(it.second)
                }
            })
        }
    }

    fun deleteActivityFromDatabase(activityUuid: String?) {
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.deleteActivityFromDatabase(activityUuid).observe(this, Observer {
            if (it) {
                toastSuccess(R.string.toast_delete_success)
                loadRequestList()
            }
        })
    }

    abstract fun loadRequestList()

    fun deleteMappingFromDatabase(item: AddCategoryDbItem) {
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.deleteMappingFromDatabase(item).observe(this, Observer {
            if (it) {
                toastSuccess(R.string.toast_delete_success)
                loadRequestList()
            }
        })
    }
}