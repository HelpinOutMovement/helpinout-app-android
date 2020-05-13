package org.helpinout.billonlights.view.fragments

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_my_requests.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.AddCategoryDbItem
import org.helpinout.billonlights.model.database.entity.SuggestionRequest
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.view.activity.HelpProviderRequestersActivity
import org.helpinout.billonlights.view.activity.HomeActivity
import org.helpinout.billonlights.view.activity.RequestDetailActivity
import org.helpinout.billonlights.view.adapters.RequestSentAdapter
import org.helpinout.billonlights.view.view.ItemOffsetDecoration
import org.helpinout.billonlights.viewmodel.OfferViewModel
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

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
        adapter = RequestSentAdapter(offerType, initiator, helpType, itemList, { item -> onSearchForHelpProviderClick(item) }, { offer, item -> onViewDetailClick(offerType, item) }, { offer, initiat, help, item -> onNewMatchesClick(offerType, initiator, help, item) }, { a, b, help, uuid -> onRequestSentClick(a, b, help, uuid) })
        val itemDecorator = ItemOffsetDecoration(activity!!, R.dimen.item_offset)
        recycler_view.addItemDecoration(itemDecorator)
        recycler_view.adapter = adapter
        loadRequestList()
    }


    private fun loadRequestList() {
        val offerType = arguments?.getInt(OFFER_TYPE, 0) ?: 0
        val initiator = arguments?.getInt(INITIATOR, 0) ?: 0
        val helpType = arguments?.getInt(HELP_TYPE, 0) ?: 0
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.getMyRequestsOrOffers(offerType, initiator).observe(this, Observer { list ->
            try {
                progress_bar.hide()
                list?.let {
                    itemList.clear()
                    itemList.addAll(list.reversed())
                }
                if (helpType == HELP_TYPE_OFFER) {
                    (activity as HomeActivity).hideMailIcon(itemList.isNotEmpty())
                }
                recycler_view.goneIf(itemList.isEmpty())
                tv_no_data_available.visibleIf(itemList.isEmpty())
                adapter.notifyDataSetChanged()

                if (itemList.isNotEmpty()) getNewMatchesDetail(helpType)

            } catch (e: Exception) {

            }
        })
    }

    private fun getNewMatchesDetail(activity_type: Int) {
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.getNewMatchesResponse(activity_type).observe(this, Observer {
            if (it.first != null) {
                if (activity_type== HELP_TYPE_OFFER){
                    it.first!!.data?.offers?.forEach { request ->
                        val item = itemList.find {
                            it.activity_uuid == request.activity_uuid
                        }
                        if (item != null) {
                            item.newMatchesCount = request.new_matches
                        }
                    }
                }else{
                    it.first!!.data?.requests?.forEach { request ->
                        val item = itemList.find {
                            it.activity_uuid == request.activity_uuid
                        }
                        if (item != null) {
                            item.newMatchesCount = request.new_matches
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun onRequestSentClick(offerType: Int, initiator: Int, helpType: Int, activity_uuid: String) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_TIME) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        activity?.startActivityForResult<RequestDetailActivity>(activityResult, OFFER_TYPE to offerType, INITIATOR to initiator, HELP_TYPE to helpType, ACTIVITY_UUID to activity_uuid)
        activity?.overridePendingTransition(R.anim.enter, R.anim.exit)
    }

    private fun onSearchForHelpProviderClick(item: AddCategoryDbItem) {
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
        }
    }

    private fun onNewMatchesClick(offerType: Int, initiator: Int, helpType: Int, item: AddCategoryDbItem) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_TIME) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }


    private fun onViewDetailClick(offerType: Int, item: AddCategoryDbItem) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_TIME) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val rateReport = BottomSheetsRequestOfferDetailFragment(offerType, item, onCancelRequestClick = { id, activity_type -> onCancelRequestClick(id, activity_type) })
        rateReport.show(childFragmentManager, null)
    }

    private fun onCancelRequestClick(activity_uuid: String, activity_type: Int) {
        val dialog = activity?.indeterminateProgressDialog(R.string.alert_msg_please_wait)
        dialog?.setCancelable(false)
        dialog?.show()
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.deleteActivity(activity_uuid, activity_type).observe(this, Observer {
            dialog?.dismiss()
            it.first?.let {
                toastSuccess(R.string.toast_delete_success)
                (activity as HomeActivity).refreshBedge()
                loadRequestList()
            } ?: kotlin.run {
                if (!isNetworkAvailable()) {
                    toastError(R.string.toast_error_internet_issue)
                } else toastError(it.second)
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
            filter.addAction(BEDGE_REFRESH)
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
            if (intent?.action==BEDGE_REFRESH){
                loadRequestList()
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