package org.helpinout.billonlights.view.fragments

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import kotlinx.android.synthetic.main.fragment_offers.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.AddCategoryDbItem
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.view.adapters.RequestSentAdapter
import org.helpinout.billonlights.view.view.ItemOffsetDecoration
import org.helpinout.billonlights.viewmodel.OfferViewModel

class RequestSentFragment : Fragment() {
    private var itemList = ArrayList<AddCategoryDbItem>()

    lateinit var adapter: RequestSentAdapter
    private var mLastClickTime: Long = 0

    companion object {
        fun newInstance(type: Int): RequestSentFragment {
            val myFragment = RequestSentFragment()
            val args = Bundle()
            args.putInt(OFFER_TYPE, type)
            myFragment.arguments = args
            return myFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_offers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView
    }

    private val mRecyclerView by lazy {
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.setHasFixedSize(true)

        adapter = RequestSentAdapter(itemList, onRateReportClick = { item -> onRateReportClick(item) }, onDeleteClick = { item -> onDeleteClick(item) })
        val itemDecorator = ItemOffsetDecoration(activity!!, R.dimen.item_offset)
        recycler_view.addItemDecoration(itemDecorator)
        recycler_view.adapter = adapter
        loadRequestList()
    }

    private fun loadRequestList() {
        val offerType = arguments?.getInt(OFFER_TYPE, 0) ?: 0
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.getMyRequestsOrOffers(offerType, activity!!).observe(this, Observer { list ->
            progress_bar.hide()
            list?.let {
                itemList.clear()
                itemList.addAll(list.reversed())
            }
            recycler_view.goneIf(itemList.isEmpty())
            tv_no_data_available.visibleIf(itemList.isEmpty())
            adapter.notifyDataSetChanged()
        })
    }

    private fun onRateReportClick(item: AddCategoryDbItem) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_TIME) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val rateReport = BottomSheetRateReportFragment(item, true)
        rateReport.show(childFragmentManager, null)
    }

    private fun onDeleteClick(item: AddCategoryDbItem) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_TIME) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()

        val deleteDialog = BottomSheetsDeleteConfirmationFragment(item.activity_uuid ?: "", onDeleteYesClick = { uuid -> onDeleteYesClick(uuid) })
        deleteDialog.show(childFragmentManager, null)
    }

    private fun onDeleteYesClick(uuid: String) {
        val offerType = arguments?.getInt(OFFER_TYPE, 0) ?: 0
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.deleteActivity(uuid, offerType).observe(this, Observer { it ->
            it.first?.let { delete ->
                deleteDataFromDatabase(delete.data?.activity_uuid)
            } ?: kotlin.run {
                toastError(R.string.toast_error_some_error)
            }

        })
    }

    private fun deleteDataFromDatabase(activityUuid: String?) {
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.deleteActivityFromDatabase(activityUuid).observe(this, Observer {
            if (it) {
                toastSuccess(R.string.toast_delete_success)
                loadRequestList()
            }
        })
    }
}