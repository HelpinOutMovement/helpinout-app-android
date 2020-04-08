package com.triline.billionlights.view.fragments

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import com.triline.billionlights.R
import com.triline.billionlights.model.database.entity.RequestSent
import com.triline.billionlights.utils.DOUBLE_CLICK_TIME
import com.triline.billionlights.utils.OFFER_TYPE
import com.triline.billionlights.utils.goneIf
import com.triline.billionlights.utils.visibleIf
import com.triline.billionlights.view.adapters.RequestSentAdapter
import com.triline.billionlights.view.view.ItemOffsetDecoration
import com.triline.billionlights.viewmodel.OfferViewModel
import kotlinx.android.synthetic.main.fragment_offers.*

class RequestSentFragment : Fragment() {
    private var itemList = ArrayList<RequestSent>()

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
        val offerType = arguments?.getInt(OFFER_TYPE, 0) ?: 0
        loadRequestList(offerType)
    }

    @Synchronized
    private fun loadRequestList(offerType: Int) {
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.getRequestSent(offerType).observe(this, Observer { list ->
            progress_bar.hide()
            list?.let {
                itemList.clear()
                itemList.addAll(list)
            }
            recycler_view.goneIf(itemList.isEmpty())
            tv_no_data_available.visibleIf(itemList.isEmpty())
        })
    }

    private fun onRateReportClick(item: RequestSent) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_TIME) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val rateReport = BottomSheetRateReportFragment.newInstance(item, true)
        rateReport.show(childFragmentManager, null)
    }

    private fun onDeleteClick(item: RequestSent) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_TIME) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()

        val deleteDialog = BottomSheetsDeleteConfirmationFragment.newInstance(item)
        deleteDialog.show(childFragmentManager, null)
    }
}