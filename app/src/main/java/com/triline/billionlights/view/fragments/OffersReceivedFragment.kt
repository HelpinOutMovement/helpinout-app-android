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
import com.triline.billionlights.model.database.entity.OfferReceived
import com.triline.billionlights.utils.DOUBLE_CLICK_TIME
import com.triline.billionlights.utils.OFFER_TYPE
import com.triline.billionlights.utils.goneIf
import com.triline.billionlights.utils.visibleIf
import com.triline.billionlights.view.adapters.OfferReceivedAdapter
import com.triline.billionlights.view.view.ItemOffsetDecoration
import com.triline.billionlights.viewmodel.OfferViewModel
import kotlinx.android.synthetic.main.fragment_offers.*

class OffersReceivedFragment : Fragment() {
    private var itemList = ArrayList<OfferReceived>()
    private var mLastClickTime: Long = 0

    lateinit var adapter: OfferReceivedAdapter

    companion object {
        fun newInstance(type: Int): OffersReceivedFragment {
            val myFragment = OffersReceivedFragment()
            val args = Bundle()
            args.putInt(OFFER_TYPE, type)
            myFragment.arguments = args
            return myFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_offers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView
    }

    private val mRecyclerView by lazy {
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.setHasFixedSize(true)
        adapter = OfferReceivedAdapter(
            itemList,
            onRateReportClick = { item -> onRateReportClick(item) },
            onDeleteClick = { item -> onDeleteClick(item) })
        val itemDecorator = ItemOffsetDecoration(activity!!, R.dimen.item_offset)
        recycler_view.addItemDecoration(itemDecorator)
        recycler_view.adapter = adapter
        val offerType = arguments?.getInt(OFFER_TYPE, 0) ?: 0
        loadOfferItemList(offerType)
    }

    @Synchronized
    private fun loadOfferItemList(offerType: Int) {
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.getReceivedOffers(offerType).observe(this, Observer { list ->
            progress_bar.hide()
            list?.let {
                itemList.clear()
                itemList.addAll(list)
            }
            recycler_view.goneIf(itemList.isEmpty())
            tv_no_data_available.visibleIf(itemList.isEmpty())
            adapter.notifyDataSetChanged()
        })
    }

    private fun onRateReportClick(item: OfferReceived) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_TIME) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()

        val rateReport = RateReportFragment.newInstance(item, false)
        rateReport.show(childFragmentManager, null)
    }

    private fun onDeleteClick(item: OfferReceived) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_TIME) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val deleteDialog = DeleteConfirmationFragment.newInstance(item)
        deleteDialog.show(childFragmentManager, "Delete Dialog")
    }
}