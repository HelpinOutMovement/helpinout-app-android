package org.helpinout.billonlights.view.fragments

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import kotlinx.android.synthetic.main.fragment_offers.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.AddItem
import org.helpinout.billonlights.model.database.entity.OfferReceived
import org.helpinout.billonlights.utils.DOUBLE_CLICK_TIME
import org.helpinout.billonlights.utils.OFFER_TYPE
import org.helpinout.billonlights.utils.goneIf
import org.helpinout.billonlights.utils.visibleIf
import org.helpinout.billonlights.view.adapters.OfferReceivedAdapter
import org.helpinout.billonlights.view.view.ItemOffsetDecoration
import org.helpinout.billonlights.viewmodel.OfferViewModel

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
        val offerType = arguments?.getInt(OFFER_TYPE, 0) ?: 0
        adapter = OfferReceivedAdapter(offerType, itemList, onRateReportClick = { item -> onRateReportClick(item) }, onDeleteClick = { item -> onDeleteClick(item) })
        val itemDecorator = ItemOffsetDecoration(activity!!, R.dimen.item_offset)
        recycler_view.addItemDecoration(itemDecorator)
        recycler_view.adapter = adapter
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

//        val rateReport = BottomSheetRateReportFragment(item, false)
//        rateReport.show(childFragmentManager, null)
    }

    private fun onDeleteClick(item: OfferReceived) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_TIME) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
//        val deleteDialog = BottomSheetsDeleteConfirmationFragment(item., onDeleteYesClick = { onDeleteYesClick(item) })
//        deleteDialog.show(childFragmentManager, null)
    }

    private fun onDeleteYesClick(item: AddItem) {
        val offerType = arguments?.getInt(OFFER_TYPE, 0) ?: 0
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.deleteActivity(item.activity_uuid ?: "", offerType).observe(this, Observer {
            Log.d("", "")
        })
    }
}