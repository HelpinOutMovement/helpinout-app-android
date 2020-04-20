package org.helpinout.billonlights.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_request_detail.*
import kotlinx.android.synthetic.main.fragment_my_requests.progress_bar
import kotlinx.android.synthetic.main.fragment_my_requests.recycler_view
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.MappingDetail
import org.helpinout.billonlights.model.database.entity.SuggestionRequest
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.view.adapters.RequestDetailAdapter
import org.helpinout.billonlights.view.fragments.BottomSheetRateReportFragmentForMapping
import org.helpinout.billonlights.view.fragments.BottomSheetsDeleteConfirmationFragment
import org.helpinout.billonlights.view.fragments.BottomSheetsDetailFragment
import org.helpinout.billonlights.view.view.DividerItemDecoration
import org.helpinout.billonlights.viewmodel.OfferViewModel
import org.jetbrains.anko.startActivity
import timber.log.Timber


class RequestDetailActivity : BaseActivity(), View.OnClickListener {

    private var activity_uuid: String = ""
    private var initiator: Int = 0
    private var offerType: Int = 0
    private var itemList = ArrayList<MappingDetail>()
    private var isFromNotification: Boolean = false

    lateinit var adapter: RequestDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        offerType = intent.getIntExtra(OFFER_TYPE, HELP_TYPE_REQUEST)
        initiator = intent.getIntExtra(INITIATOR, HELP_TYPE_REQUEST)
        activity_uuid = intent.getStringExtra(ACTIVITY_UUID) ?: ""
        isFromNotification = intent.getBooleanExtra(FROM_NOTIFICATION, false)

        if (offerType == HELP_TYPE_REQUEST) {
            if (initiator == HELP_TYPE_REQUEST) {//send request
                supportActionBar?.title = getString(R.string.request_send_to)
                layout_bottom.show()
            } else {//offer received
                supportActionBar?.title = getString(R.string.help_offers_received_from)
            }
        } else {
            tv_no_sender.text = getString(R.string.no_offer_sent)
            if (initiator == HELP_TYPE_OFFER) {//send offer
                supportActionBar?.title = getString(R.string.offer_send_to)
                btn_cancel_request.setText(R.string.cancel_this_offer)
                layout_bottom.show()
            } else {//request received from
                supportActionBar?.title = getString(R.string.help_request_received_from)
            }
        }
        btn_cancel_request.setOnClickListener(this)
        mRecyclerView
    }

    private val mRecyclerView by lazy {
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.setHasFixedSize(true)
        adapter = RequestDetailAdapter(itemList, onRateReportClick = { item -> onRateReportClick(item) }, onDeleteClick = { item -> onDeleteClick(item) }, onDetailClick = { name, detail, description -> onDetailClick(name, detail, description) }, onMakeCallClick = { parentUuid, activityUUid -> onMakeCallClick(parentUuid, activityUUid) })
        val divider = ContextCompat.getDrawable(this, R.drawable.line_divider)
        recycler_view.addItemDecoration(DividerItemDecoration(divider!!, 0, 0))
        recycler_view.adapter = adapter
        loadRequestDetails()
    }

    private fun loadRequestDetails() {
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.getRequestDetails(offerType, initiator, activity_uuid).observe(this, Observer { list ->
            progress_bar.hide()
            list?.let {
                itemList.clear()
                itemList.addAll(list)
            }
            recycler_view.goneIf(itemList.isEmpty())
            tv_no_sender.visibleIf(itemList.isEmpty())
            adapter.notifyDataSetChanged()
        })
    }

    private fun onRateReportClick(item: MappingDetail) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_TIME) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        if (item.parent_uuid.isNullOrEmpty()) {//for  search for help providers or search for help requester
            val suggestionData = SuggestionRequest()
            suggestionData.activity_uuid = item.activity_uuid ?: ""
            suggestionData.activity_category = item.activity_category ?: 0
            suggestionData.activity_type = item.activity_type ?: 0
            try {
                val latt = item.geo_location!!.split(",")
                suggestionData.latitude = latt[0].toDouble()
                suggestionData.longitude = latt[1].toDouble()
                suggestionData.accuracy = ""
            } catch (e: Exception) {
            }

            val suggestionDataAsString = Gson().toJson(suggestionData)
            startActivity<HelpProviderRequestersActivity>(SUGGESTION_DATA to suggestionDataAsString, HELP_TYPE to (item.activity_type ?: 0))
        } else {
            val rateReport = BottomSheetRateReportFragmentForMapping(item, onSubmitClick = { _, rating, recommendToOther, comments -> onSubmitClick(item, rating, recommendToOther, comments) })
            rateReport.show(supportFragmentManager, null)
        }
    }

    private fun onSubmitClick(item: MappingDetail, rating: String, recommendToOther: Int, comments: String) {
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.makeRating(item.parent_uuid, item.activity_uuid ?: "", offerType, rating, recommendToOther, comments).observe(this, Observer {
            if (it.first != null) {
                toastSuccess(R.string.rating_success)
            } else {
                if (!isNetworkAvailable()) {
                    toastError(R.string.toast_error_internet_issue)
                } else toastError(it.second)
            }
        })
    }

    private fun onDeleteClick(item: MappingDetail) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_TIME) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()

        val deleteDialog = BottomSheetsDeleteConfirmationFragment(item.activity_type!!, item.parent_uuid, item.activity_uuid ?: "", onDeleteYesClick = { uuid1, uuid2 -> onDeleteYesClick(uuid1, uuid2) })
        deleteDialog.show(supportFragmentManager, null)
    }

    private fun onDeleteYesClick(parent_uuid: String?, activity_uuid: String) {
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)

        if (parent_uuid.isNullOrEmpty()) {
            viewModel.deleteActivity(activity_uuid, offerType).observe(this, Observer {
                it.first?.let { delete ->
                    deleteActivityFromDatabase(delete.data?.activity_uuid)
                } ?: kotlin.run {
                    if (!isNetworkAvailable()) {
                        toastError(R.string.toast_error_internet_issue)
                    } else toastError(it.second)
                }
            })
        } else {
            viewModel.deleteMapping(parent_uuid, activity_uuid, offerType).observe(this, Observer {
                it.first?.let {
                    deleteMappingFromDatabase(parent_uuid, activity_uuid)
                } ?: kotlin.run {
                    if (!isNetworkAvailable()) {
                        toastError(R.string.toast_error_internet_issue)
                    } else toastError(it.second)
                }
            })

        }
    }

    private fun onMakeCallClick(parentUUid: String?, activity_uuid: String) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_TIME) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.makeCallTracking(parentUUid, activity_uuid, helpType).observe(this, Observer {
            if (it.first != null) Timber.d("Response success of make call api")
        })
    }

    private fun onDetailClick(name: String, detail: String, description: String) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_TIME) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val deleteDialog = BottomSheetsDetailFragment(offerType, name, detail, description)
        deleteDialog.show(supportFragmentManager, null)
    }

    private fun deleteMappingFromDatabase(parent_uuid: String?, activity_uuid: String) {
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.deleteMappingFromDatabase(parent_uuid, activity_uuid).observe(this, Observer {
            if (it) {
                toastSuccess(R.string.toast_delete_success)
                loadRequestDetails()
                val intent1 = Intent(DATA_REFRESH)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent1)
            }
        })
    }

    private fun deleteActivityFromDatabase(activityUuid: String?) {
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.deleteActivityFromDatabase(activityUuid).observe(this, Observer {
            if (it) {
                toastSuccess(R.string.toast_delete_success)
                val returnIntent = Intent()
                setResult(Activity.RESULT_OK, returnIntent)
                finishWithFade()
            }
        })
    }

    override fun onBackPressed() {
        if (isFromNotification) {
            goToHome()
        }
        super.onBackPressed()
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home) {
            if (isFromNotification) {
                goToHome()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun goToHome() {
        val intent = Intent(baseContext!!, HomeActivity::class.java)
        intent.putExtra(SELECTED_INDEX, helpType)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        overridePendingTransition(R.anim.enter, R.anim.exit)
        finishWithSlideAnimation()
    }

    override fun onClick(v: View?) {
        if (v == btn_cancel_request) {
            onDeleteYesClick(null, activity_uuid)
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_request_detail
    }
}
