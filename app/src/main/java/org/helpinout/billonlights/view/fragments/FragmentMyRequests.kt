package org.helpinout.billonlights.view.fragments

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import kotlinx.android.synthetic.main.fragment_my_requests.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.AddCategoryDbItem
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.view.adapters.RequestSentAdapter
import org.helpinout.billonlights.view.view.ItemOffsetDecoration
import org.helpinout.billonlights.viewmodel.OfferViewModel

class FragmentMyRequests : BaseFragment() {
    private var itemList = ArrayList<AddCategoryDbItem>()
    lateinit var adapter: RequestSentAdapter


    companion object {
        fun newInstance(type: Int, initiator: Int, helpType: Int): FragmentMyRequests {
            val myFragment = FragmentMyRequests()
            val args = Bundle()
            args.putInt(OFFER_TYPE, type)
            args.putInt(INITIATOR, initiator)
            args.putInt(HELP_TYPE, helpType)
            myFragment.arguments = args
            return myFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView
    }

    private val mRecyclerView by lazy {
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.setHasFixedSize(true)

        val offerType = arguments?.getInt(OFFER_TYPE, 0) ?: 0

        if (offerType == HELP_TYPE_OFFER) {
            tv_no_data_available.text = getString(R.string.no_offer_sended)
        }
        val initiator = arguments?.getInt(INITIATOR, 0) ?: 0
        val helpType = arguments?.getInt(HELP_TYPE, 0) ?: 0
        adapter = RequestSentAdapter(offerType, initiator, helpType, itemList, onRateReportClick = { item -> onRateReportClick(item) }, onSendRequestClick = { offer, initiat, helpType, item -> onSendRequestClick(offerType, initiator, helpType, item) }, onOffersClick = { offer, initiat, helpType, item -> onOffersClick(offerType, initiator, helpType, item) })
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
            try {
                progress_bar.hide()
                list?.let {
                    itemList.clear()
                    itemList.addAll(list.reversed())
                }
                recycler_view.goneIf(itemList.isEmpty())
                tv_no_data_available.visibleIf(itemList.isEmpty())
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == activityResult) {
            loadRequestList()
        }
    }

    override fun onStart() {
        super.onStart()
        activity?.let {
            val filter = IntentFilter()
            filter.addAction(DATA_REFRESH)
            LocalBroadcastManager.getInstance(it).registerReceiver(uploadStatusReceiver, filter)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        activity?.let {
            LocalBroadcastManager.getInstance(it).unregisterReceiver(uploadStatusReceiver)
        }
    }


    private val uploadStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == DATA_REFRESH) {
                checkOfferList()
            }
        }
    }

    private fun checkOfferList() {
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.getUserRequestOfferList(activity!!, 0).observe(this, Observer {
            loadRequestList()
        })
    }
}