package org.helpinout.billonlights.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import kotlinx.android.synthetic.main.fragment_offers.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.AddCategoryDbItem
import org.helpinout.billonlights.utils.INITIATOR
import org.helpinout.billonlights.utils.OFFER_TYPE
import org.helpinout.billonlights.utils.goneIf
import org.helpinout.billonlights.utils.visibleIf
import org.helpinout.billonlights.view.adapters.OfferReceivedAdapter
import org.helpinout.billonlights.view.view.ItemOffsetDecoration
import org.helpinout.billonlights.viewmodel.OfferViewModel

class OffersReceivedFragment : BaseFragment() {
    private var itemList = ArrayList<AddCategoryDbItem>()

    lateinit var adapter: OfferReceivedAdapter

    companion object {
        fun newInstance(type: Int, initiator: Int): OffersReceivedFragment {
            val myFragment = OffersReceivedFragment()
            val args = Bundle()
            args.putInt(OFFER_TYPE, type)
            args.putInt(INITIATOR, initiator)
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
        loadRequestList()
    }

    override fun loadRequestList() {
        val offerType = arguments?.getInt(OFFER_TYPE, 0) ?: 0
        val initiator = arguments?.getInt(INITIATOR, 0) ?: 0
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.getMyRequestsOrOffers(offerType, initiator, activity!!).observe(this, Observer { list ->
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
}